package com.coms309.chats;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.coms309.notifServer.NotificationServer;
import com.coms309.notifServer.UserSession;
import com.coms309.notifications.NotificationRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coms309.chats.SpringContext;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.coms309.users.User;
import com.coms309.users.UserRepository;


/**
 *
 * @author Udip Shrestha
 *
 */

@ServerEndpoint("/chat/{userId}")
@Component
public class ChatServer {

    // Maps to track sessions and user IDs
    private static final Map<Session, Long> sessionUserIdMap = new Hashtable<>(); // Maps session to userId
    private static final Map<Long, Session> userIdSessionMap = new Hashtable<>(); // Maps userId to session


    private static final Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static final Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static final Map<String, String> draftMessages = new Hashtable<>();
    private static final Set<String> knownUsers = new HashSet<>();
    private static final Set<String> mutedUsers = new HashSet<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(ChatServer.class);

//    @Autowired
//    private GoogleMapsService googleMapsService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationServer notifServer;


    private void initRepositories() {
        if (userRepository == null) {
            userRepository = SpringContext.getBean(UserRepository.class);
        }
        if (chatRepository == null) {
            chatRepository = SpringContext.getBean(ChatRepository.class);
        }
        if (notifServer == null) {
            notifServer = SpringContext.getBean(NotificationServer.class);
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) throws IOException {
        initRepositories();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            session.getBasicRemote().sendText("User ID not found.");
            session.close();
            return;
        }

        String username = user.getUsername();

        System.out.println(username);
        System.out.println(userId);

        sessionUserIdMap.put(session, userId);
        userIdSessionMap.put(userId, session);
        sessionUsernameMap.put(session, username);

        System.out.println(username);
        System.out.println(userId);

        String welcomeMessage = knownUsers.contains(username) ?
                "Welcome back to the chat, " + username + "!" :
                "Welcome to the chat server, " + username + "!";

        System.out.println(username);
        System.out.println(userId);

        knownUsers.add(username);
        sendMessageToParticularUser(userId, welcomeMessage);
        broadcast("User: " + username + " has joined the chat.");
    }


    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        initRepositories();

        // Retrieve userId instead of username from the session mapping
        Long userId = sessionUserIdMap.get(session);  // Assuming sessionUserIdMap maps session to userId
        if (userId == null) {
            logger.error("[onMessage] User ID not found for session: " + session.getId());
            session.getBasicRemote().sendText("User ID not found. Please reconnect.");
            return;
        }

        logger.info("[onMessage] userId: " + userId + ": " + message);

