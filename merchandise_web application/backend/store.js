const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const { MongoClient } = require("mongodb");

const app = express();
const port = 8081;
const host = "localhost";

app.use(cors());
app.use(bodyParser.json());

const url = "mongodb://127.0.0.1:27017";
const dbName = "se319";
const client = new MongoClient(url);

// Connect to MongoDB
const connectToMongoDB = async () => {
  try {
    await client.connect();
    console.log("Connected to MongoDB successfully");
  } catch (error) {
    console.error("Error connecting to MongoDB:", error);
  }
};
connectToMongoDB();

// Routes for shoes
app.get("/shoes", async (req, res) => {
  try {
    const db = client.db(dbName);
    const query = {};
    const results = await db.collection("shoes").find(query).toArray();
    res.status(200).send(results);
  } catch (error) {
    console.error("Error fetching shoes:", error);
    res.status(500).send({ error: "Internal Server Error" });
  }
});

app.get("/shoes/:id", async (req, res) => {
  const shoeId = parseInt(req.params.id);
  try {
    const db = client.db(dbName);
    const query = { id: shoeId };
    const result = await db.collection("shoes").findOne(query);
    if (!result) {
      res.status(404).send("Not Found");
    } else {
      res.status(200).send(result);
    }
  } catch (error) {
    console.error("Error fetching shoe by ID:", error);
    res.status(500).send({ error: "Internal Server Error" });
  }
});



// Routes for watches
app.get("/watches", async (req, res) => {
  try {
    const db = client.db(dbName);
    const query = {};
    const results = await db.collection("watches").find(query).toArray();
    res.status(200).send(results);
  } catch (error) {
    console.error("Error fetching watches:", error);
    res.status(500).send({ error: "Internal Server Error" });
  }
});

app.get("/watches/:id", async (req, res) => {
  const watchId = parseInt(req.params.id);
  try {
    const db = client.db(dbName);
    const query = { id: watchId };
    const result = await db.collection("watches").findOne(query);
    if (!result) {
      res.status(404).send("Not Found");
    } else {
      res.status(200).send(result);
    }
  } catch (error) {
    console.error("Error fetching watch by ID:", error);
    res.status(500).send({ error: "Internal Server Error" });
  }
});


// Routes for person
app.get("/person/:name", async (req, res) => {
  const fullName = req.params.name;
  try {
      const db = client.db(dbName);
      const query = { name: fullName };
      const result = await db.collection("person").findOne(query);
      if (!result) {
          res.status(404).send("Person not found");
      } else {
          res.status(200).send(result);
      }
  } catch (error) {
      console.error("Error fetching person by name:", error);
      res.status(500).send({ error: "Internal Server Error" });
  }
});

app.put("/updatePerson/:name", async (req, res) => {
  const fullName = req.params.name;
  const { name, email, address, city, zip, card } = req.body;
  try {
      const db = client.db(dbName);
      const query = { name: fullName };
      const update = {
          $set: {
              name: name,
              email: email,
              address: address,
              city: city,
              zip: zip,
              card: card
          }
      };
      const options = { returnOriginal: false };
      const result = await db.collection("person").findOneAndUpdate(query, update, options);
      if (!result.value) {
          res.status(404).send("Person not found");
      } else {
          res.status(200).send(result.value);
      }
  } catch (error) {
      console.error("Error updating person:", error);
      res.status(500).send({ error: "Internal Server Error" });
  }
});



app.post("/person", async (req, res) => {
  const { name, email, card, address, city, state, zip } = req.body;
  try {
    const db = client.db(dbName);
    const result = await db.collection("person").insertOne({ name, email, card, address, city, state, zip });
    console.log("Inserted:", result.ops[0]);
    res.status(201).send("Data inserted successfully");
  } catch (error) {
    console.error("Error inserting data into person collection:", error);
    res.status(500).send({ error: "Internal Server Error" });
  }
});

app.delete("/person/:name", async (req, res) => {
  const fullName = req.params.name;
  try {
    const db = client.db(dbName);
    const query = { name: fullName };
    const result = await db.collection("person").deleteOne(query);
    if (result.deletedCount === 0) {
      res.status(404).send("Person not found");
    } else {
      res.status(200).send("Data successfully deleted");
    }
  } catch (error) {
    console.error("Error deleting person data:", error);
    res.status(500).send({ error: "Internal Server Error" });
  }
});




// Start the server
app.listen(port, () => {
  console.log(`App listening at http://${host}:${port}`);
});
