package com.coms309.notifications;

import com.coms309.admin.Admin;
import com.coms309.admin.AdminRepository;
import com.coms309.events.Event;
import com.coms309.orgs.Org;
import com.coms309.orgs.OrgRepository;
import com.coms309.users.JsonStringResponse;
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
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Notification Controller", description = "Controller for Notification-related operations")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;


    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/notifications")
    @Operation(summary = "Get all notifications", description = "Returns a list of all existing notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<Notification> getAllNotif(){
        return notificationRepository.findAll();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/notifications/{id}")
    @Operation(summary = "Get a notification by id", description = "Returns a specific notification by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the notification",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notification.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public Notification getNotifById(@Parameter(description = "id of notification to be searched") @PathVariable int id){
        return notificationRepository.findById(id);
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/notifications/token/{accessToken}")
    @Operation(summary = "Get a notification by token", description = "Returns all notifications for a specific user given by their access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the notification list",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public List<Notification> getNotifByAccessToken(@Parameter(description = "token of user's notifications to be searched") @PathVariable String accessToken){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null)
            return null;
        if(user.getNotifications() == null)
            return null;
        return user.getNotifications();
    }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/notifications")
    @Operation(summary = "Create a notification", description = "Creates a notification in the database according to the Notification JSON body parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification created successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = Long.class),
                            examples = @ExampleObject(value = "123"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content) })
    public Long createNotif(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notification to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Notification.class),
                    examples = @ExampleObject(value = "{ \"notificationStr\": \"Notification message\"}")))
                                @RequestBody Notification notif) {
        if (notif == null)
            return null; // telling frontend there was an error
        notificationRepository.save(notif);
        return notif.getId();
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/notifications/{id}")
    @Operation(summary = "Update a notification by id", description = "Update a specific notification by id in the database according to the Notification JSON body parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notification.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content) })
    public Notification updateNotif(@Parameter(description = "id of notification to be updated")@PathVariable int id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notification to update", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Notification.class),
                    examples = @ExampleObject(value = "{ \"notificationStr\": \"Notification message\"}")))
                    @RequestBody Notification request){
        Notification notification = notificationRepository.findById(id);
        if(notification == null || request == null)
            return null;
        notification.setNotificationStr(request.getNotificationStr());
        notification.setDate(request.getDate());
        notificationRepository.save(notification);
        return notificationRepository.findById(id);
    }

    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/notifications/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Notification deleted successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid notification ID provided.")))})
    @Operation(summary = "Delete a notification by id", description = "Delete a specific notification from the database by the given id")
    public String deleteNotif(@Parameter(description = "id of notification to be searched") @PathVariable int id){
        Notification notif = notificationRepository.findById(id);
        if(notif == null){
            return "failed deleting notification";
        }
        if(notif.getUser() != null){
            notif.getUser().deleteNotifications(notif);
            notif.setUser(null);
        }
        notificationRepository.deleteById(id);
        //System.out.println(notificationRepository.count());
        return "notification deleted successfully";
    }

    @PutMapping("/user/{userId}/notifications/{notifId}")
    @Operation(summary = "Assign a notification to a user by id", description = "Assign the notification given by the notifId to a specific user given by the userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification assigned to user successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Notification assigned to user successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid ID provided.")))})
    public String assignUserToNotif(@Parameter(description = "id of user to be assigned")@PathVariable int userId, @Parameter(description = "id of notification to be assigned")@PathVariable int notifId){
        Notification notification = notificationRepository.findById(notifId);
        User user = userRepository.findById(userId);
        if (notification == null || user == null) {
            return "Error: user or notification not found";
        }
        user.addNotifications(notification);
        userRepository.save(user);
        notification.setUser(user);
        notificationRepository.save(notification);
        return "User " + notification.getUser().getUsername() + " assigned to notification " + notification.getId() + " successfully";

    }


    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/notifications/user/{userId}")
    @Operation(summary = "Create a notification and assign it to a user by id", description = "Creates a notification according to the Notification JSON body parameter and assigns it to a specific user given by the userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification created and assigned to user successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Notification created and assigned to user successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid ID provided.")))})
    public String createNotifAndUserConnection(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notification to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = JsonStringResponse.class),
                    examples = @ExampleObject(value = "{ \"notificationStr\": \"Notification message\"}")
                    )) @RequestBody JsonStringResponse notifStr, @Parameter(description = "id of user receiving the notification") @PathVariable int userId) {
        if (notifStr == null)
            return "Error: notification str is null"; // telling frontend there was an error
        User user = userRepository.findById(userId);
        if (user == null)
            return "Error: user not found";
//        Notification notification = new Notification(user, notifStr.getStrResponse(), user.getUsername());
        Notification notification = new Notification(user, notifStr.getStrResponse());
        createNotif(notification);
        user.addNotifications(notification);
        userRepository.save(user);
        notification.setUser(user);
        notificationRepository.save(notification);

        return "User " + user.getUsername() + " assigned to notification " + notification.getId() + " successfully";
    }

}