        if (message.startsWith("/whisper")) {
            handleWhisper(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith("/mute")) {
            handleMute(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith("/unmute")) {
            handleUnmute(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith("/kick")) {
            handleKick(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith(":react:")) {
            handleReaction(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith(":draft:")) {
            saveDraft(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith(":schedule:")) {
            scheduleMessage(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (message.startsWith(":edit:")) {
            editMessage(Long.valueOf(String.valueOf(userId)), message);
        }
        else if (mutedUsers.contains(userId.toString())) {
            sendMessageToParticularUser(userId, "You are muted and cannot send messages to the chat.");
        }
        else if (message.startsWith("@")) {
            directMessage(Long.valueOf(String.valueOf(userId)), message);
        } else {
            broadcast("User " + userId + ": " + message);
        }

        saveChatMessage(Long.valueOf(String.valueOf(userId)), message, null);
    }


    private void handleWhisper(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 3);
        if (splitMsg.length >= 3) {
            Long destUserId = Long.parseLong(splitMsg[1]);  // Parse destination userId from message
            String actualMessage = splitMsg[2];

            sendMessageToParticularUser(destUserId, "[Whisper from User " + userId + "]: " + actualMessage);
            saveChatMessage(userId, actualMessage, destUserId);

        }
    }




    private void handleMute(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 2);
        if (splitMsg.length >= 2) {
            Long userToMute = Long.parseLong(splitMsg[1]);
            mutedUsers.add(String.valueOf(userToMute));
            broadcast("User " + userId + " has muted User " + userToMute + " in the chat.");
        }
    }

    private void handleUnmute(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 2);
        if (splitMsg.length >= 2) {
            Long userToUnmute = Long.parseLong(splitMsg[1]);
            if (mutedUsers.remove(userToUnmute)) {
                broadcast("User " + userId + " has unmuted User " + userToUnmute + " in the chat.");
            }
        }
    }

    private void handleKick(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 2);
        if (splitMsg.length >= 2) {
            Long userToKick = Long.parseLong(splitMsg[1]);
            Session kickedSession = userIdSessionMap.get(userToKick);
            if (kickedSession != null) {
                kickedSession.close();
                sessionUserIdMap.remove(kickedSession);
                userIdSessionMap.remove(userToKick);
                broadcast("User " + userToKick + " has been kicked by User " + userId);
            }
        }
    }
    private void handleReaction(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 3);
        if (splitMsg.length >= 3) {
            Long destUserId = Long.parseLong(splitMsg[1]);
            String reaction = splitMsg[2];
            sendMessageToParticularUser(destUserId, "User " + userId + " reacted to your message with " + reaction);
        }
    }

    private void saveDraft(Long userId, String message) {
        draftMessages.put(userId.toString(), message.substring(":draft:".length()).trim());
        sendMessageToParticularUser(userId, "Draft saved.");
    }
    private void scheduleMessage(Long userId, String message) {
        String[] splitMsg = message.split("\\s+", 3);
        if (splitMsg.length >= 3) {
            String scheduledMessage = splitMsg[2];
            scheduler.schedule(() -> broadcast("User " + userId + " (scheduled): " + scheduledMessage), 5, TimeUnit.SECONDS);
        }
    }

    private void editMessage(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 3);
        if (splitMsg.length >= 3) {
            String newMessage = splitMsg[2];
            broadcast("User " + userId + " edited their message: " + newMessage);
        }
    }
//    private void broadcastLocation(String username, String message) throws IOException {
//        String[] splitMsg = message.split("\\s+", 2);
//        if (splitMsg.length == 2) {
//            String eventLocation = splitMsg[1];
//            String coordinates = googleMapsService.getCoordinates(eventLocation);
//            broadcast(username + " shared location coordinates for event: " + coordinates);
//        }
//    }

    private void directMessage(Long userId, String message) throws IOException {
        String[] splitMsg = message.split("\\s+", 2);
        Long destUserId = Long.parseLong(splitMsg[0].substring(1)); // Extract destination userId from message
        String actualMessage = (splitMsg.length > 1) ? splitMsg[1] : "";

        // Modify the format to "{userId} message"
        sendMessageToParticularUser(destUserId, userId + " " + actualMessage);
        saveChatMessage(userId, actualMessage, destUserId);


        // listening to send real-time notifications
        User sender = userRepository.findById(userId.intValue());
        User receiver = userRepository.findById(destUserId.intValue());
        for (UserSession userAndSession : notifServer.getLoggedInUsers()) {
            if (receiver.getUsername().equals(userAndSession.getUser())) {
                try {
                    notifServer.onMessage(userAndSession.getSession(), "/friend_chat " + receiver.getUsername() + " " + sender.getUsername());
                } catch (IOException ignored) {
                }
            }
        }

    }


    private void sendMessageToParticularUser(Long userId, String message) {
        try {
            Session session = userIdSessionMap.get(userId);
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            logger.info("[DM Exception] Failed to send message to User " + userId + ": " + e.getMessage());
        }
    }

    private void broadcast(String message) {
        sessionUserIdMap.keySet().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] Failed to broadcast message: " + e.getMessage());
            }
        });
    }

    private void saveChatMessage(Long senderId, String content, Long recipientId) {
        initRepositories();
        try {
            Optional<User> sender = userRepository.findById(senderId);
            Optional<User> recipient = recipientId != null ? userRepository.findById(recipientId) : Optional.empty();

            if (sender.isPresent()) {
                Chat chatMessage = new Chat();
                chatMessage.setSender(sender.get());
                chatMessage.setContent(content);
                chatMessage.setTimestamp(LocalDateTime.now());

                if (recipient.isPresent()) {
                    chatMessage.setRecipient(recipient.get());
                }

                chatRepository.save(chatMessage);
            } else {
                logger.error("Sender not found in the database: " + senderId);
            }
        } catch (Exception e) {
            logger.error("[saveChatMessage] Error saving message: " + e.getMessage(), e);
        }
    }





    @OnClose
    public void onClose(Session session) throws IOException {
        Long userId = sessionUserIdMap.remove(session);
        if (userId != null) {
            userIdSessionMap.remove(userId);
            broadcast("User " + userId + " disconnected");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        Long userId = sessionUserIdMap.get(session);
        logger.info("[onError] User " + userId + ": " + throwable.getMessage());
    }
}

