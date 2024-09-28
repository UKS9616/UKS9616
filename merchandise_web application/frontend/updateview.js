import React, { useState } from 'react';

export function UpdateView({ changePage }) {
    const [fullName, setFullName] = useState('');
    const [person, setPerson] = useState(null);
    const [newName, setNewName] = useState('');
    const [newEmail, setNewEmail] = useState('');
    const [newAddress, setNewAddress] = useState('');
    const [newCity, setNewCity] = useState('');
    const [newZip, setNewZip] = useState('');
    const [newCard, setNewCard] = useState('');
    const [updateStatus, setUpdateStatus] = useState('');

    const handleFetchPerson = async () => {
        try {
            const response = await fetch(`http://localhost:8081/person/${encodeURIComponent(fullName)}`);
            if (response.ok) {
                const data = await response.json();
                setPerson(data);
            } else {
                console.error('Error fetching person by name:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching person by name:', error);
        }
    };

    const handleUpdatePerson = async () => {
        alert("Updated!");
        try {
            const response = await fetch(`http://localhost:8081/updatePerson/${encodeURIComponent(fullName)}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    "name": newName,
                    "email": newEmail,
                    "address": newAddress,
                    "city": newCity,
                    "zip": newZip,
                    "card": newCard
                }),
            });
            if (response.ok) {
                setUpdateStatus('Person data successfully updated');
                const data = await response.json();
                console.log('Person updated:', data);
                // Optionally, update the person state to reflect the changes
                setPerson(data);
            } else {
                console.error('Error updating person:', response.statusText);
            }
        } catch (error) {
            console.error('Error updating person:', error);
        }
    };

    return (
        <div>
            <h1>Update Person Information</h1>
            {updateStatus && <p>{updateStatus}</p>}
            <div>
                <label>Enter Full Name:</label>
                <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} />
                <button onClick={handleFetchPerson}>Fetch Person</button>
            </div>
            {person && (
                <div>
                    <h2>Person Information</h2>
                    <p>Name: {person.name}</p>
                    <p>Email: {person.email}</p>
                    <p>Card Information: {person.card}</p>
                    <p>Address: {person.address}</p>
                    <p>City: {person.city}</p>
                    <p>State: {person.state}</p>
                    <p>Zip code: {person.zip}</p>

                </div>
            )}
            {person && (
                <div>
                    <label>New Name:</label>
                    <input type="text" value={newName} onChange={(e) => setNewName(e.target.value)} />
                    <label>New Email:</label>
                    <input type="email" value={newEmail} onChange={(e) => setNewEmail(e.target.value)} />
                    <label>New Address:</label>
                    <input type="text" value={newAddress} onChange={(e) => setNewAddress(e.target.value)} />
                    <label>New City:</label>
                    <input type="text" value={newCity} onChange={(e) => setNewCity(e.target.value)} />
                    <label>New Zip Code:</label>
                    <input type="text" value={newZip} onChange={(e) => setNewZip(e.target.value)} />
                    <label>New Card Info:</label>
                    <input type="text" value={newCard} onChange={(e) => setNewCard(e.target.value)} />
                    <button onClick={handleUpdatePerson}>Update Person</button>
                </div>
            )}
        </div>
    );
}
