package com.coms309.chats;

import com.coms309.admin.Admin;
import com.coms309.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findById(int id);

    @Transactional
    void deleteById(int id);

    // Retrieve all chat messages between two users, ordered by timestamp
    List<Chat> findBySenderAndRecipientOrderByTimestamp(User sender, User recipient);

    // Retrieve all chat messages from a single user (e.g., for group messages)
    List<Chat> findBySenderOrderByTimestamp(User sender);

    // Optionally, retrieve all messages involving a particular user as sender or recipient
    List<Chat> findBySenderOrRecipientOrderByTimestamp(User sender, User recipient);
}
