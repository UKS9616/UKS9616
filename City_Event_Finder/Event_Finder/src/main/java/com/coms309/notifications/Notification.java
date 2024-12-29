package com.coms309.notifications;

import com.coms309.users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.Getter;

/**\
 * Definitions and outline for Notification entity
 *
 * @author Corey Heithoff
 */

@Getter
@Entity
@Table(name="notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    @JsonBackReference
    private User user;

//    @Column
//    private String username;

    @Column
    private String notificationStr;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date = new Date();


    public Notification() {}

    public Notification(User user, String content) {
        this.user = user;
        this.notificationStr = content;
//        this.username = username;

    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNotificationStr(String content) {
        this.notificationStr = content;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }

    public void setDate(Date date) {
        this.date = date;
    }



}
