package com.coms309.orgProfiles;


import com.coms309.images.Image;
import com.coms309.images.ImageRepository;
import com.coms309.orgs.Org;
import com.coms309.users.JsonStringResponse;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller used for organization profiles
 *
 * @author Corey Heithoff
 */

@RestController
public class OrgProfileController {

    @Autowired
    private OrgProfileRepository orgProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;


        // THIS IS THE LIST OPERATION
        @GetMapping(path = "/orgProfile")
        public List<OrgProfile> getAllOrgProfile(){
        return orgProfileRepository.findAll();
    }

        // THIS IS THE READ OPERATION
        @GetMapping(path = "/orgProfile/{id}")
        public OrgProfile getOrgProfileById( @PathVariable int id){
        return orgProfileRepository.findById(id);
    }

        // THIS IS THE READ OPERATION
        @GetMapping(path = "/orgProfile/token/{accessToken}")
        public OrgProfile getOrgProfByAccessToken(@PathVariable String accessToken){
            User user = userRepository.findByLoginToken(accessToken);
            if(user == null)
                return null;
            if(user.getOrg() == null  || user.getOrg().getOrgProf() == null)
                return null;
            return user.getOrg().getOrgProf();
        }

        // THIS IS THE CREATE OPERATION
        @PostMapping(path = "/orgProfile")
        public JsonStringResponse createOrgProf(@RequestBody OrgProfile orgProf) {
            System.out.println(orgProf);
            if (orgProf == null)
                return new JsonStringResponse("0"); // telling frontend there was an error
            orgProfileRepository.save(orgProf);
            return new JsonStringResponse(Long.toString(orgProf.getOrgProfId()));
            //return "New organization profile created successfully";
        }

        // THIS IS THE UPDATE OPERATION
        @PutMapping("/orgProfile/{id}")
        public OrgProfile updateOrgProfile(@PathVariable int id, @RequestBody OrgProfile request){
            OrgProfile orgProf = orgProfileRepository.findById(id);
            if(orgProf == null || request == null)
                return null;
            orgProf.setBio(request.getBio());
            orgProf.setLocation(request.getLocation());
            orgProf.setContactInfo(request.getContactInfo());
            orgProfileRepository.save(orgProf);
            return orgProfileRepository.findById(id);
        }

        // THIS IS THE UPDATE OPERATION
        @PutMapping("/orgProfile/token/{accessToken}")
        public OrgProfile updateOrgProfileByToken(@PathVariable String accessToken, @RequestBody OrgProfile request){
            User user = userRepository.findByLoginToken(accessToken);
            if(user == null)
                return null;
            if(user.getOrg() == null  || user.getOrg().getOrgProf() == null)
                return null;
            OrgProfile orgProf = user.getOrg().getOrgProf();
            if(orgProf == null || request == null)
                return null;
            orgProf.setBio(request.getBio());
            orgProf.setLocation(request.getLocation());
            orgProf.setContactInfo(request.getContactInfo());
            orgProfileRepository.save(orgProf);
            return user.getOrg().getOrgProf();
        }

        // THIS IS THE DELETE OPERATION
        @DeleteMapping(path = "/orgProfile/{id}")
        public String deleteOrgProfile(@PathVariable int id){
            OrgProfile orgProf = orgProfileRepository.findById(id);
            if(orgProf == null){
                return "failed deleting organization profile";
            }
            if (orgProf.getOrg() != null){
                orgProf.getOrg().setOrgProf(null);
                orgProf.setOrg(null);
            }
            if (orgProf.getProfileImage() != null) {
                Image profileImage = orgProf.getProfileImage();
                orgProf.setProfileImage(null);
                imageRepository.delete(profileImage);
            }
            orgProfileRepository.deleteById(id);
            //System.out.println(orgProfileRepository.count());
            return "User Profile deleted successfully";
        }


//        /**
//         * This is where the userdata and the image should be uploaded, you can observe that there is no @RequestBody,
//         * this is because a file and data cannot be uploaded together with different formats with @RequestBody.
//         * @RequestParam allows for 2 different types of data i.e. file and text. The text is entered as a JSON and then
//         * converted into an Object by the Object mapper and then saved into the database. Note: use form data as body for postman testing
//         * first key will be avatar with assciated file, and the second key will be user with input as a json
//         */
//        @PutMapping(path = "/orgProfile/{id}/photo")
//        public String uploadLaptopInvoice(@PathVariable int id, @RequestParam("orgphoto") MultipartFile orgphoto) throws Exception{
//
//            OrgProfile orgProfile = orgProfileRepository.findById(id);
//
//            // if user id not found it cannot have an avatar associated with it
//            if(orgProfile == null || orgphoto == null){
//                return null;
//            }
//
//            // This indicates the file name and the extension of the image i.e. png jpg etc and is required when
//            // the image has to be sent to the front end
//            orgProfile.setExtension(orgphoto.getOriginalFilename());
//
//            // update the image for the entity
//            byte[] file = orgphoto.getBytes();
//            SerialBlob blob = new SerialBlob(file);
//            Blob image = blob;
//            orgProfile.setOrgPhoto(image);
//            orgProfileRepository.save(orgProfile);
//            return "New organization profile photo upload successfully";
//        }
//
//
//        /**
//         * The Response Entity type is set as <Resource>, which can handle files and images very well
//         * additional header have to be set to tell the front end what type of conent is being sent from the
//         * backend, thus in this example headers are set.
//         */
//        @GetMapping(path = "/orgProfile/{id}/photo")
//        ResponseEntity<Resource> getOrgProfPhoto(@PathVariable int id) throws IOException, SQLException {
//
//            OrgProfile orgProfile = orgProfileRepository.findById(id);
//
//            // if user id not found it cannot have an avatar associated with it
//            if(orgProfile == null || orgProfile.getOrgPhoto() == null){
//                return null;
//            }
//
//            // add headers to state that a file is being downloaded
//            HttpHeaders header = new HttpHeaders();
//            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+orgProfile.getExtension());
//            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
//            header.add("Pragma", "no-cache");
//            header.add("Expires", "0");
//
//            // convert blob to bytearray and set it as response
//            int blobLength = (int)orgProfile.getOrgPhoto().length();
//            byte[] byteArray = orgProfile.getOrgPhoto().getBytes(1, blobLength);
//            ByteArrayResource data = new ByteArrayResource(byteArray);
//
//            // send the response entity back to the front end with the file
//            return ResponseEntity.ok()
//                    .headers(header)
//                    .contentLength(blobLength)
//                    .contentType(MediaType.parseMediaType("application/octet-stream"))
//                    .body(data);
//        }

}

