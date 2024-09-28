// WatchView.js
import React, { useState, useEffect } from 'react';

export function WatchView({ onCheckoutClick, updateCart }) {
  const [watches, setWatches] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredWatches, setFilteredWatches] = useState([]);
  const [cart, setCart] = useState([]); // State variable to store the cart items

  useEffect(() => {
    fetchWatches();
  }, []);

  const fetchWatches = async () => {
    try {
      const response = await fetch('http://localhost:8081/watches');
      const data = await response.json();
      setWatches(data);
      setFilteredWatches(data); // Initialize filteredWatches with all watches
    } catch (error) {
      console.error('Error fetching watches:', error);
    }
  };

  const handleSearch = () => {
    const filtered = watches.filter(watch =>
      watch.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredWatches(filtered);
  };

  const handleAddToCart = watchId => {

    const selectedWatch = watches.find(watch => watch.id === watchId);
    if (selectedWatch) {
      const newCart = [...cart, selectedWatch]; // New cart array with the selected watch added
      setCart([...cart, selectedWatch]);
      updateCart(newCart); // Pass the updated cart to App.js
    }
  };

  const handleRemoveFromCart = watchId => {

    setCart(cart.filter(item => item.id !== watchId));
  };

  const handleCheckoutClick = () => {

    onCheckoutClick(); // Call the onCheckoutClick function passed from App.js
  };

  return (
    <div className="container">
      {/* Search by title */}
      <div className="mt-4">
        <h2>Search</h2>
        <div className="input-group mb-3">
          <input
            type="text"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            className="form-control"
            placeholder="Enter watch name"
          />
          <button onClick={handleSearch} className="btn btn-primary">
            Search
          </button>
        </div>
      </div>

      <div className="row">
        {filteredWatches.map(watch => (
          <div key={watch.id} className="col-md-4 mb-4">
            <div className="card">
              <img
                src={watch.image}
                className="card-img-top"
                alt={watch.name}
              />
              <div className="card-body">
                <h5 className="card-title">{watch.name}</h5>
                <p className="card-text">Price: ${watch.price}</p>
                {/* Display the number of items selected for this watch */}
                <p className="card-text">
                  Selected: {cart.filter(item => item.id === watch.id).length}
                </p>
                <p className="card-text">{watch.description}</p>
                {/* Buttons to add and remove the watch from the cart */}
                <button onClick={() => handleAddToCart(watch.id)} className="btn btn-primary "> + </button>
                <button
                  onClick={() => handleRemoveFromCart(watch.id)}
                  className="btn btn-danger"
                >
                  -
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      <button
        className="btn btn-light bg-warning container-fluid d-flex justify-content-center mt-2" onClick={handleCheckoutClick}> Checkout </button>
      {/* if checkout is Clicked is true then, navigate to page, checkoutview.js and make sure to pass in props like all selected watches which should have names, image, description and price. */}
    </div>
  );
}
