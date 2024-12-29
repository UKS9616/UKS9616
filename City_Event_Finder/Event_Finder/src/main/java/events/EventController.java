package com.coms309.events;

import com.coms309.images.Image;
import com.coms309.images.ImageRepository;
import com.coms309.notifServer.NotificationServer;
import com.coms309.notifServer.UserSession;
import com.coms309.orgs.Org;
import com.coms309.orgs.OrgController;
import com.coms309.orgs.OrgRepository;
import com.coms309.reviews.Review;
import com.coms309.reviews.ReviewController;
import com.coms309.users.JsonStringResponse;
import com.coms309.users.LoginRequest;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller used for event finder
 *
 * @author Udip Shrestha
 */

@RestController
@Tag(name = "Event Controller", description = "Controller for Event-related operations")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationServer notifServer;

    @Autowired
    private ReviewController reviewContr;

    @Autowired
    private EventService eventService;

    @Autowired
    private ImageRepository imageRepository;


    // THIS IS THE LIST OPERATION
    @Operation(summary = "Get all events", description = "Returns a list of all existing events")
    @GetMapping(path = "/event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event list found",
                            content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Event.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/event/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the event",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    @Operation(summary = "Get an event by id", description = "Returns a specific event by the given id")
    public Event getEventById(@Parameter(description = "id of event to be searched") @PathVariable int id){
        return eventRepository.findById(id);
    }

    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/event/page/{pageNum}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the events",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid pageNum supplied",
                    content = @Content)})
    @Operation(summary = "Get events by page number", description = "Returns a page of events by the given page number")
    public List<Event> getEventByPageNum(@Parameter(description = "number of event page to be searched") @PathVariable int pageNum){
        return eventService.pageView(eventRepository.findAll(), pageNum);
    }

    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/org/{id}/event/page/{pageNum}")
    public List<Event> getOrgEventPage( @PathVariable int id, @PathVariable int pageNum){
        Org org = orgRepository.findById(id);
        if(org == null)
            return null;
        return eventService.pageView(org.getEvents(), pageNum);
    }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new event\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    @Operation(summary = "Create an event", description = "Creates an event in the database according to the Event JSON body parameter")
    public JsonStringResponse createEvent(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Event to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Event.class),
                    examples = @ExampleObject(value = "{ \"name\": \"New Event\", " +
                            "\"location\": \"Location Address\", " +
                            "\"description\": \"Description Text\", " +
                            "\"cost\": \"Entry Fee\", " +
                            "\"date\": \"Event Date\", " +
                            "\"startTime\": \"Event Start Time\", " +
                            "\"endTime\": \"Event End Time\"" +
                            " }"))) @RequestBody Event event) {

        //System.out.println(event);
        if (event == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        eventRepository.save(event);
        return new JsonStringResponse(Long.toString(event.getEventId()));
        //return "New event "+ event.getName() + " created successfully";
    }



    // THIS IS THE UPDATE OPERATION
    @PutMapping("/event/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content) })
    @Operation(summary = "Update an event by id", description = "Update a specific event by id in the database according to the Event JSON body parameter")
    public Event updateEvent(@Parameter(description = "id of event to be updated") @PathVariable int id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Event to update", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Event.class),
                    examples = @ExampleObject(value = "{ \"name\": \"New Event\", " +
                            "\"location\": \"Location Address\", " +
                            "\"description\": \"Description Text\", " +
                            "\"cost\": \"Entry Fee\", " +
                            "\"date\": \"Event Date\", " +
                            "\"startTime\": \"Event Start Time\", " +
                            "\"endTime\": \"Event End Time\"" +
                            " }")))@RequestBody Event request){
        Event event = eventRepository.findById(id);
        if(event == null || request == null)
            return null;
        event.setName(request.getName());
        event.setLocation(request.getLocation());
        event.setDate(request.getDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setDescription(request.getDescription());
        event.setCost(request.getCost());
        eventRepository.save(event);
        return eventRepository.findById(id);
    }


    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/event/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event deleted successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Event deleted successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid event ID provided.")))})
    @Operation(summary = "Delete an event by id", description = "Delete a specific event by the given id")
    public String deleteEvent(@Parameter(description = "id of event to be searched") @PathVariable int id){
        Event event = eventRepository.findById(id);
        if(event == null){
            return "Failed deleting event";
        }
        if(event.getEventOrg() != null){
            event.getEventOrg().deleteEvents(event);
            event.setEventOrg(null);
        }
        if(event.getReviews() != null){
            List<Review> reviewsCopy = new ArrayList<>(event.getReviews());
            for(Review review : reviewsCopy){
                reviewContr.deleteReview(review.getReviewId().intValue());
            }
            event.setReviews(null);
        }
        if(event.getUserRsvp() != null){
            List<User> userRsvpCopy = new ArrayList<>(event.getUserRsvp());
            //System.out.println("\n\nuser RSVPs: " + userRsvpCopy);
            for (User userCopy : userRsvpCopy){
                User user = userRepository.findById(userCopy.getUserId().intValue());
                //System.out.println("\n\nuser details " + user.getUserId() + ": " + user.getEventsRsvp());
                if(user.getEventsRsvp() != null) {
                    user.deleteEventsRsvp(event);
                }
                //System.out.println("\n\nuser details after delete " + user.getUserId() + ": " + user.getEventsRsvp());
            }
            event.setUserRsvp(null);
        }
        if (event.getEventImage() != null) {
            Image profileImage = event.getEventImage();
            event.setEventImage(null);
            imageRepository.delete(profileImage);
        }
        eventRepository.deleteById(id);
        //System.out.println(eventRepository.count());
        return "Event deleted successfully";
    }

    // THIS IS THE READ OPERATION
//        @GetMapping(path = "/event/name/{name}")
//        public Event searchEventName(@PathVariable String name) {
//            for(int i = 1; i <= eventRepository.count(); i++){
//                Event event = eventRepository.findById(i);
//                if(event == null)
//                    continue;
//                if (name.equals(event.getName())) {
//                    return eventRepository.findById(i);
//                }
//            }
//            return null;
//        }

//        // THIS IS THE READ OPERATION
//        @GetMapping(path = "/event/org/{org}")
//        public Event searchEventOrganizer(@PathVariable String org) {
//            for (int i = 1; i <= eventRepository.count(); i++) {
//                Event event = eventRepository.findById(i);
//                if (event == null)
//                    continue;
//                if (org.equals(event.getOrganizer())) {
//                    return eventRepository.findById(i);
//                }
//            }
//            return null;
//        }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/event/{id}/org")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the event's organization",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Org.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    @Operation(summary = "Get an event's organization by id", description = "Returns the JSON body of the organization connected to a specific event by the given id")
    public Org getEventOrgById(@Parameter(description = "id of event to be searched") @PathVariable int id){
        Event event = eventRepository.findById(id);
        if(event == null)
            return null;
        return event.getEventOrg();
    }

    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/event/{id}/userRsvp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the event's user RSVPs",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    @Operation(summary = "Get an event's user RSVPs by id", description = "Returns a list of user that have RSVPed to a specific event by the given id")
    public List<User> getEventRsvps(@Parameter(description = "id of event to be searched") @PathVariable int id){
        Event event = eventRepository.findById(id);
        if(event == null)
            return null;
        return event.getUserRsvp();
    }


        // THIS IS THE LIST OPERATION
        @GetMapping(path = "/event/review/{id}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Found the event's reviews",
                        content = @Content(mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
                @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                        content = @Content)})
        @Operation(summary = "Get an event's reviews by id", description = "Returns a list of reviews for a specific event by the given id")
        public List<Review> getEventReviews(@Parameter(description = "id of event to be searched") @PathVariable int id){
            Event event = eventRepository.findById(id);
            if(event == null)
                return null;
            return event.getReviews();
        }

        @PutMapping("/user/{userId}/event/{eventId}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully RSVPed a user to an event",
                        content = @Content(mediaType = "text/plain",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "User RSVP to event successful"))),
                @ApiResponse(
                        responseCode = "400", description = "Invalid id supplied",
                        content = @Content(mediaType = "text/plain",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Invalid event ID provided.")))})
        @Operation(summary = "RSVP a user to an event by id", description = "RSVPs the user given by the userId to a specific event given by the eventId")
        String assignUserToEvent(@Parameter(description = "id of user to be assigned") @PathVariable int userId,@Parameter(description = "id of event to be assigned") @PathVariable int eventId){
            User user = userRepository.findById(userId);
            Event event = eventRepository.findById(eventId);
            if(user == null || event == null)
                return "Failure to assign user to event";
            event.addUserRsvp(user);
            user.addEventsRsvp(event);
            userRepository.save(user);
            eventRepository.save(event);
            return "User "+ user.getUsername() + " rsvp for event " + event.getName() + " successfully";
        }


        @PutMapping("/user/token/{accessToken}/event/{eventId}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully RSVPed a user to an event",
                        content = @Content(mediaType = "text/plain",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "User RSVP to event successful"))),
                @ApiResponse(
                        responseCode = "400", description = "Invalid parameters supplied",
                        content = @Content(mediaType = "text/plain",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Invalid parameters supplied")))})
        @Operation(summary = "RSVP a user to an event by token", description = "RSVPs the user given by their access token to a specific event given by the eventId")
        String assignUserToEventByToken(@Parameter(description = "token of user to be assigned")@PathVariable String accessToken,@Parameter(description = "id of event to be assigned")@PathVariable int eventId){
            User user = userRepository.findByLoginToken(accessToken);
            Event event = eventRepository.findById(eventId);
            if(user == null || event == null)
                return "Failure to assign user to event";
            event.addUserRsvp(user);
            user.addEventsRsvp(event);
            userRepository.save(user);
            eventRepository.save(event);

            // listening to send real-time notifications
            for (UserSession userAndSession : notifServer.getLoggedInUsers()) {
                if (user.getUsername().equals(userAndSession.getUser())) {
                    try {
                        notifServer.onMessage(userAndSession.getSession(), "/rsvp_confirmation " + user.getUsername() + " " + event.getName());
                    } catch (IOException ignored) {
                    }
                }
            }

            return "User "+ user.getUsername() + " rsvp for event " + event.getName() + " successfully";
        }




//    @GetMapping("/events/location")
//    public ResponseEntity<String> searchEventByLocation(@RequestParam String name) {
//        // Find the event by name in the database
//        Event event = eventRepository.findByName(name);
//
//        if (event == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
//        }
//
//        // Get the address from the event
//        String address = event.getLocation();
//        if (address == null || address.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event address is missing");
//        }
//
//        // Fetch the coordinates from Google Maps API
//        try {
//            String coordinates = googleMapsService.getCoordinates(address);
//            return ResponseEntity.ok(coordinates);
//        } catch (Exception e) {
//            // Log the exception and return a 500 response
//            System.err.println("Error while fetching coordinates: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch coordinates");
//        }
//    }






//    @GetMapping("/events/search")
//    public ResponseEntity<Event> searchEventByName(@RequestParam String name) {
//        Event event = eventRepository.findByName(name);
//
//        if (event != null) {
//            return ResponseEntity.ok(event); // Return event details, including location
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }
//


    @GetMapping("/search")
    @Operation(summary = "Get events by search parameters", description = "Returns a list of events that match the given search parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found searched events",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Event.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public ResponseEntity<List<Event>> searchEvents(
            @Parameter(description = "Search by event name") @RequestParam(required = false) String event,
            @Parameter(description = "Search by events' organization name") @RequestParam(required = false) String org,
            @Parameter(description = "Search by event description") @RequestParam(required = false) String description,
            @Parameter(description = "Search by event location") @RequestParam(required = false) String location,
            @Parameter(description = "Search by event time") @RequestParam(required = false) String time,
            @Parameter(description = "Search by event date") @RequestParam(required = false) String date)

    {
        LocalDate Ldate = null;
        if(date != null && !date.isEmpty()){
            System.out.println(date);
            date = date.replace('T', '-');
            DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yy");
            System.out.println(format);
            System.out.println(date);
            try{
                Ldate = LocalDate.parse(date, format);
            }
            catch(Exception e){
                return ResponseEntity.ok(new ArrayList<>());

            }

        }


        LocalTime Ltime = null;
        if(time != null && !time.isEmpty()){
            System.out.println(time);
            time = time.replace('T', ':');
            DateTimeFormatter format2 = DateTimeFormatter.ofPattern("h:mm a");
            System.out.println(format2);
            System.out.println(time);

            try{
                Ltime = LocalTime.parse(time, format2);
            }
            catch(Exception e){
                return ResponseEntity.ok(new ArrayList<>());

            }

        }

        // Set fields to null if they are empty or blank
        event = (event != null && !event.trim().isEmpty()) ? event : null;
        org = (org != null && !org.trim().isEmpty()) ? org : null;
        location = (location != null && !location.trim().isEmpty()) ? location : null;
        //date = date;
        description = (description != null && !description.trim().isEmpty()) ? description : null;

        System.out.println("Search params - event: " + event + ", org: " + org + ", location: " + location + ", date: " + date + ", description: " + description + "time:" + time);

        List<Event> results = eventRepository.searchEventsByKeyword(event, org, location, Ldate, description, Ltime);
        System.out.println("Results: " + results);
        return ResponseEntity.ok(results);
    }







    @GetMapping("/user/{userId}/event/past")
    public ResponseEntity<List<Event>> getPastEventsForUser(@PathVariable int userId) {
        // Fetch user to ensure user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Fetch RSVP-ed past events for the user
        List<Event> pastEvents = eventRepository.findPastEventsForUser(userId, LocalDateTime.now().toLocalDate());

        // Sort events by date (descending), and if dates are equal, by start time (descending)
        pastEvents.sort((event1, event2) -> {
            int dateComparison = event2.getDate().compareTo(event1.getDate());
            if (dateComparison == 0) {
                return event2.getStartTime().compareTo(event1.getStartTime());
            }
            return dateComparison;
        });

        // Return the sorted list
        return ResponseEntity.ok(pastEvents);
    }


    @GetMapping("/org/{orgId}/event/past")
    public ResponseEntity<List<Event>> getPastEventsForOrg(@PathVariable int orgId) {
        // Fetch organization to ensure it exists
        Org org = orgRepository.findById(orgId);
        if (org == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Fetch past events for the organization
        List<Event> pastEvents = eventRepository.findPastEventsForOrg(orgId, LocalDateTime.now().toLocalDate());

        // Sort events by date (descending), and if dates are equal, by start time (descending)
        pastEvents.sort((event1, event2) -> {
            int dateComparison = event2.getDate().compareTo(event1.getDate());
            if (dateComparison == 0) {
                return event2.getStartTime().compareTo(event1.getStartTime());
            }
            return dateComparison;
        });

        // Return the sorted list
        return ResponseEntity.ok(pastEvents);
    }



}















