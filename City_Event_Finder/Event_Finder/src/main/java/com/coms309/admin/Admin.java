package com.coms309.admin;


import com.coms309.users.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

import java.time.LocalDate;


/**\
 * Definitions and outline for admin profile entity
 *
 * @author Udip Shrestha
 */

@Entity
@Table(name="admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String adminName;

    @Column(name = "email")
    private String adminEmail;

    @Column(name = "address")
    private String adminAddress;

    // column for city/area



    @Column(name = "creationDate")
    @JsonFormat(pattern = "M-d-yy")
    private LocalDate adminCreationDate;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;



    public Admin(){
    }

    public Admin(String adminName, String adminEmail, String adminAddress, LocalDate adminCreationDate){
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminAddress = adminAddress;
        this.adminCreationDate = adminCreationDate;
    }


    public Long getAdminId(){
        return id;
    }

    public void setAdminId(Long id){
        this.id = id;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return this.adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminAddress() {
        return this.adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }

    public LocalDate getAdminCreationDate() {
        return this.adminCreationDate;
    }

    public void setAdminCreationDate(LocalDate adminCreationDate) {
        this.adminCreationDate = adminCreationDate;
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
                + adminName + " "
                + adminEmail + " "
                + adminAddress + " "
                + adminCreationDate.toString();
    }


}
