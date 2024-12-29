package com.coms309.notifications;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Corey Heithoff
 */

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
    Notification findById(int id);

    @Transactional
    void deleteById(int id);

    List<Notification> findByUserId(Long userId);
}
