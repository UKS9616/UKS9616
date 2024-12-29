package com.coms309.users;



import com.coms309.admin.Admin;
import com.coms309.admin.AdminController;
import com.coms309.admin.AdminRepository;
import com.coms309.chats.Chat;
import com.coms309.chats.ChatController;
import com.coms309.events.Event;
import com.coms309.events.EventRepository;
import com.coms309.notifServer.NotificationServer;
import com.coms309.notifServer.UserSession;
import com.coms309.notifications.Notification;
import com.coms309.notifications.NotificationController;
import com.coms309.orgProfiles.OrgProfile;
import com.coms309.orgProfiles.OrgProfileController;
import com.coms309.orgs.Org;
import com.coms309.orgs.OrgController;
import com.coms309.orgs.OrgRepository;
import com.coms309.reviews.Review;
import com.coms309.reviews.ReviewController;
import com.coms309.userProfiles.UserProfile;
import com.coms309.userProfiles.UserProfileController;
import com.coms309.userProfiles.UserProfileRepository;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Udip Shrestha
 *
 */

@RestController
@Tag(name = "User Controller", description = "Controller for User-related operations")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private NotificationServer notifServer;

    @Autowired
    private OrgController orgContr;

    @Autowired
    private AdminController adminContr;

    @Autowired
    private UserProfileController userProfContr;

    @Autowired
    private NotificationController notifContr;

    @Autowired
    private ReviewController reviewContr;

    @Autowired
    private ChatController chatContr;

    @Autowired
    private EventRepository eventRepository;


    @GetMapping(path = "/user")
    @Operation(summary = "Get all users", description = "Returns a list of all existing users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping(path = "/user/{id}")
    @Operation(summary = "Get a user by id", description = "Returns a specific user by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public User getUserById(@Parameter(description = "id of user to be searched") @PathVariable int id){
        return userRepository.findById(id);
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/user/token/{accessToken}")
    @Operation(summary = "Get a user by token", description = "Returns a specific user given by an access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid token supplied",
                    content = @Content)})
    public User getUserByAccessToken(@Parameter(description = "Token of user to be searched") @PathVariable String accessToken){
        if(userRepository.findByLoginToken(accessToken) == null)
            return null;
        return userRepository.findByLoginToken(accessToken);
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/user/username/{username}")
    @Operation(summary = "Get a user by name", description = "Returns a specific user given by a name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid username supplied",
                    content = @Content)})
    public User getUserByUsername(@Parameter(description = "Username of user to be searched") @PathVariable String username){
        if(userRepository.findByUsername(username) == null)
            return null;
        return userRepository.findByUsername(username);
    }

    @PostMapping(path = "/user")
    @Operation(summary = "Create a user", description = "Creates a user in the database according to the User JSON body parameter and user login validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new user\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(value = "{ \"email\": \"User email\", " +
                            "\"username\": \"Username\", " +
                            "\"password\": \"Password\", " +
                            "\"loginToken\": \"1\", " +
                            "\"userLoginDate\": \"Login Date\"" +
                            "}"))) @RequestBody User user){
        if (user == null)
            return new JsonStringResponse("0"); // telling frontend there was an error;
        if(user.getUsername().length() < 3 || user.getUsername().length() > 50){
            return new JsonStringResponse("-1"); // telling frontend there was an error with username;
        }
        if(user.getPassword().length() < 8 || user.getPassword().length() > 50){
            return new JsonStringResponse("-2"); // telling frontend there was an error with password;
        }
        if (!user.getPassword().matches(".*\\d.*")) {
            return new JsonStringResponse("-3"); // telling frontend there was an error with password;
        }
        if (!user.getPassword().matches(".*[!@#$%^&*_=+()<>?].*")) {
            return new JsonStringResponse("-4"); // telling frontend there was an error with password;
        }
        userRepository.save(user);
        return new JsonStringResponse(Long.toString(user.getUserId()));
    }

    @PostMapping(path = "/user/profile")
    @Operation(summary = "Create a user & profile", description = "Creates a user and a default user profile in the database according to the User JSON body parameter and user login validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User & profile created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new user\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse createUserAndProfile(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(value = "{ \"email\": \"User email\", " +
                            "\"username\": \"Username\", " +
                            "\"password\": \"Password\", " +
                            "\"loginToken\": \"1\", " +
                            "\"userLoginDate\": \"Login Date\"" +
                            "}"))) @RequestBody User user){
        JsonStringResponse response = createUser(user);
        if (response.getStrResponse() == null)
            return new JsonStringResponse("0"); // telling frontend there was an error;
        if(Integer.parseInt(response.getStrResponse()) < 1){
            return response; // telling frontend there was an error;
        }
        User newUser = userRepository.findById(Integer.parseInt(response.getStrResponse()));
        UserProfile newProf = new UserProfile("", newUser.getEmail(), newUser.getUsername(), "", "", null);
        userProfRepository.save(newProf);
        newProf.setUser(newUser);
        newUser.setUserProf(newProf);
        userRepository.save(newUser);
        return new JsonStringResponse(Long.toString(user.getUserId()));
    }

