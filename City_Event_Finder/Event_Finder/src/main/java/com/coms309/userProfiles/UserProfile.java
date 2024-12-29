package com.coms309.userProfiles;

import com.coms309.events.Event;
import com.coms309.images.Image;
import com.coms309.orgs.Org;
import com.coms309.reviews.Review;
import com.coms309.users.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**\
 * Definitions and outline for user profile entity
 *
 * @author Corey Heithoff
 */

@Entity
@Table(name="userProfile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private List<Integer> publicRSVPs;

    @OneToOne
    @JsonIgnore
    private User user;


    @OneToOne(cascade = CascadeType.ALL) // Define a one-to-one relationship with Image
    @JoinColumn(name = "profile_image_id") // Foreign key column in the User table
    private Image profileImage;


    @Column(name = "bio")
    private String bio;

    @Column(name = "perferredName")
    private String perfName;

    @Column(name = "contactInfo")
    private String contactInfo;

    @Column(name = "socialMedia")
    private String socialMedia;

    @Column(name = "hobbies")
    private String hobbies;

    @Column(name = "birthday")
    @JsonFormat(pattern = "M-d-yy")
    private LocalDate birthday;


    public UserProfile(){
        this.publicRSVPs = new ArrayList<>();
    }

    public UserProfile(String bio, String contactInfo, String perferedName, String socialMedia, String hobbies, LocalDate birthday){
        this.bio = bio;
        this.contactInfo = contactInfo;
        this.perfName = perferedName;
        this.socialMedia = socialMedia;
        this.hobbies = hobbies;
        this.birthday = birthday;
        this.publicRSVPs = new ArrayList<>();
    }


    public Long getUserProfId(){
        return id;
    }

    public void setUserProfId(Long id){
        this.id = id;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public String getUserProfBio() {
        return bio;
    }

    public void setUserProfBio(String bio) {
        this.bio = bio;
    }

    public String getUserPerfName() {
        return perfName;
    }

    public void setUserPerfName(String perfName) {
        this.perfName = perfName;
    }

    public String getUserProfContactInfo() {
        return contactInfo;
    }

    public void setUserProfContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getUserSocialMedia() {
        return socialMedia;
    }

    public void setUserSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public String getUserHobbies() {
        return hobbies;
    }

    public void setUserHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }


    public List<Integer> getPublicRSVP() {
        return publicRSVPs;
    }

    public void setPublicRSVP(List<Integer> publicRSVPs) {
        this.publicRSVPs = publicRSVPs;
    }

    public void addPublicRSVP(Integer eventid){
        this.publicRSVPs.add(eventid);
    }

    public void deletePublicRSVP(Integer eventid){
        this.publicRSVPs.remove(eventid);
    }

    public void setProfileImage(Image savedImage) {
        this.profileImage = savedImage;
    }

    public Image getProfileImage() {
        return this.profileImage;
    }

}
