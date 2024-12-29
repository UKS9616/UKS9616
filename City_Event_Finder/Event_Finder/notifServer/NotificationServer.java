package com.coms309.notifServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.coms309.notifications.Notification;
import com.coms309.notifications.NotificationRepository;
import com.coms309.notifications.NotificationController;
import com.coms309.users.JsonStringResponse;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Component
@ServerEndpoint(value = "/notification/{token}")
public class NotificationServer {

    private static NotificationController notifController;

    @Autowired
    public void setNotificationController(NotificationController repo) {
        notifController = repo;
    }


    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository repo) {
        userRepository = repo;
    }

    private static NotificationRepository notificationRepo;

    @Autowired
    public void setNotifRepository(NotificationRepository repo) {
        notificationRepo = repo;
    }

    private static Map<Session, String> sessionUserMap = new Hashtable<>();
    private static Map<String, Session> userSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(NotificationServer.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {

        logger.info("Entered into Open");

        if (token.equals("5")){
            System.out.println("Notification Manager is Active");
            String NotifManager = "notification";
            sessionUserMap.put(session, NotifManager);
            userSessionMap.put(NotifManager, session);
            sendMessageToParticularUser(NotifManager, "Notification Manager is Active");
        }
        else {

            User user = userRepository.findByLoginToken(token);
            if (user == null) {
                System.out.println("Error: user not found");
                //broadcast("Error: user not found");
            }
            else {
                System.out.println(user.getUsername());

                // store connecting user information
                sessionUserMap.put(session, user.getUsername());
                userSessionMap.put(user.getUsername(), session);

                String message = user.getUsername() + ", your notifications will appear here:";
                sendMessageToParticularUser(user.getUsername(), message);

                //Send notification history
                if(!getNotificationHistory(user.getUserId()).isEmpty()) {
                    sendMessageToParticularUser(user.getUsername(), getNotificationHistory(user.getUserId()));
                }
            }
        }
    }


    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUserMap.get(session);
        String notificationStr = "";
        String userReceiving = "";

        if (message.startsWith("/users")) {

            if (userSessionMap.containsKey("notification")) {
                sendMessageToParticularUser("notification", getLoggedInUsersDebug());
            }

        }

        // notification when a user receives a friend request

        else if (message.startsWith("/friend_request")) {

            String[] parts = message.split("\\s+", 3);

            if (parts.length > 2) {
                userReceiving = parts[1].trim();
                String userSendingReq = parts[2].trim();
                if (!(userSessionMap.containsKey(userReceiving))) {
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Did not find " + userReceiving);
                    }
                }
                else {
                    notificationStr = userSendingReq + " added you as a friend!";
                    sendMessageToParticularUser(userReceiving, notificationStr);
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Friend request to " + userReceiving + " successful");
                    }
                }
            }
            else {
                if (userSessionMap.containsKey("notification")) {
                    sendMessageToParticularUser("notification", "Error sending notification");
                }
            }
        }

        // notification when an organization that a user follows posts an event

        else if (message.startsWith("/org_post_event")) {

            String[] parts = message.split("\\s+", 4);

            if (parts.length > 3) {
                userReceiving = parts[1].trim();
                String orgName = parts[2].trim();
                String eventName = parts[3].trim();
                if (!(userSessionMap.containsKey(userReceiving))) {
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Did not find " + userReceiving);
                    }
                }
                else {
                    notificationStr = orgName + " that you follow posted a new event: " + eventName;
                    sendMessageToParticularUser(userReceiving, notificationStr);
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Organization event post to " + userReceiving + " successful");
                    }
                }
            }
            else {
                if (userSessionMap.containsKey("notification")) {
                    sendMessageToParticularUser("notification", "Error sending organization event post");
                }
            }
        }

        // notification when a user receives a chat from a friend

        else if (message.startsWith("/friend_chat")) {

            String[] parts = message.split("\\s+", 3);

            if (parts.length > 2) {
                userReceiving = parts[1].trim();
                String userSendingChat = parts[2].trim();
                if (!(userSessionMap.containsKey(userReceiving))) {
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Did not find " + userReceiving);
                    }
                }
                else {
                    notificationStr = "You have received a chat from " + userSendingChat;
                    sendMessageToParticularUser(userReceiving, notificationStr);
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Friend chat alert to " + userReceiving + " successful");
                    }
                }
            }
            else {
                if (userSessionMap.containsKey("notification")) {
                    sendMessageToParticularUser("notification", "Error sending friend chat notification");
                }
            }
        }

        // notification when an event the user RSVPed to is about to begin

        else if (message.startsWith("/rsvp_event")) {

            String[] parts = message.split("\\s+", 4);

            if (parts.length > 2) {
                userReceiving = parts[1].trim();
                String eventName = parts[2].trim();
                String eventTime = parts[3].trim();
                if (!(userSessionMap.containsKey(userReceiving))) {
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Did not find " + userReceiving);
                    }
                }
                else {
                    notificationStr = eventName + " that you RSVP to will begin in " + eventTime + " minutes";
                    sendMessageToParticularUser(userReceiving, notificationStr);
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Event alert to " + userReceiving + " successful");
                    }
                }
            }
            else {
                if (userSessionMap.containsKey("notification")) {
                    sendMessageToParticularUser("notification", "Error sending Event Alert notification");
                }
            }
        }

        // notification for RSVP confirmation

        else if (message.startsWith("/rsvp_confirmation")) {

            String[] parts = message.split("\\s+", 3);

            if (parts.length > 2) {
                userReceiving = parts[1].trim();
                String eventName = parts[2].trim();
                if (!(userSessionMap.containsKey(userReceiving))) {
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "Did not find " + userReceiving);
                    }
                }
                else {
                    notificationStr = "Your RSVP to " + eventName + " is successful";
                    sendMessageToParticularUser(userReceiving, notificationStr);
                    if (userSessionMap.containsKey("notification")) {
                        sendMessageToParticularUser("notification", "RSVP confirmation to " + userReceiving + " successful");
                    }
                }
            }
            else {
                if (userSessionMap.containsKey("notification")) {
                    sendMessageToParticularUser("notification", "Error sending RSVP confirmation notification");
                }
            }
        }

        else {
            if (userSessionMap.containsKey("notification")) {
                sendMessageToParticularUser("notification", "Error sending notification");
            }
        }