//    @PostMapping(path = "/user/valid")
//    public ResponseEntity<JsonStringResponse> createUser(@Valid @RequestBody User user, BindingResult result) {
//        if (result.hasErrors()) {
//            return new ResponseEntity<>(new JsonStringResponse("0"), HttpStatus.BAD_REQUEST);
//        }
//        userRepository.save(user);
//        return new ResponseEntity<>(new JsonStringResponse(Long.toString(user.getUserId())), HttpStatus.CREATED);
//    }

    @PutMapping("/user/{id}")
    @Operation(summary = "Update a user by id", description = "Update a specific user by id in the database according to the User JSON body parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public User updateUser(@Parameter(description = "id of user to be updated") @PathVariable int id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User to update", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(value = "{ \"email\": \"User email\", " +
                            "\"username\": \"Username\", " +
                            "\"password\": \"Password\", " +
                            "\"loginToken\": \"1\", " +
                            "\"userLoginDate\": \"Login Date\"" +
                            "}"))) @RequestBody User request){
        User user = userRepository.findById(id);
        if(user == null || request == null)
            //throw new RuntimeException("User with ID " + id + " not found.");
            return null;

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setLoginToken(request.getLoginToken());
        userRepository.save(user);
        return userRepository.findById(id);
    }


    // Delete a user by ID
    @DeleteMapping("/user/{id}")
    @Operation(summary = "Delete a user by id", description = "Delete a specific user from the database by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "User deleted successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid user ID provided.")))})
    public String deleteUserById(@Parameter(description = "id of user to be deleted") @PathVariable int id) {
        User user = userRepository.findById(id);
        if(user == null){
            return "User not found";
        }
        if (user.getOrg() != null) {
            orgContr.deleteOrg(user.getOrg().getOrgId().intValue());
            user.setOrg(null);
        }
        if(user.getAdmin() != null){
            adminContr.deleteAdmin(user.getAdmin().getAdminId().intValue());
            user.setAdmin(null);
        }
        if(user.getUserProf() != null){
            userProfContr.deleteUserProfile(user.getUserProf().getUserProfId().intValue());
            user.setUserProf(null);
        }
        if(user.getReviews() != null){
            List<Review> reviewsCopy = new ArrayList<>(user.getReviews());
            for(Review review : reviewsCopy){
                reviewContr.deleteReview(review.getReviewId().intValue());
            }
            user.setReviews(null);
        }
        if(user.getNotifications() != null){
            List<Notification> notifCopy = new ArrayList<>(user.getNotifications());
            for(Notification notif : notifCopy){
                notifContr.deleteNotif(notif.getId().intValue());
            }
            user.setNotifications(null);
        }
        if(user.getFriends() != null){
            List<User> friendsCopy = new ArrayList<>(user.getFriends());
            for(User friendCopy : friendsCopy) {
                User friend = userRepository.findById(friendCopy.getUserId().intValue());
                if (friend.getFriends() != null) {
                    friend.deleteFriends(user);
                }
            }
            user.setFriends(null);
        }
        if(user.getEventsRsvp() != null){
            List<Event> eventsRsvpCopy = new ArrayList<>(user.getEventsRsvp());
            for(Event eventCopy : eventsRsvpCopy){
                Event event = eventRepository.findById(eventCopy.getEventId().intValue());
                if(event.getUserRsvp() != null) {
                    event.deleteUserRsvp(user);
                }
            }
            user.setEventsRsvp(null);
        }
        if(chatContr.getMessagesSentByUser(user.getUserId()) != null){
            List<Chat> chatCopy = new ArrayList<>(chatContr.getUserMessageHistoryAll(user.getUserId().intValue()));
            for(Chat chat : chatCopy){
                chatContr.deleteChats(chat.getId().intValue());
            }
        }
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login and assign an access token to a user if the given Login request body, username and password, is verified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"user's new access token\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse loginUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User to login", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(value = "{ \"username\": \"Username\", " +
                            "\"password\": \"Password\""+
                            "}"))) @RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getLoginUsername();
        String password = loginRequest.getLoginPassword();
        if (username == null || password == null) {
            return new JsonStringResponse("0");
        }

        System.out.println("Received username: " + username);
        //System.out.println("Received password: " + password);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            System.out.println("findBy function didn't didn't work");
            return new JsonStringResponse("0");
        }
        if (!password.equals(user.getPassword())) {
            return new JsonStringResponse("0");
        }

        // acceptToken = "0" means not active (soft delete) or error

        // acceptToken = "1" means logged out user
        // acceptToken = "2" means logged out organization
        // acceptToken = "3" means logged out admin

        // acceptToken = "100000-199999" means logged in user
        // acceptToken = "200000-299999" means logged in organization
        // acceptToken = "300000-399999" means logged in admin

        Random rand = new Random();

        // functionality for user to be required to log out before they are able to login again
