package com.coms309.users;

import com.coms309.admin.Admin;
import com.coms309.events.Event;
import com.coms309.notifications.Notification;

//import com.coms309.friends.Friend;
import com.coms309.orgProfiles.OrgProfile;
import com.coms309.orgs.Org;
import com.coms309.reviews.Review;
import com.coms309.userProfiles.UserProfile;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;
import javax.validation.constraints.Size;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;





/**
 *
 * @author Udip Shrestha
 *
 */

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "loginToken")
    private String loginToken;
    // acceptToken = "0" means not active (soft delete)

    // acceptToken = "1" means logged out user
    // acceptToken = "2" means logged out organization
    // acceptToken = "3" means logged out admin

    // acceptToken = "100000-199999" means logged in user
    // acceptToken = "200000-299999" means logged in organization
    // acceptToken = "300000-399999" means logged in admin

    //@Column(name = "username")
    @Size(min = 3, max = 50)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    //@JsonIgnore
    //@Column(name = "password")
    @Size(min = 8, max = 50)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "userLoginDate")
    @JsonFormat(pattern = "M-d-yy h:mm a") // Specify the format "mm-dd-yyyy"
    private LocalDateTime userLoginDate;

    @ManyToMany(mappedBy = "users_rsvp")
    @JsonIgnore
    private List<Event> events_rsvp;

//    @OneToMany
//    private List<User> friends;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> friends;


    @OneToOne
    @JsonIgnore
    private Org org;

    @OneToOne
    @JsonIgnore
    private Admin admin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userProfId")
    private UserProfile userProf;

    @OneToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JsonManagedReference
    private List<Notification> notifications;

    @OneToMany
    @JsonIgnore
    @JsonManagedReference
    private List<Review> reviews;


    public User(String email, String username, String password, String loginToken, LocalDateTime userLoginDate) {
        this.email = email;
        this.username = username;
        this.loginToken = loginToken;
        this.password = password;
        this.userLoginDate = userLoginDate; //LocalDate.now();
        events_rsvp = new ArrayList<>();
        notifications = new ArrayList<>();

        reviews = new ArrayList<>();
        friends = new ArrayList<>();
    }

    public User() {
        events_rsvp = new ArrayList<>();
        notifications = new ArrayList<>();

        reviews = new ArrayList<>();
        friends = new ArrayList<>();
    }


    public Long getUserId(){
        return id;
    }

    public void setUserId(Long id){
        this.id = id;
    }

    public String getLoginToken(){
        return loginToken;
    }

    public void setLoginToken(String loginToken){
        this.loginToken = loginToken;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String checkLogin() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getUserLoginDate() {
        return this.userLoginDate;
    }

    public void setUserLoginDate(LocalDateTime userLoginDate) {
        this.userLoginDate = userLoginDate;
    }

//    public List<Event> getEventsRsvp() {
//        return events_rsvp;
//    }

    public List<Event> getEventsRsvp() {
        if(events_rsvp == null){
            return null;
        }
        else{
            List<Event> evntRSVP = new ArrayList<>();
            for(Event event : events_rsvp){
                event.setUserRsvp(null);
                if(event.getEventOrg() != null) {
                    if (event.getEventOrg().getUser() != null) {
                        event.getEventOrg().setUser(null);
                    }
                }
                evntRSVP.add(event);
            }
            return evntRSVP;
        }
    }

    public void setEventsRsvp(List<Event> events_rsvp) {
        this.events_rsvp = events_rsvp;
    }

    public void addEventsRsvp(Event events_rsvp){
        this.events_rsvp.add(events_rsvp);
    }

    public void deleteEventsRsvp(Event events_rsvp){
        if(this.events_rsvp != null) {
            this.events_rsvp.remove(events_rsvp);
        }
    }

    public Org getOrg(){
        return org;
    }

    public void setOrg(Org org){
        this.org = org;
    }

    public Admin getAdmin(){
        return admin;
    }

    public void setAdmin(Admin admin){
        this.admin = admin;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addNotifications(Notification notification){
        this.notifications.add(notification);
    }

//    public void addNotifications2(Notification notification){
//        List<Notification> notifs = getNotifications();
//        notifs.add(notification);
//        setNotifications(notifs);
//    }

    public void deleteNotifications(Notification notification) {
        this.notifications.remove(notification);
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review){
        this.reviews.add(review);
    }

    public void deleteReview(Review review){
        this.reviews.remove(review);
    }


//    public List<User> getFriends() {
//        return friends;
//    }

    public List<User> getFriends() {
        if(friends == null){
            return null;
        }
        else{
            List<User> friendList = new ArrayList<>();
            for(User user : friends){
                user.setFriends(null);

//                event.setUserRsvp(null);
//                event.getEventOrg().setUser(null);
                friendList.add(user);
            }
            return friendList;
        }
    }

    public void addFriends(User friend) {
        this.friends.add(friend);
    }

    public void deleteFriends(User friend) {
        this.friends.remove(friend);
    }

    public Object getId() {

        return this.id;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public UserProfile getUserProf(){
        return userProf;
    }

    public void setUserProf(UserProfile userProf){
        this.userProf = userProf;
    }

}

