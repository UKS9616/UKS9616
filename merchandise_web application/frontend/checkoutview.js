import React, { useState, useEffect } from 'react';
import axios from 'axios';


export function CheckoutView({ cart, navigateToHome }) {
    const [cartItems, setCartItems] = useState(cart);
    const [totalPrice, setTotalPrice] = useState('');
    const [fullName, setFullName] = useState('');
    const [personData, setPersonData] = useState(null);
    const [confirmAddress, setConfirmAddress] = useState(false);
    const [deliveryAddress, setDeliveryAddress] = useState('');

    useEffect(() => {
        // Calculate total price whenever cart items change
        calculateTotalPrice();
    }, [cartItems]);

    useEffect(() => {
        if (fullName !== '') {
            fetchPersonData();
        }
    }, [fullName]);

    const handleDeleteItem = (itemId) => {
        // Filter out the item with the provided itemId and update the cartItems state
        const updatedCartItems = cartItems.filter(item => item.id !== itemId);
        setCartItems(updatedCartItems);
    };

    const calculateTotalPrice = () => {
        let total = 0;
        cartItems.forEach(item => {
            total += item.price;
        });
        // Add 10% tax
        total *= 1.1;
        setTotalPrice(total.toFixed(2)); // Round to 2 decimal places
    };

    const handleOrderClick = () => {
        const inputFullName = prompt("Please enter your full name:");
        if (inputFullName) {
            setFullName(inputFullName);
        }
    };

    const fetchPersonData = async () => {
        try {
            const response = await axios.get(`http://localhost:8081/person/${fullName}`);
            setPersonData(response.data);
            setDeliveryAddress(`${response.data.address}, ${response.data.city}, ${response.data.state} ${response.data.zip}`);
            setConfirmAddress(true);
        } catch (error) {
            console.error('Error fetching person data:', error);
            alert('Person not found. Please enter a valid full name.');
        }
    };

    const handleAddressConfirmation = () => {
        const confirm = window.confirm("Is this your correct address?\n" +
            `Name: ${personData.name}\n` +
            `Address: ${personData.address}\n` +
            `City: ${personData.city}\n` +
            `State: ${personData.state}\n` +
            `Zip Code: ${personData.zip}`);
        if (confirm) {
            alert(`Your order will be delivered to:\n${deliveryAddress}`);
            // Call the function to navigate to the home page
            navigateToHome();

        } else {
            alert('Please navigate to the Update/Modify page to make changes');
        }
    };

    return (
        <div className="container">
            <h2>Cart</h2>
            {cartItems.length === 0 ? (
                <p>Your cart is empty</p>
            ) : (
                <div>
                    {cartItems.map(item => (
                        <div key={item.id} className="card mb-3">
                            <div className="row g-0">
                                <div className="col-md-4">
                                    <img src={item.image} className="img-fluid rounded-start" alt={item.name} />
                                </div>
                                <div className="col-md-8">
                                    <div className="card-body">
                                        <h5 className="card-title">{item.name}</h5>
                                        <p className="card-text">Price: {item.price}</p>
                                        {/* Add a delete button */}
                                        <button onClick={() => handleDeleteItem(item.id)} className="btn btn-danger">
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                    {/* Display total price with tax */}
                    <div className="row mt-3">
                        <div className="col-md-12">
                            <h4>Total Price: ${totalPrice}</h4>
                        </div>
                        <button className="btn btn-danger col-md-12" onClick={handleOrderClick}>
                            Order
                        </button>
                    </div>
                    {confirmAddress && (
                        <div className="row mt-3">
                            <div className="col-md-12">
                                <p>Delivery Address: {deliveryAddress}</p>
                                <button className="btn btn-primary col-md-12" onClick={handleAddressConfirmation}>
                                    Confirm Address
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}
