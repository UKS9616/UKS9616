package com.coms309.reviews;


import com.coms309.events.Event;
import com.coms309.users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;




@Entity
@Table(name="review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "rating")
    private double rating;

    @Column(name = "reviewDate")
    @JsonFormat(pattern = "M-d-yy h:mm a")
    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "eventId")
    @JsonIgnore
    @JsonBackReference
    private Event event;



    public Review(){
    }

    public Review(String content, float rating, LocalDateTime reviewDate){
        this.content = content;
        this.rating = rating;
        this.reviewDate = reviewDate;
    }


    public Long getReviewId(){
        return id;
    }

    public void setReviewId(Long id){
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public LocalDateTime getReviewDate() {
        return this.reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public User getReviewUser() {
        return user;
    }

    public void setReviewUser(User user) {
        this.user = user;
    }

    public Event getReviewEvent() {
        return event;
    }

    public void setReviewEvent(Event event) {
        this.event = event;
    }


    @Override
    public String toString() {
        return id + " "
                + content + " "
                + rating + " "
                + reviewDate.toString();
    }


}
