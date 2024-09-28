import React, { useState, useEffect } from 'react';
import axios from 'axios';

function DeleteView() {
  const [fullName, setFullName] = useState('');
  const [personData, setPersonData] = useState(null);
  const [deleted, setDeleted] = useState(false);

  useEffect(() => {
    setPersonData(null); // Reset personData when fullName changes
    setDeleted(false); // Reset deleted state when fullName changes
  }, [fullName]);

  const handleFetchData = async () => {
    try {
      const response = await axios.get(`http://localhost:8081/person/${fullName}`);
      setPersonData(response.data);
    } catch (error) {
      console.error('Error fetching person data:', error);
    }
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`http://localhost:8081/person/${fullName}`);
      setDeleted(true);
    } catch (error) {
      console.error('Error deleting person data:', error);
    }
  };

  return (
    <div>
      <h2>Delete Person Data</h2>
      {deleted ? (
        <p>Data successfully deleted.</p>
      ) : (
        <div>
          <label htmlFor="fullName">Enter Full Name:</label>
          <input 
            type="text" 
            id="fullName" 
            value={fullName} 
            onChange={(e) => setFullName(e.target.value)} 
          />
          <button onClick={handleFetchData}>Fetch Data</button>
          {personData && (
            <div>
              <h3>Full Name: {personData.name}</h3>
              <p>Email: {personData.email}</p>
              <p>Address: {personData.address}</p>
              <p>City: {personData.city}</p>
              <p>State: {personData.state}</p>
              <p>Zip Code: {personData.zip}</p>
              <p>Credit Card Info: {personData.card}</p>
              <button onClick={handleDelete}>Delete</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default DeleteView;