//        // Direct message to a user using the format "@username <message>"
//        if (message.startsWith("@")) {
//            String destUsername = message.split(" ")[0].substring(1);
//
//            // send the message to the sender and receiver
//            sendMessageToParticularUser(destUsername, "[DM] " + username + ": " + message);
//            sendMessageToParticularUser(username, "[DM] " + username + ": " + message);
//
//        }
//        else { // broadcast
//            broadcast(username + ": " + message);
//        }

        // Saving chat history to repository
        System.out.println("Notification str: " + notificationStr);
        System.out.println("Receiving user: " + userReceiving);
        if(!(notificationStr.isEmpty() || userReceiving.isEmpty())){
            System.out.println("passed check 1");
            User user = userRepository.findByUsername(userReceiving);
            if (user != null) {
                System.out.println("passed check 2");
                System.out.println(user.getNotifications().toString());

//                createNotificationForUser(user, notificationStr);

//                notificationRepo.save(new Notification(user, notificationStr, user.getUsername()));

                JsonStringResponse notifString = new JsonStringResponse(notificationStr);
                notifController.createNotifAndUserConnection(notifString, user.getUserId().intValue());



//                Notification notif = new Notification(user, notificationStr);
//                System.out.println("Notification Str Check: " + notif.getNotificationStr());
//                notificationRepo.save(new Notification(user, notificationStr));
//                Long notifId = notifController.createNotif(notif);
//                System.out.println("Notification Id: " + notifId);
//                user.addNotifications(notif);
                System.out.println("passed check 3");
//                String rep = notifController.assignUserToNotif(user.getUserId().intValue(), notifId.intValue());
//                System.out.println("response of PUT request: " + rep);
            }
        }
        notificationStr = "";

    }

//    private void createNotificationForUser(User user, String notificationStr) {
//        Notification notification = new Notification(user, notificationStr);
//        notificationRepo.save(notification);
//        notification.setUser(user);
//        user.addNotifications(notification);
//        userRepository.save(user);
//        //notificationRepo.save(notification);
//    }


    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");
        String username = sessionUserMap.get(session);
        sessionUserMap.remove(session);
        userSessionMap.remove(username);

        // broadcast that the user disconnected
        String message = username + " disconnected";
        //sendMessageToParticularUser(username, message);
        System.out.println(message);
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = sessionUserMap.get(session);
        logger.info("[onError] " + username + ": " + throwable.getMessage());
        // Do error handling here
//        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToParticularUser(String username, String message) {
        try {
            System.out.println(username);
            userSessionMap.get(username).getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }


//    private void broadcast(String message) {
//        sessionUserMap.forEach((session, username) -> {
//            try {
//                session.getBasicRemote().sendText(message);
//            }
//            catch (IOException e) {
//                logger.info("Exception: " + e.getMessage().toString());
//                e.printStackTrace();
//            }
//
//        });
//
//    }


    // Gets the Chat history from the repository
    private String getNotificationHistory(Long userId) {
        List<Notification> messages = notificationRepo.findByUserId(userId);

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        if(messages != null && !messages.isEmpty()) {
            for (Notification message : messages) {
                sb.append(message.getNotificationStr() + "\n");
            }
        }
        return sb.toString();
    }

    public String getLoggedInUsersDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("Currently logged-in users:\n");

        for (Map.Entry<String, Session> entry : userSessionMap.entrySet()) {
            String username = entry.getKey();
            String sessionId = entry.getValue().getId();
            sb.append("User: ").append(username).append(", Session ID: ").append(sessionId).append("\n");
        }

        return sb.toString();
    }

    public List<UserSession> getLoggedInUsers() {
        List<UserSession> userAndSessionList = new ArrayList<>();
        for (Map.Entry<String, Session> entry : userSessionMap.entrySet()) {
            String username = entry.getKey();
            Session session = entry.getValue();
            UserSession userAndSession = new UserSession(session, username);
            userAndSessionList.add(userAndSession);
        }
        return userAndSessionList;
    }

}
