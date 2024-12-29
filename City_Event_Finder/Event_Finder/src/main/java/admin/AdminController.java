package com.coms309.admin;

import com.coms309.events.Event;
import com.coms309.orgs.Org;
import com.coms309.orgs.OrgRepository;
import com.coms309.users.JsonStringResponse;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller used for admins
 *
 * @author Udip Shrestha
 */

@RestController
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrgRepository orgRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/admin")
    public List<Admin> getAllAdmin(){
        return adminRepository.findAll();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/admin/{id}")
    public Admin getAdminById( @PathVariable int id){
        return adminRepository.findById(id);
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/admin/token/{accessToken}")
    public Admin getAdminByAccessToken( @PathVariable String accessToken){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null)
            return null;
        if(user.getAdmin() == null)
            return null;
        return user.getAdmin();
    }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/admin")
    public JsonStringResponse createAdmin(@RequestBody Admin admin) {
        System.out.println(admin);
        if (admin == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        adminRepository.save(admin);
        return new JsonStringResponse(Long.toString(admin.getAdminId()));
        //return "New admin "+ admin.getAdminName() + " created successfully";
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/admin/{id}")
    public Admin updateAdmin(@PathVariable int id, @RequestBody Admin request){
        Admin admin = adminRepository.findById(id);
        if(admin == null || request == null)
            return null;
        admin.setAdminName(request.getAdminName());
        admin.setAdminEmail(request.getAdminEmail());
        admin.setAdminAddress(request.getAdminAddress());
        adminRepository.save(admin);
        return adminRepository.findById(id);
    }

    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/admin/{id}")
    public String deleteAdmin(@PathVariable int id){
        Admin admin = adminRepository.findById(id);
        if(admin == null){
            return "failed deleting admin";
        }
        if(admin.getUser() != null){
            admin.getUser().setAdmin(null);
            admin.setUser(null);
        }
        adminRepository.deleteById(id);
        //System.out.println(adminRepository.count());
        return "Admin deleted successfully";
    }

    @PutMapping("/user/{userId}/admin/{adminId}")
    String assignUserToAdmin(@PathVariable int userId, @PathVariable int adminId){
        Admin admin = adminRepository.findById(adminId);
        User user = userRepository.findById(userId);
        if (admin == null || user == null)
            return "Error: user or admin not found";
        if( (user.getLoginToken().equals("3")) || ((Integer.parseInt(user.getLoginToken()) >= 300000) && (Integer.parseInt(user.getLoginToken()) < 400000)) ){
            user.setAdmin(admin);
            admin.setUser(user);
            adminRepository.save(admin);
            return "User assigned to admin " + admin.getAdminName() + " successfully";
        }
        return "Error: user is not qualified to be an admin";
    }

    @PutMapping("/org/{orgId}/accept")
    public String acceptOrganization(@PathVariable int orgId) {

        Org org = orgRepository.findById(orgId);
// Admin admin = adminRepository.findById(adminId);
if (org == null)
    return "Organization not found";

org.setAcceptToken("2");  // This marks organization as accepted
orgRepository.save(org);
return "Organization accepted by admin.";
    }


}

