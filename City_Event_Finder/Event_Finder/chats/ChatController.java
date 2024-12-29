package com.coms309.chats;

import com.coms309.reviews.Review;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;


    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/{id}")
    public String deleteChats(@PathVariable int id){
        Chat chat = chatRepository.findById(id);
        if(chat == null){
            return "failed deleting chat";
        }
        if(chat.getSender() != null){
            chat.setSender(null);
        }
        if(chat.getRecipient() != null){
            chat.setRecipient(null);
        }
        chatRepository.deleteById(id);
        //System.out.println(reviewRepository.count());
        return "Chat deleted successfully";
    }

    /**
     * Retrieve chat history between two users.
     */
    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<Chat>> getChatHistory(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        Optional<User> user1Opt = userRepository.findById(user1Id);
        Optional<User> user2Opt = userRepository.findById(user2Id);

        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user1 = user1Opt.get();
        User user2 = user2Opt.get();

        List<Chat> chatHistory = new ArrayList<>();

        for (Chat chat : chatRepository.findAll()){
            if(chat.getRecipient() != null && chat.getSender() != null){
                if(((chat.getRecipient().getUserId() == user1Id) && (chat.getSender().getUserId() == user2Id)) || ((chat.getRecipient().getUserId() == user2Id) && (chat.getSender().getUserId() == user1Id))){
                    chatHistory.add(chat);
                }
            }
        }

        // Retrieve messages sent from user1 to user2 and vice versa
//        List<Chat> chatHistory = chatRepository.findBySenderAndRecipientOrderByTimestamp(user1, user2);
//        chatHistory.addAll(chatRepository.findBySenderAndRecipientOrderByTimestamp(user2, user1));

        return ResponseEntity.ok(chatHistory);
    }

    /**
     * Retrieve all messages sent by a specific user.
     */
    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<Chat>> getMessagesSentByUser(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Chat> messages = chatRepository.findBySenderOrderByTimestamp(userOpt.get());
        return ResponseEntity.ok(messages);
    }



    /**
     * Retrieve all chat messages involving a user as either sender or recipient.
     */
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Chat>> getAllMessagesInvolvingUser(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userOpt.get();
        List<Chat> chatHistory = chatRepository.findBySenderOrRecipientOrderByTimestamp(user, user);

        return ResponseEntity.ok(chatHistory);
    }



    @GetMapping("/user/history/{userId}")
    public List<Chat> getUserMessageHistory(@PathVariable int userId) {
        User user = userRepository.findById(userId);
        return chatRepository.findBySenderOrderByTimestamp(user);
    }

    @GetMapping("/user/history/all/{userId}")
    public List<Chat> getUserMessageHistoryAll(@PathVariable int userId) {
        User user = userRepository.findById(userId);
        return chatRepository.findBySenderOrRecipientOrderByTimestamp(user, user);
    }
}

