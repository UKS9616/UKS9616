package com.coms309.orgProfiles;

import com.coms309.images.Image;
import com.coms309.orgs.Org;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Blob;


/**\
 * Definitions and outline for organization profile entity
 */

@Entity
@Table(name="orgProfile")
public class OrgProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
     * @JsonIgnore is to assure that there is no infinite loop while returning either user/laptop objects (laptop->user->laptop->...)
     */
    @OneToOne
    @JsonIgnore
    private Org org;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;


    @Column(name = "bio")
    private String bio;

    @Column(name = "location")
    private String location;

    @Column(name = "contactInfo")
    private String contactInfo;

    @Column(name = "rating")
    private double rating;

    @Column(name = "numOfRatings")
    private int numOfRatings;


    public OrgProfile(){
        this.rating = 5.0;
        this.numOfRatings = 0;
    }

    public OrgProfile(String bio, String contactInfo, String location){
        this.bio = bio;
        this.contactInfo = contactInfo;
        this.location = location;
        this.rating = 5.0;
        this.numOfRatings = 0;
    }


    public Long getOrgProfId(){
        return id;
    }

    public void setOrgProfId(Long id){
        this.id = id;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getContactInfo() {
        return this.contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {this.location = location;}

    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {this.rating = rating;}

    public int getNumOfRatings() {
        return this.numOfRatings;
    }

    public void setNumOfRatings(int numOfRatings) {this.numOfRatings = numOfRatings;}

    public Org getOrg(){
        return org;
    }

    public void setOrg(Org org){
        this.org = org;
    }




    public void setProfileImage(Image savedImage) {
        this.profileImage = savedImage;
    }

    public Image getProfileImage() {
        return this.profileImage;
    }


}
