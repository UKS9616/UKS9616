import React, { useState } from "react";
import axios from "axios"; // Import Axios for making HTTP requests

const PersonView = () => {
    const [name, setName] = useState(""); // State variable to store the name
    const [email, setEmail] = useState(""); // Store the email
    const [card, setCard] = useState(""); // Store the card info
    const [address, setAddress] = useState(""); // Store the address
    const [city, setCity] = useState(""); // Store the city
    const [state, setState] = useState(""); // Store the state
    const [zip, setZip] = useState(""); // Store the zip code

    const [errors, setErrors] = useState({}); // State variable to store validation errors

    // Validation function for card information
    const validateCard = () => {
        const isValid = /^\d{16}$/.test(card); // Assuming card number is 16 digits
        setErrors((prevErrors) => ({
            ...prevErrors,
            card: isValid ? "" : "Invalid card format",
        }));
    };

    // Validation function for email
    const validateEmail = () => {
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
        setErrors((prevErrors) => ({
            ...prevErrors,
            email: isValid ? "" : "Invalid email format",
        }));
    };

    // Validation function for zip code
    const validateZip = () => {
        const isValid = /(^\d{5}$)|(^\d{5}-\d{4}$)/.test(zip); // Zip code should be 5 digits
        setErrors((prevErrors) => ({
            ...prevErrors,
            zip: isValid ? "" : "Invalid zip code format",
        }));
    };

    // Handle form submission
    const handleSubmit = (e) => {
        e.preventDefault();
        // Validate all fields
        validateEmail();
        validateCard();
        validateZip();

        // Check if there are any errors
        if (Object.values(errors).every((error) => !error)) {
            // No errors, submit data
            axios
                .post("http://localhost:8081/person", { name, email, card, address, city, state, zip })
                .then((response) => {
                    console.log("Data submitted successfully:", response.data);
                    // Handle success message or navigation here
                })
                .catch((error) => {
                    console.error("Error submitting data:", error);
                    // Handle error message here
                });
        } else {
            // Display error messages
            alert("Wrong input field, check again!");
        }
    };

    return (
        <div className="container">
            <h2>Personal Information</h2>
            <form onSubmit={handleSubmit}>
                {/* Add input fields for personal information */}
                <div className="row mb-3">
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Enter Full Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>
                    <div className="col-md-6">
                        <input
                            type="email"
                            className="form-control"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            onBlur={validateEmail}
                            required
                        />
                        {errors.email && <p className="text-danger">{errors.email}</p>}
                    </div>
                </div>

                {/* Add more input fields for other personal information */}
                <div className="row mb-3">
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Card Information"
                            value={card}
                            onChange={(e) => setCard(e.target.value)}
                            onBlur={validateCard}
                            required
                        />
                        {errors.card && <p className="text-danger">{errors.card}</p>}
                    </div>
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Address"
                            value={address}
                            onChange={(e) => setAddress(e.target.value)}
                            required
                        />
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="City"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                            required
                        />
                    </div>
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="State"
                            value={state}
                            onChange={(e) => setState(e.target.value)}
                            required
                        />
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Zip Code"
                            value={zip}
                            onChange={(e) => setZip(e.target.value)}
                            onBlur={validateZip}
                            required
                        />
                        {errors.zip && <p className="text-danger">{errors.zip}</p>}
                    </div>
                </div>

                <button type="submit" className="btn btn-success" onClick={() => alert("Submitted")}>Submit</button>

            </form>
        </div>
    );
};

export default PersonView;
