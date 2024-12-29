package com.coms309.orgs;

import com.coms309.admin.Admin;
import com.coms309.events.Event;
import com.coms309.orgProfiles.OrgProfile;
import com.coms309.users.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**\
 * Definitions and outline for organization entity
 *
 * @author Corey Heithoff
 */

@Entity
@Table(name="organizations")
public class Org {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String orgName;

    @Column(name = "email")
    private String orgEmail;

    @Column(name = "address")
    private String orgAddress;

    @Column(name = "acceptToken")
    private String acceptToken;
    // acceptToken = "1" means pending
    // acceptToken = "2" means accepted
    // acceptToken = "3" means denied

    @Column(name = "acceptDate")
    @JsonFormat(pattern = "M-d-yy")
    private LocalDate orgAcceptDate;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orgProfId")
    private OrgProfile orgProf;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany
    @JsonIgnore
    @JsonManagedReference
    private List<Event> events;


    public Org(){
        events = new ArrayList<>();
        this.acceptToken = "1";
        //this.orgAcceptDate = LocalDateTime.now();
    }

    public Org(String orgName, String orgEmail, String orgAddress, LocalDate orgAcceptDate){
        this.orgName = orgName;
        this.orgEmail = orgEmail;
        this.orgAddress = orgAddress;
        this.acceptToken = "1";
        this.orgAcceptDate = orgAcceptDate; //LocalDateTime.now();
        events = new ArrayList<>();
    }


    public Long getOrgId(){
        return id;
    }

    public void setOrgId(Long id){
        this.id = id;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgEmail() {
        return this.orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getOrgAddress() {
        return this.orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getAcceptToken() {
        return this.acceptToken;
    }

    public void setAcceptToken(String acceptToken) {this.acceptToken = acceptToken;}

//    public String getOrgAcceptDate() {
//        return this.orgAcceptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));
//    }

    public LocalDate getOrgAcceptDate() {
        return this.orgAcceptDate;
    }

    public void setOrgAcceptDate(LocalDate acceptDate) {
        this.orgAcceptDate = acceptDate;
    }

    public OrgProfile getOrgProf(){
        return orgProf;
    }

    public void setOrgProf(OrgProfile orgProf){
        this.orgProf = orgProf;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> event) {
        this.events = event;
    }

    public void addEvents(Event event){
        this.events.add(event);
    }

    public void deleteEvents(Event event){
        this.events.remove(event);
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }


    @Override
    public String toString() {
        return id + " "
                + orgName + " "
                + orgEmail + " "
                + orgAddress + " "
                + acceptToken + " "
                + orgAcceptDate.toString();
    }


}