//        switch (user.getLoginToken()) {
//            case "1" -> user.setLoginToken(Integer.toString(100000 + rand.nextInt(100000)));
//            case "2" -> user.setLoginToken(Integer.toString(200000 + rand.nextInt(100000)));
//            case "3" -> user.setLoginToken(Integer.toString(300000 + rand.nextInt(100000)));
//            default -> {
//                return new JsonStringResponse("0");
//            }
//        }

        // looping until a random and unique user access token is found
        boolean good_token = false;
        while (!good_token){
            // functionality for user to be able to log in without needing to be logged out initially
            if( (user.getLoginToken().equals("1")) || ((Integer.parseInt(user.getLoginToken()) >= 100000) && (Integer.parseInt(user.getLoginToken()) < 200000)) ){
                user.setLoginToken(Integer.toString(100000 + rand.nextInt(100000)));
            }
            else if( (user.getLoginToken().equals("2")) || ((Integer.parseInt(user.getLoginToken()) >= 200000) && (Integer.parseInt(user.getLoginToken()) < 300000)) ){
                user.setLoginToken(Integer.toString(200000 + rand.nextInt(100000)));
            }
            else if( (user.getLoginToken().equals("3")) || ((Integer.parseInt(user.getLoginToken()) >= 300000) && (Integer.parseInt(user.getLoginToken()) < 400000)) ){
                user.setLoginToken(Integer.toString(300000 + rand.nextInt(100000)));
            }
            else{
                return new JsonStringResponse("0");
            }

            good_token = true;
            for (User userCheck : getAllUsers()){
                if( (user.getLoginToken().equals(userCheck.getLoginToken())) && !Objects.equals(user.getUserId(), userCheck.getUserId())){
                    good_token = false;
                    break;
                }
            }

        }

        userRepository.save(user);
        System.out.println("Token: " + user.getLoginToken());
        return new JsonStringResponse(user.getLoginToken());
    }

    // User logout
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout and remove the user's access token for the user given by the access token in the JsonStringResponse body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"User logged out successfully\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse loginOutUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Token of user to logout", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = JsonStringResponse.class),
                    examples = @ExampleObject(value = "{ \"responseStr\": \"User token\"}")
            )) @RequestBody JsonStringResponse userToken) {
        String user_Token = userToken.getStrResponse();
        System.out.println("User Token: " + user_Token);
        if (user_Token == null) {
            return new JsonStringResponse("0"); // tell frontend an error occurred
        }
        User user = userRepository.findByLoginToken(user_Token);
//        if (user == null) {
//            System.out.println("error 1");
//        }

//        for(int i = 1; i <= userRepository.count(); i++){
//            User userCheck = userRepository.findById(i);
//            if(userCheck == null) {
//                continue;
//            }
//            if (user_Token.equals(userCheck.getLoginToken())) {
//                user = userRepository.findById(i);
//            }
//        }

        if (user == null) {
            System.out.println("error");
            return new JsonStringResponse("0");
        }

        if((Integer.parseInt(user_Token) >= 100000) && (Integer.parseInt(user_Token) < 200000)){
            user.setLoginToken("1");
        }
        else if((Integer.parseInt(user_Token) >= 200000) && (Integer.parseInt(user_Token) < 300000)){
            user.setLoginToken("2");
        }
        else if((Integer.parseInt(user_Token) >= 300000) && (Integer.parseInt(user_Token) < 400000)){
            user.setLoginToken("3");
        }
        else{
            user.setLoginToken("0");
            return new JsonStringResponse("0"); // tell frontend an error occurred
        }

        userRepository.save(user);
        System.out.println("Logged Out User Token: " + user.getLoginToken());
        return new JsonStringResponse("10"); // confirmation that the user has been logged out
    }

    @PutMapping("/logout/token/{accessToken}")
    @Operation(summary = "User logout by token", description = "Logout and remove the user's access token for a specific user given by an access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"User logged out successfully\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse loginOutUser(@Parameter(description = "Token of user to be logged out") @PathVariable String accessToken) {
        User user = userRepository.findByLoginToken(accessToken);

        if (user == null) {
            System.out.println("error");
            return new JsonStringResponse("0");
        }

        if((Integer.parseInt(accessToken) >= 100000) && (Integer.parseInt(accessToken) < 200000)){
            user.setLoginToken("1");
        }
        else if((Integer.parseInt(accessToken) >= 200000) && (Integer.parseInt(accessToken) < 300000)){
            user.setLoginToken("2");
        }
        else if((Integer.parseInt(accessToken) >= 300000) && (Integer.parseInt(accessToken) < 400000)){
            user.setLoginToken("3");
        }
        else{
            user.setLoginToken("0");
            return new JsonStringResponse("0"); // tell frontend an error occurred
        }

        userRepository.save(user);
        System.out.println("Logged Out User Token: " + user.getLoginToken());
        return new JsonStringResponse("10"); // confirmation that the user has been logged out
    }

    @PutMapping("/user/{userId}/org/{orgId}")
    @Operation(summary = "Assign a user to an organization by id", description = "Assign the user given by the user Id to a specific organization given by the org Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User assigned to organization successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "User assigned to organization successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid IDs provided.")))})
    String assignUserToOrg(@Parameter(description = "id of user to be assigned") @PathVariable int userId, @Parameter(description = "id of organization to be assigned") @PathVariable int orgId){
        Org org = orgRepository.findById(orgId);
        User user = userRepository.findById(userId);
        if(org == null || user == null)
            return "Error: user or organization not found";
        if( (user.getLoginToken().equals("2")) || ((Integer.parseInt(user.getLoginToken()) >= 200000) && (Integer.parseInt(user.getLoginToken()) < 300000)) ){
            user.setOrg(org);
            org.setUser(user);
            orgRepository.save(org);
            return "User assigned to organization "+ org.getOrgName() + " successfully";
        }
        return "Error: user is not qualified to be an organization";
    }



    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/user/{id}/eventsRsvp")
    @Operation(summary = "Get events that a user has RSVPed to by id", description = "Returns a list of events that have been RSVPed to by a specific user by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's RSVP Event list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Event.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content)})
    public List<Event> getEventRsvps(@Parameter(description = "id of user's event RSVPs to search") @PathVariable int id){
        User user = userRepository.findById(id);
        if(user == null)
            return null;
        return user.getEventsRsvp();
    }



    // Retrieve the list of friends for a specific user
    @GetMapping("/user/{userId}/friends")
    @Operation(summary = "Get a user's friends by id", description = "Returns a list of user that are friends with a specific user by the given userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content)})
    public List<User> getFriendsList(@Parameter(description = "id of user's friends to search") @PathVariable int userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return null; // User not found
        }
        return user.getFriends();
    }


    @PutMapping("/user/{userId}/add/{friendId}")
    @Operation(summary = "Add friends by id", description = "Users defined by userId and friendId are added to each others friends list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added friend",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public User addFriend(@Parameter(description = "id of user to be assigned") @PathVariable int userId, @Parameter(description = "id of friend to be assigned")@PathVariable int friendId) {
        // Check if both users exist
        User user = userRepository.findById(userId);
        User friend = userRepository.findById(friendId);

        if (user == null || friend == null) {
            return null;
        }

        user.addFriends(friend);
        friend.addFriends(user);
        userRepository.save(user);
        userRepository.save(friend);

        // listening to send real-time notifications
        for (UserSession userAndSession : notifServer.getLoggedInUsers()) {
            if (friend.getUsername().equals(userAndSession.getUser())) {
                try {
                    notifServer.onMessage(userAndSession.getSession(), "/friend_request " + friend.getUsername() + " " + user.getUsername());
                } catch (IOException ignored) {
                }
            }
        }

        return friend;
    }


    @GetMapping("/user/suggest/{userId}")
    @Operation(summary = "Search potential friends", description = "Returns a list of users that are not already friends of a specific user given by userId and not itself")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Potential friends list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<User> getSuggestedFriends(@Parameter(description = "id of user to search for friends") @PathVariable int userId, @Parameter(description = "Username of potential friend to be searched") @RequestParam(required = false) String username) {

        // Find the current user by ID
        User currentUser = userRepository.findById(userId);

        if (currentUser == null) {
            return null;
        }

        List<User> excludeUsers = currentUser.getFriends();
//        excludeUsers.add(currentUser);
        List<User> suggestUsers = new ArrayList<>();

//        System.out.println("exclude users lists size: " + excludeUsers.size());
//        System.out.println("total users lists size: " + userRepository.findAll().size());
        for (User user : userRepository.findAll()) {
            if(user != null) {
//                System.out.println("user is not null");
                if (!excludeUsers.contains(user)) {
//                    System.out.println("user is not already a friend");
                    if(!user.getUserId().equals(currentUser.getUserId())) {
//                        System.out.println("user is not itself");
                        if (user.getUsername().toLowerCase().contains(username.toLowerCase())) {
//                            System.out.println("user matches search input");
                            suggestUsers.add(user);
//                            System.out.println("user successfully added");
                        }
                    }
                }
            }
        }

        return suggestUsers;
    }




}
