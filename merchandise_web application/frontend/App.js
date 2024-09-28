// App.js
import './App.css';
import { ShoeView } from "./shoeview.js";
import { WatchView } from "./watchview.js";
import { AboutView } from "./aboutview.js";
import { CheckoutView } from "./checkoutview.js"
import DeleteView from './deleteview.js';
import PersonView from "./personview";

import React, { useState } from "react";
import { UpdateView } from './updateview';


function App() {
  const [currentView, setCurrentView] = useState('create'); // Set default view to 'create'
  const [checkoutClicked, setCheckoutClicked] = useState(false); // State to manage checkout button click
  const [cart, setCart] = useState([]); // State to manage cart items

  function changeView(view) {
    setCurrentView(view);
  }
  // Function to navigate to the home view
  const navigateToHome = () => {
    setCurrentView('create');
  }

  // Function to update the cart state
  const updateCart = (newCart) => {
    setCart(newCart);
  }

  // Function to handle checkout button click
  const handleCheckoutClick = () => {
    setCheckoutClicked(true);
    if (checkoutClicked) {
      setCurrentView('checkoutview');
    }
  };


  return (
    <div className="App">
      <div style={{ backgroundColor: "#F2F2F2" }}>
        {/* Navbar */}
        <nav className="navbar navbar-expand-md navbar-dark mb-4" style={{ backgroundColor: "#c0354c" }}>
          <div className="container-fluid">
            <a className="navbar-brand" href="#">SoleElegance</a>
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
              aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
              <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarCollapse">
              <ul className="navbar-nav me-auto mb-2 mb-md-0">
                <li className="nav-item">
                  <a className="nav-link active" aria-current="page" href="#homepage" onClick={() => changeView("create")}>Home</a>
                </li>
                <li className="nav-item">
                  <a className="nav-link" href="#" onClick={() => changeView("shoeview")}>Shoes</a>
                </li>
                <li className="nav-item">
                  <a className="nav-link" href="#" onClick={() => changeView("watchview")}>Watch</a>
                </li>

                <li className="nav-item">
                  <a className="nav-link" href="#" onClick={() => changeView("personview")}>Personal Info</a>
                </li>

                <li className="nav-item">
                  <a className="nav-link" href="#" onClick={() => changeView("updateview")}>Update/Modify</a>
                </li>

                <li className="nav-item">
                  <a className="nav-link" href="#" onClick={() => changeView("deleteview")}>Delete</a>
                </li>
              </ul>
            </div>
          </div>
        </nav>

        {/* Main content */}
        <main className="container">
          {currentView === "create" && (
            <div className="p-5 rounded">
              <h1>Iowa State Merchandise</h1>
              <a href="https://ibb.co/N7Jcf4w"><img src="https://i.ibb.co/Tr79JCz/Homepage2.jpg" alt="Homepage2" border="0"></img></a>
              <p className="lead">Welcome to SoleElegance, your one-stop destination for Iowa State products tailored to college students. We pride ourselves on offering a wide variety of merchandise at affordable prices, ensuring that every student can represent their school with pride without breaking the bank. Conveniently located on campus, our store provides easy access for students, supporting and promoting local businesses while offering the best deals on Iowa State essentials. Shop now and show your Cyclone spirit with style!</p>
              <a className="btn btn-lg btn-primary" role="button" onClick={() => changeView("watchview")}>View products &raquo;</a>
            </div>
          )}
          {/* Rendering different views based on currentView state */}
          {currentView === "shoeview" && <ShoeView onCheckoutClick={handleCheckoutClick} updateCart={updateCart} />}
          {currentView === "watchview" && <WatchView onCheckoutClick={handleCheckoutClick} updateCart={updateCart} />}
          {currentView === "aboutview" && <AboutView />}
          {currentView === "updateview" && <UpdateView />}
          {currentView === "personview" && <PersonView />}
          {currentView === "deleteview" && <DeleteView />}
          {currentView === "checkoutview" && <CheckoutView cart={cart} navigateToHome={changeView} />} {/* To pass the cart items to the CheckoutView */}

        </main>

        {/* Footer */}
        <footer className="footer mt-auto py-3 bg-light">
          <div className="container text-center">
            <span className="text-muted"><a href="#" onClick={() => changeView("aboutview")}> Contact us:</a></span><br /> 
            <span>Email: <u>info@soleelegance.com</u></span><br />
            <span>Phone: +1 (555) 555-5555</span><br />
            <span>Developed by <a href="#" onClick={() => changeView("aboutview")}>Prakarsha Poudel</a> and <a href="#" onClick={() => changeView("aboutview")}>Udip Shrestha</a></span>
          </div>
        </footer>
      </div>

    </div>
  );
}

export default App;
