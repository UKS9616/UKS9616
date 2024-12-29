package com.coms309;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class coreySystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }


    @Test
    public void createUserTest() {
        // Step 1: Create a user using the POST endpoint
        String userRequestBody = "{ " +
                "\"email\": \"testuser@example.com\", " +
                "\"username\": \"testuser\", " +
                "\"password\": \"Password123!\", " +
                "\"loginToken\": \"1\", " +
                "\"userLoginDate\": \"12-02-24 3:30 PM\" " +
                "}";

        Response createUserResponse = RestAssured.given().
                header("Content-Type", "application/json").
                body(userRequestBody).
                when().
                post("/user");

        int createStatusCode = createUserResponse.getStatusCode();
        assertEquals(200, createStatusCode);

        String createUserResponseBody = createUserResponse.getBody().asString();
        String userId = null;
        try {
            JSONObject createResponseObj = new JSONObject(createUserResponseBody);
            userId = createResponseObj.getString("strResponse");
            assertEquals(true, Integer.parseInt(userId) > 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Step 2: Retrieve the user using the GET /user/{id} endpoint
        Response getUserResponse = RestAssured.given().
                when().
                get("/user/" + userId);

        int getStatusCode = getUserResponse.getStatusCode();
        assertEquals(200, getStatusCode);

        String getUserResponseBody = getUserResponse.getBody().asString();
        try {
            JSONObject userObj = new JSONObject(getUserResponseBody);
            assertEquals("testuser@example.com", userObj.getString("email"));
            assertEquals("testuser", userObj.getString("username"));
            assertEquals("1", userObj.getString("loginToken"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void loginTest() {
        // Send request with valid username and password
        String requestBody = "{ \"username\": \"user1\", \"password\": \"password1&\" }";

        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                body(requestBody).
                when().
                post("/login");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for valid token
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            String token = returnObj.getString("strResponse");
            assertEquals(6, token.length()); // Example: Token length is 6
            assertEquals(true, Integer.parseInt(token) >= 100000 && Integer.parseInt(token) < 200000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createEventTest() {
        // Step 1: Create an event using /event POST request
        String requestBody = "{ " +
                "\"name\": \"New Event\", " +
                "\"location\": \"Location Address\", " +
                "\"description\": \"Description Text\", " +
                "\"cost\": \"10\", " +
                "\"date\": \"12-01-24\", " +
                "\"startTime\": \"10:00 AM\", " +
                "\"endTime\": \"2:00 PM\" " +
                "}";

        Response createResponse = RestAssured.given().
                header("Content-Type", "application/json").
                body(requestBody).
                when().
                post("/event");

        // Check the status code
        int createStatusCode = createResponse.getStatusCode();
        assertEquals(200, createStatusCode);

        String createResponseBody = createResponse.getBody().asString();
        String eventId = null;
        try {
            JSONObject createResponseObj = new JSONObject(createResponseBody);
            eventId = createResponseObj.getString("strResponse");
            assertEquals(true, Long.parseLong(eventId) > 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Step 2: Retrieve the new event using the /event/{id} GET request
        Response getResponse = RestAssured.given().
                when().
                get("/event/" + eventId);

        int getStatusCode = getResponse.getStatusCode();
        assertEquals(200, getStatusCode);

        // Verify the event details in the response
        String getResponseBody = getResponse.getBody().asString();
        try {
            JSONObject eventObj = new JSONObject(getResponseBody);
            assertEquals("New Event", eventObj.getString("name"));
            assertEquals("Location Address", eventObj.getString("location"));
            assertEquals("Description Text", eventObj.getString("description"));
            assertEquals("10", eventObj.getString("cost"));
            assertEquals("12-1-24", eventObj.getString("date"));
            assertEquals("10:00 AM", eventObj.getString("startTime"));
            assertEquals("2:00 PM", eventObj.getString("endTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createReviewTest() {
        // Step 1: Create a review using the POST endpoint
        String reviewRequestBody = "{ " +
                "\"content\": \"Excellent event!\", " +
                "\"rating\": \"4.5\", " +
                "\"reviewDate\": \"12-02-24 3:30 PM\" " +
                "}";

        // Using user ID #1 and event ID #1 by default
        int userId = 1;
        int eventId = 1;

        Response createReviewResponse = RestAssured.given().
                header("Content-Type", "application/json").
                body(reviewRequestBody).
                when().
                post("/review/user/" + userId + "/event/" + eventId);

        int createStatusCode = createReviewResponse.getStatusCode();
        assertEquals(200, createStatusCode);

        String createReviewResponseBody = createReviewResponse.getBody().asString();
        String reviewId = null;
        try {
            JSONObject createResponseObj = new JSONObject(createReviewResponseBody);
            reviewId = createResponseObj.getString("strResponse");
            System.out.println("\n\nreviewId: " + Integer.parseInt(reviewId));
            assertEquals(true, Integer.parseInt(reviewId) > 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Step 2: Retrieve the review using the /review/{id} GET endpoint
        Response getReviewResponse = RestAssured.given().
                when().
                get("/review/" + reviewId);

        int getStatusCode = getReviewResponse.getStatusCode();
        assertEquals(200, getStatusCode);

        // Verify the review details in the response
        String getReviewResponseBody = getReviewResponse.getBody().asString();
        try {
            JSONObject reviewObj = new JSONObject(getReviewResponseBody);
            assertEquals("Excellent event!", reviewObj.getString("content"));
            assertEquals("4.5", reviewObj.getString("rating"));
            assertEquals("12-2-24 3:30 PM", reviewObj.getString("reviewDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
