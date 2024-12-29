package com.coms309.userProfiles;

import com.coms309.chats.Chat;
import com.coms309.chats.ChatController;
import com.coms309.events.Event;
import com.coms309.events.EventRepository;
import com.coms309.images.Image;
import com.coms309.images.ImageRepository;
import com.coms309.orgProfiles.OrgProfile;
import com.coms309.orgProfiles.OrgProfileRepository;
import com.coms309.orgs.Org;
import com.coms309.users.JsonStringResponse;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class UserProfileController {


    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ChatController chatContr;

    @Autowired
    private ImageRepository imageRepository;


    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/userProfile")
    public List<UserProfile> getAllUserProfile() {
        return userProfileRepository.findAll();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/userProfile/{id}")
    public UserProfile getUserProfileById(@PathVariable int id) {
        return userProfileRepository.findById(id);
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/userProfile/token/{accessToken}")
    public UserProfile getUserProfByAccessToken(@PathVariable String accessToken) {
        User user = userRepository.findByLoginToken(accessToken);
        if (user == null)
            return null;
        if (user.getUserProf() == null)
            return null;
        return user.getUserProf();
    }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/userProfile")
    public JsonStringResponse createUserProf(@RequestBody UserProfile userProf) {
        //System.out.println(userProf);
        if (userProf == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        userProfileRepository.save(userProf);
        return new JsonStringResponse(Long.toString(userProf.getUserProfId()));
        //return "New organization profile created successfully";
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/userProfile/{id}")
    public UserProfile updateUserProfile(@PathVariable int id, @RequestBody UserProfile request) {
        UserProfile userProf = userProfileRepository.findById(id);
        if (userProf == null || request == null)
            return null;
        userProf.setUserProfBio(request.getUserProfBio());
        userProf.setUserHobbies(request.getUserHobbies());
        userProf.setUserProfContactInfo(request.getUserProfContactInfo());
        userProf.setUserSocialMedia(request.getUserSocialMedia());
        userProf.setUserPerfName(request.getUserPerfName());
        userProf.setBirthday(request.getBirthday());
        userProfileRepository.save(userProf);
        return userProfileRepository.findById(id);
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/userProfile/token/{accessToken}")
    public UserProfile updateUserProfileByToken(@PathVariable String accessToken, @RequestBody UserProfile request) {
        User user = userRepository.findByLoginToken(accessToken);
        if (user == null)
            return null;
        if (user.getUserProf() == null)
            return null;
        UserProfile userProf = user.getUserProf();
        if (userProf == null || request == null)
            return null;
        userProf.setUserProfBio(request.getUserProfBio());
        userProf.setUserHobbies(request.getUserHobbies());
        userProf.setUserProfContactInfo(request.getUserProfContactInfo());
        userProf.setUserSocialMedia(request.getUserSocialMedia());
        userProf.setUserPerfName(request.getUserPerfName());
        userProf.setBirthday(request.getBirthday());
        userProfileRepository.save(userProf);
        return user.getUserProf();
    }

    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/userProfile/{id}")
    public String deleteUserProfile(@PathVariable int id) {
        UserProfile userProf = userProfileRepository.findById(id);
        if(userProf == null){
            return "failed deleting user profile";
        }
        if(userProf.getUser() != null){
            userProf.getUser().setUserProf(null);
            userProf.setUser(null);
        }
        if (userProf.getProfileImage() != null) {
            Image profileImage = userProf.getProfileImage();
            userProf.setProfileImage(null);
            imageRepository.delete(profileImage);
        }
        userProfileRepository.deleteById(id);
        return "User Profile deleted successfully";
    }

    @PutMapping("/user/{userId}/profile/{profId}")
    public String assignProfToUser(@PathVariable int userId, @PathVariable int profId) {
        User user = userRepository.findById(userId);
        UserProfile userProf = userProfileRepository.findById(profId);
        if (user == null || userProf == null)
            return "Failure to assign user profile";
        userProf.setUser(user);
        user.setUserProf(userProf);
        userRepository.save(user);
        return "User " + user.getUsername() + " profile assigned successfully";
    }

    @GetMapping(path = "/user/token/{accessToken}/eventsRsvp")
    public List<Event> getEventRsvpsByToken(@PathVariable String accessToken) {
        User user = userRepository.findByLoginToken(accessToken);
        if (user == null)
            return null;
        return user.getEventsRsvp();
    }

    @PutMapping("/userProfile/{id}/publicRSVP")
    public UserProfile updateUserPublicRSVP(@PathVariable int id, @RequestBody JsonListResponse request){
        UserProfile userProf = userProfileRepository.findById(id);
        if(userProf == null || request == null)
            return null;
        userProf.setPublicRSVP(request.getListResponse());
        userProfileRepository.save(userProf);
        return userProfileRepository.findById(id);
    }

    @PutMapping("/userProfile/token/{accessToken}/publicRSVP")
    public UserProfile updateUserPublicRSVPbyToken(@PathVariable String accessToken, @RequestBody JsonListResponse request){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null || request == null)
            return null;
        if(user.getUserProf() == null)
            return null;
        return updateUserPublicRSVP(user.getUserProf().getUserProfId().intValue(), request);
    }

    @GetMapping("/userProfile/{id}/publicRSVP")
    public List<Event> getUserPublicRSVP(@PathVariable int id){
        UserProfile userProf = userProfileRepository.findById(id);
        if(userProf == null)
            return null;
        if(userProf.getPublicRSVP() == null)
            return null;
        List<Event> publicRSVPevents = new ArrayList<>();
        for (Integer eventid : userProf.getPublicRSVP()){
            Event newEvent = eventRepository.findById(eventid);
            if (newEvent != null){
                publicRSVPevents.add(newEvent);
            }
        }
        return publicRSVPevents;
    }

    @GetMapping("/userProfile/token/{accessToken}/publicRSVP")
    public List<Event> getUserPublicRSVPbyToken(@PathVariable String accessToken){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null)
            return null;
        if(user.getUserProf() == null)
            return null;
        return getUserPublicRSVP(user.getUserProf().getUserProfId().intValue());
    }



    // THIS IS THE UPDATE OPERATION
    @PutMapping("/user/rsvpDelete/{id}")
    public User DeleteUserRSVPData(@PathVariable int id) {
        User user = userRepository.findById(id);
        if (user == null)
            return null;
        if (user.getEventsRsvp() != null) {
            List<Event> eventsRsvpCopy = new ArrayList<>(user.getEventsRsvp());
            for (Event eventCopy : eventsRsvpCopy) {
                Event event = eventRepository.findById(eventCopy.getEventId().intValue());
                if (event.getUserRsvp() != null) {
                    event.deleteUserRsvp(user);
                }
            }
            user.setEventsRsvp(null);
        }
        userRepository.save(user);
        return userRepository.findById(id);
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/user/messageDelete/{id}")
    public User DeleteUserMessageData(@PathVariable int id) {
        User user = userRepository.findById(id);
        if (user == null)
            return null;
        if (chatContr.getMessagesSentByUser(user.getUserId()) != null) {
            List<Chat> chatCopy = new ArrayList<>(chatContr.getUserMessageHistoryAll(user.getUserId().intValue()));
            for (Chat chat : chatCopy) {
                chatContr.deleteChats(chat.getId().intValue());
            }
        }
        userRepository.save(user);
        return userRepository.findById(id);
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/user/dataDelete/{id}")
    public User DeleteUserData(@PathVariable int id) {
        User userRSVP = DeleteUserRSVPData(id);
        User userChat = DeleteUserMessageData(id);
        if (userRSVP == null || userChat == null){
            return null;
        }
        return userRepository.findById(id);
    }



//    /**
//     * This is where the userdata and the image should be uploaded, you can observe that there is no @RequestBody,
//     * this is because a file and data cannot be uploaded together with different formats with @RequestBody.
//     * @RequestParam allows for 2 different types of data i.e. file and text. The text is entered as a JSON and then
//     * converted into an Object by the Object mapper and then saved into the database. Note: use form data as body for postman testing
//     * first key will be avatar with assciated file, and the second key will be user with input as a json
//     */
//    @PutMapping(path = "/userProfile/{id}/photo")
//    public String setUserProfPhoto(@PathVariable int id, @RequestParam("orgphoto") MultipartFile orgphoto) throws Exception{
//
//        UserProfile userProfile = userProfileRepository.findById(id);
//
//        // if user id not found it cannot have an avatar associated with it
//        if(userProfile == null || orgphoto == null){
//            return null;
//        }
//
//        // This indicates the file name and the extension of the image i.e. png jpg etc and is required when
//        // the image has to be sent to the front end
//        userProfile.setExtension(orgphoto.getOriginalFilename());
//
//        // update the image for the entity
//        byte[] file = orgphoto.getBytes();
//        SerialBlob blob = new SerialBlob(file);
//        Blob image = blob;
//        userProfile.setUserPhoto(image);
//        userProfileRepository.save(userProfile);
//        return "New organization profile photo upload successfully";
//    }
//
//
//    /**
//     * The Response Entity type is set as <Resource>, which can handle files and images very well
//     * additional header have to be set to tell the front end what type of conent is being sent from the
//     * backend, thus in this example headers are set.
//     */
//    @GetMapping(path = "/userProfile/{id}/photo")
//    ResponseEntity<Resource> getOrgProfPhoto(@PathVariable int id) throws IOException, SQLException {
//
//        UserProfile userProfile = userProfileRepository.findById(id);
//
//        // if user id not found it cannot have an avatar associated with it
//        if(userProfile == null || userProfile.getUserPhoto() == null){
//            return null;
//        }
//
//        // add headers to state that a file is being downloaded
//        HttpHeaders header = new HttpHeaders();
//        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+userProfile.getExtension());
//        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        header.add("Pragma", "no-cache");
//        header.add("Expires", "0");
//
//        // convert blob to bytearray and set it as response
//        int blobLength = (int)userProfile.getUserPhoto().length();
//        byte[] byteArray = userProfile.getUserPhoto().getBytes(1, blobLength);
//        ByteArrayResource data = new ByteArrayResource(byteArray);
//
//        // send the response entity back to the front end with the file
//        return ResponseEntity.ok()
//                .headers(header)
//                .contentLength(blobLength)
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(data);
//    }

    @PutMapping("/userProfile/{id}/publicRSVP/string")
    public UserProfile updateUserPublicRSVPString(@PathVariable int id, @RequestBody JsonStringResponse request){
        UserProfile userProf = userProfileRepository.findById(id);
        if(userProf == null || request == null)
            return null;
        List<Integer> result = new ArrayList<>();
        String[] eventIds = request.getStrResponse().split("\\s+");
        for (String Ids : eventIds) {
            result.add(Integer.parseInt(Ids));
        }
        userProf.setPublicRSVP(result);
        userProfileRepository.save(userProf);
        return userProfileRepository.findById(id);
    }

    @PutMapping("/userProfile/token/{accessToken}/publicRSVP/string")
    public UserProfile updateUserPublicRSVPbyTokenString(@PathVariable String accessToken, @RequestBody JsonStringResponse request){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null || request == null)
            return null;
        if(user.getUserProf() == null)
            return null;
        return updateUserPublicRSVPString(user.getUserProf().getUserProfId().intValue(), request);
    }

}

