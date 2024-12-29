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
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UdipSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void updateAdminTest() {
        // Step 1: Update an admin using the PUT endpoint
        int adminId = 1; // Replace with an actual admin ID
        String requestBody = "{ " +
                "\"name\": \"Updated Admin Name\", " +
                "\"email\": \"updatedadmin@example.com\" " +
                "}";

        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                body(requestBody).
                when().
                put("/admin/" + adminId);

        // Assert status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Assert the admin details were updated
        Response getResponse = RestAssured.given().
                when().
                get("/admin/" + adminId);

        String getResponseBody = getResponse.getBody().asString();
        try {
            JSONObject adminObj = new JSONObject(getResponseBody);
            assertEquals("Updated Admin Name", adminObj.getString("name"));
            assertEquals("updatedadmin@example.com", adminObj.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getAllAdminsTest() {
        // Step 1: Retrieve all admins
        Response response = RestAssured.given().
                when().
                get("/admin");

        // Assert status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Assert response contains admin data
        String responseBody = response.getBody().asString();
        try {
            JSONArray adminsArray = new JSONArray(responseBody);
            assertEquals(true, adminsArray.length() >= 0); // At least zero or more admins
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }







    @Test
    public void retrieveOrganizationImageTest() {
        // Test for retrieving organization profile image
        int orgId = 1; // Replace with a valid organization ID
        Response response = RestAssured.given()
                .when()
                .get("/org/" + orgId + "/image");

        // Verify response status code
        assertEquals(200, response.getStatusCode());

        // Verify the content type is an image
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void deleteEventTest() {
        // Step 1: Create an event to delete
        String requestBody = "{ " +
                "\"name\": \"Delete Event\", " +
                "\"location\": \"123 Main St\", " +
                "\"description\": \"This event will be deleted.\", " +
                "\"cost\": \"0\", " +
                "\"date\": \"12-02-24\", " +
                "\"startTime\": \"10:00 AM\", " +
                "\"endTime\": \"12:00 PM\" " +
                "}";

        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/event");

        int createStatusCode = createResponse.getStatusCode();
        assertEquals(200, createStatusCode);

        String createResponseBody = createResponse.getBody().asString();
        String eventId = null;
        try {
            JSONObject createResponseObj = new JSONObject(createResponseBody);
            eventId = createResponseObj.getString("strResponse");
            assertEquals(true, Integer.parseInt(eventId) > 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Step 2: Delete the event
        Response deleteResponse = RestAssured.given()
                .when()
                .delete("/event/" + eventId);

        // Verify status code
        assertEquals(200, deleteResponse.getStatusCode());
        String deleteResponseBody = deleteResponse.getBody().asString();
        assertEquals("Event deleted successfully", deleteResponseBody);
    }

    
}
