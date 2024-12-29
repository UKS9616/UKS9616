package com.coms309.images;

import com.coms309.events.Event;
import com.coms309.events.EventRepository;
import com.coms309.orgs.Org;
import com.coms309.orgs.OrgRepository;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
public class ImageController {


//    @Value("${image.upload.dir}")
//    private String directory;


    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private EventRepository eventRepository;

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);



    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageById(@PathVariable int id) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (imageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Image image = imageOptional.get();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(image.getContentType())) // Use the stored MIME type
                .body(image.getData());
    }



    @PostMapping("/images")
    public String handleFileUpload(@RequestParam("image") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return "No file uploaded.";
        }

        try {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setContentType(imageFile.getContentType());
            image.setData(imageFile.getBytes()); // Save binary data

            imageRepository.save(image);

            return "File uploaded successfully with ID: " + image.getId();
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }



    @PutMapping("/images/{id}")
    public String updateImage(@PathVariable int id, @RequestParam("image") MultipartFile newImageFile) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (imageOptional.isEmpty()) {
            return "Image with ID " + id + " not found.";
        }

        try {
            Image image = imageOptional.get();
            image.setData(newImageFile.getBytes()); // Update binary data
            image.setContentType(newImageFile.getContentType()); // Update content type
            image.setName(newImageFile.getOriginalFilename()); // Update file name

            imageRepository.save(image); // Save the updated image in the database

            return "Image updated successfully.";
        } catch (IOException e) {
            return "Failed to update image: " + e.getMessage();
        }
    }


    @DeleteMapping("/images/{id}")
    public String deleteImage(@PathVariable int id) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (imageOptional.isEmpty()) {
            return "Image with ID " + id + " not found.";
        }

        imageRepository.delete(imageOptional.get()); // Remove the image from the database
        return "Image deleted successfully.";
    }



    @PostMapping("/user/{userId}/image")
    public ResponseEntity<String> uploadUserProfileImage(@PathVariable int userId, @RequestParam("image") MultipartFile imageFile) {
        User user = userRepository.findById(userId);

        // Create and save the image
        try {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setContentType(imageFile.getContentType());
            image.setData(imageFile.getBytes());
            Image savedImage = imageRepository.save(image);

            // Associate the image with the user
            user.getUserProf().setProfileImage(savedImage);
            userRepository.save(user);

            return ResponseEntity.ok("Profile image uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }

    @GetMapping("/user/{userId}/image")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable int userId) {
        Image profileImage = userRepository.findById(userId).getUserProf().getProfileImage();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profileImage.getContentType()))
                .body(profileImage.getData());
    }


    @PutMapping("/user/{userId}/image")
    public ResponseEntity<String> updateUserProfileImage(@PathVariable int userId, @RequestParam("image") MultipartFile imageFile) {
        User user = userRepository.findById(userId);
        Image oldImage = user.getUserProf().getProfileImage();

        try {
            // Create and save the new image
            Image newImage = new Image();
            newImage.setName(imageFile.getOriginalFilename());
            newImage.setContentType(imageFile.getContentType());
            newImage.setData(imageFile.getBytes());
            Image savedImage = imageRepository.save(newImage);

            // Update the user's profile image
            user.getUserProf().setProfileImage(newImage);
            userRepository.save(user);

            // Delete the old image if it exists
            if (oldImage != null) {
                imageRepository.delete(oldImage);
            }

            return ResponseEntity.ok("Profile image updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update image.");
        }
    }


    @DeleteMapping("/user/{userId}/image")
    public ResponseEntity<String> deleteUserProfileImage(@PathVariable int userId) {
        User user = userRepository.findById(userId);
        Image profileImage = user.getUserProf().getProfileImage();
        if (profileImage != null) {
            user.getUserProf().setProfileImage(null);
            userRepository.save(user);
            imageRepository.delete(profileImage);
            return ResponseEntity.ok("Profile image deleted successfully.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile image to delete.");
    }




    @PostMapping("/org/{orgId}/image")
    public ResponseEntity<String> uploadOrgProfileImage(@PathVariable int orgId, @RequestParam("image") MultipartFile imageFile) {
        Org organization = orgRepository.findById(orgId);

        // Create and save the image
        try {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setContentType(imageFile.getContentType());
            image.setData(imageFile.getBytes());
            Image savedImage = imageRepository.save(image);

            // Associate the image with the organization
            organization.getOrgProf().setProfileImage(savedImage);
            orgRepository.save(organization);

            return ResponseEntity.ok("Organization profile image uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }


    @GetMapping("/org/{orgId}/image")
    public ResponseEntity<byte[]> getOrgProfileImage(@PathVariable int orgId) {

        Image profileImage = orgRepository.findById(orgId).getOrgProf().getProfileImage();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profileImage.getContentType()))
                .body(profileImage.getData());
    }

    @PutMapping("/org/{orgId}/image")
    public ResponseEntity<String> updateOrgProfileImage(@PathVariable int orgId, @RequestParam("image") MultipartFile imageFile) {

        Org organization = orgRepository.findById(orgId);
        Image oldImage = organization.getOrgProf().getProfileImage();

        try {
            // Create and save the new image
            Image newImage = new Image();
            newImage.setName(imageFile.getOriginalFilename());
            newImage.setContentType(imageFile.getContentType());
            newImage.setData(imageFile.getBytes());
            Image savedImage = imageRepository.save(newImage);

            // Update the organization's profile image
            organization.getOrgProf().setProfileImage(savedImage);
            orgRepository.save(organization);

            // Delete the old image if it exists
            if (oldImage != null) {
                imageRepository.delete(oldImage);
            }

            return ResponseEntity.ok("Organization profile image updated successfully.");
        } catch (IOException e) {
            logger.error("Failed to update organization image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update image.");
        }
    }



    @DeleteMapping("/org/{orgId}/image")
    public ResponseEntity<String> deleteOrgProfileImage(@PathVariable int orgId) {

        Org organization = orgRepository.findById(orgId);
        Image profileImage = organization.getOrgProf().getProfileImage();
        if (profileImage != null) {
            organization.getOrgProf().setProfileImage(null);
            orgRepository.save(organization);
            imageRepository.delete(profileImage);
            return ResponseEntity.ok("Organization profile image deleted successfully.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile image to delete.");
    }

    @PostMapping("/event/{eventId}/image")
    public ResponseEntity<String> uploadEventImage(@PathVariable Long eventId, @RequestParam("image") MultipartFile imageFile) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // Create and save the image
        try {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setContentType(imageFile.getContentType());
            image.setData(imageFile.getBytes());
            Image savedImage = imageRepository.save(image);

            // Associate the image with the event
            event.setEventImage(savedImage);
            eventRepository.save(event);

            return ResponseEntity.ok("Event image uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }

    @GetMapping("/event/{eventId}/image")
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        Image eventImage = event.getEventImage();
        if (eventImage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(eventImage.getContentType()))
                .body(eventImage.getData());
    }

    @PutMapping("/event/{eventId}/image")
    public ResponseEntity<String> updateEventImage(@PathVariable Long eventId, @RequestParam("image") MultipartFile imageFile) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        Image oldImage = event.getEventImage();

        try {
            // Create and save the new image
            Image newImage = new Image();
            newImage.setName(imageFile.getOriginalFilename());
            newImage.setContentType(imageFile.getContentType());
            newImage.setData(imageFile.getBytes());
            Image savedImage = imageRepository.save(newImage);

            // Update the event's image
            event.setEventImage(savedImage);
            eventRepository.save(event);

            // Delete the old image if it exists
            if (oldImage != null) {
                imageRepository.delete(oldImage);
            }

            return ResponseEntity.ok("Event image updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update image.");
        }
    }

    @DeleteMapping("/event/{eventId}/image")
    public ResponseEntity<String> deleteEventImage(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        Image eventImage = event.getEventImage();
        if (eventImage != null) {
            event.setEventImage(null);
            eventRepository.save(event);
            imageRepository.delete(eventImage);
            return ResponseEntity.ok("Event image deleted successfully.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No image found to delete.");
    }


}






