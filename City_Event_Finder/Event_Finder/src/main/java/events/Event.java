package com.coms309.events;

import com.coms309.images.Image;

import com.coms309.reviews.Review;
import com.coms309.userProfiles.UserProfile;
import com.coms309.users.User;
import com.coms309.orgs.Org;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**\
 * Definitions and outline for events entity
 *
 * @author Udip Shrestha
 */

@Entity
@Table(name="events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String name;


    @Column(name = "date")
    @JsonFormat(pattern = "M-d-yy") // Specify the format "mm-dd-yyyy"
    private LocalDate date;

    @Column(name = "startTime")
    @JsonFormat(pattern = "h:mm a") // Specify the format "mm-dd-yyyy"
    private LocalTime startTime;

    @Column(name = "endTime")
    @JsonFormat(pattern = "h:mm a") // Specify the format "mm-dd-yyyy"
    private LocalTime endTime;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @Column(name = "cost")
    private int cost;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image eventImage;


    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();



    @ManyToOne
    @JoinColumn(name = "orgId")
    @JsonIgnore
    @JsonBackReference
    private Org org;


    @ManyToMany
    @JoinTable(
            name = "event_user_rsvp",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users_rsvp;


    @OneToMany
    @JsonIgnore
    @JsonManagedReference
    private List<Review> reviews;


    public Event(){
        //user_public_rsvp = new ArrayList<>();
        users_rsvp = new ArrayList<>();
        reviews = new ArrayList<>();
    }

    public Event(String name, LocalDate date, LocalTime endTime, LocalTime startTime, String location, String organizer, String description, int cost){
        this.name = name;
        this.location = location;
        this.description = description;
        this.cost = cost;
        this.date = date;
        this.endTime = endTime;
        this.startTime = startTime;
        users_rsvp = new ArrayList<>();
        reviews = new ArrayList<>();
        //user_public_rsvp = new ArrayList<>();
    }


    public Long getEventId(){
        return id;
    }

    public void setEventId(Long id){
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {this.location = location;}

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Org getEventOrg() {
        return org;
    }

    public void setEventOrg(Org org) {
        this.org = org;
    }

//    public String getStartDateTimeStr() {
//        return this.startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));
//    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

//    public String getEndDateTimeStr() {
//        return this.endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));
//    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

//    public List<User> getUserRsvp() {
//        return users_rsvp;
//    }

    public List<User> getUserRsvp() {
        if (users_rsvp == null){
            return null;
        }
        else{
            List<User> userRSVP = new ArrayList<>();
            for (User user : users_rsvp){
                user.setEventsRsvp(null);
                userRSVP.add(user);
            }
            return userRSVP;
        }

    }

    public void setUserRsvp(List<User> users_rsvp) {
        this.users_rsvp = users_rsvp;
    }

    public void addUserRsvp(User users_rsvp){
        this.users_rsvp.add(users_rsvp);
    }

    public void deleteUserRsvp(User users_rsvp){
        this.users_rsvp.remove(users_rsvp);
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




    @Override
    public String toString() {
        return id + " "
                + name + " "
                + date.toString() + " "
                + startTime.toString() + " "
                + endTime.toString() + " "
                + location + " "
                + description + " "
                + cost;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Image getEventImage() {
        return eventImage;
    }

    public void setEventImage(Image eventImage) {
        this.eventImage = eventImage;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
