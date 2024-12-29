package com.coms309.orgs;

import com.coms309.events.Event;
import com.coms309.events.EventController;
import com.coms309.events.EventRepository;
import com.coms309.orgProfiles.OrgProfileController;
import com.coms309.notifServer.NotificationServer;
import com.coms309.notifServer.UserSession;
import com.coms309.orgProfiles.OrgProfile;
import com.coms309.orgProfiles.OrgProfileRepository;
import com.coms309.reviews.Review;
import com.coms309.users.JsonStringResponse;
import com.coms309.users.User;
import com.coms309.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller used for organizations
 *
 * @author Corey Heithoff
 */

@RestController
public class OrgController {

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrgProfileRepository orgProfRepository;

    @Autowired
    private OrgProfileController orgProfContr;

    @Autowired
    private EventController eventContr;

    @Autowired
    private EventRepository eventRepository;


    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/org")
    public List<Org> getAllOrgs(){
        return orgRepository.findAll();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/org/{id}")
    public Org getOrgById( @PathVariable int id){
        return orgRepository.findById(id);
    }

        // THIS IS THE READ OPERATION
        @GetMapping(path = "/org/token/{accessToken}")
        public Org getOrgByAccessToken( @PathVariable String accessToken){
            User user = userRepository.findByLoginToken(accessToken);
            if(user == null)
                return null;
            if(user.getOrg() == null)
                return null;
            return user.getOrg();
        }

        // THIS IS THE LIST OPERATION
        @GetMapping(path = "/org/{id}/event")
        public List<Event> getAllOrgEvents( @PathVariable int id){
            Org org = orgRepository.findById(id);
            if(org == null)
                return null;
            return org.getEvents();
        }

    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/org/token/{accessToken}/event")
    public List<Event> getAllOrgEventsByToken( @PathVariable String accessToken){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null)
            return null;
        if(user.getOrg() == null)
            return null;
        return user.getOrg().getEvents();
    }

        // THIS IS THE LIST OPERATION
        @GetMapping(path = "/org/accepted")
        public List<Org> getAllAcceptedOrgs(){
            List<Org> orgList = new ArrayList<>();
            for (Org orgCheck : getAllOrgs()){
                if (orgCheck.getAcceptToken().equals("2") ) {
                    orgList.add(orgCheck);
                }
            }
            System.out.println(orgList);
            return orgList;
        }

        // THIS IS THE LIST OPERATION
        @GetMapping(path = "/org/notAccepted")
        public List<Org> getAllNotAcceptedOrgs(){
            List<Org> orgList = new ArrayList<>();
            for (Org orgCheck : getAllOrgs()){
                if (orgCheck.getAcceptToken().equals("1") ) {
                    orgList.add(orgCheck);
                }
            }
            System.out.println(orgList);
            return orgList;
        }


    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/org/search/{orgName}")
    public List<Org> searchOrgs(@PathVariable String orgName){
        List<Org> orgList = new ArrayList<>();
        for (Org orgCheck : getAllOrgs()){
            if (orgCheck.getOrgName().toLowerCase().contains(orgName.toLowerCase()) && orgCheck.getAcceptToken().equals("2")) {
                orgList.add(orgCheck);
            }
        }
        System.out.println(orgList);
        return orgList;
    }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/org")
    public JsonStringResponse createOrg(@RequestBody Org org) {
        System.out.println(org);
        if (org == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        orgRepository.save(org);
        return new JsonStringResponse(Long.toString(org.getOrgId()));
        //return "New organization "+ org.getOrgName() + " created successfully";
    }

    // THIS IS THE CREATE OPERATION that will also create an attached organization profile object
    @PostMapping(path = "/org/profile")
    public JsonStringResponse createOrgAndProfile(@RequestBody Org org) {
        System.out.println(org);
        if (org == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        OrgProfile newProf = new OrgProfile("", org.getOrgEmail(), org.getOrgAddress());
        orgProfRepository.save(newProf);
        orgRepository.save(org);
        newProf.setOrg(org);
        org.setOrgProf(newProf);
        orgRepository.save(org);
        return new JsonStringResponse(Long.toString(org.getOrgId()));
        //return "New organization "+ org.getOrgName() + " created successfully";
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/org/{id}")
    public Org updateOrg(@PathVariable int id, @RequestBody Org request){
        Org org = orgRepository.findById(id);
        if(org == null || request == null)
            return null;
        org.setOrgName(request.getOrgName());
        org.setOrgEmail(request.getOrgEmail());
        org.setOrgAddress(request.getOrgAddress());
        orgRepository.save(org);
        return orgRepository.findById(id);
    }

    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/org/{id}")
    public String deleteOrg(@PathVariable int id){
        Org org = orgRepository.findById(id);
        if(org == null){
            return "failed deleting organization";
        }
        if(org.getOrgProf() != null) {
            orgProfContr.deleteOrgProfile(org.getOrgProf().getOrgProfId().intValue());
        }
        if(org.getUser() != null){
            org.getUser().setOrg(null);
            org.setUser(null);
        }
        if(org.getEvents() != null){
            List<Event> eventsCopy = new ArrayList<>(org.getEvents());
            for (Event event : eventsCopy){
                eventContr.deleteEvent(event.getEventId().intValue());
            }
            org.setEvents(null);
        }

        orgRepository.deleteById(id);
        //System.out.println(orgRepository.count());
        return "organization deleted successfully";
    }

    @PutMapping("/org/{orgId}/profile/{profId}")
    public String assignProfToOrg(@PathVariable int orgId,@PathVariable int profId){
        Org org = orgRepository.findById(orgId);
        OrgProfile orgProf = orgProfRepository.findById(profId);
        if(org == null || orgProf == null)
            return "Failure to assign organization profile";
        orgProf.setOrg(org);
        org.setOrgProf(orgProf);
        orgRepository.save(org);
        return "Organization "+ org.getOrgName() + " profile assigned successfully";
    }

        @PutMapping("/event/{eventId}/org/{orgId}")
        public String assignEventToOrgByID(@PathVariable int orgId,@PathVariable int eventId){
            Org org = orgRepository.findById(orgId);
            Event event = eventRepository.findById(eventId);
            if(org == null || event == null)
                return "Failure to assign organization event";
            event.setEventOrg(org);
            org.addEvents(event);
            orgRepository.save(org);
            return "Organization "+ org.getOrgName() + " event assigned successfully";
        }

        // map event to org by org name: not needed right now and untested
//        @PutMapping("/event/{eventId}/org/{orgName}")
//        String assignEventToOrgByName(@PathVariable String orgName ,@PathVariable int eventId){
//            Org org = OrgRepository.findByOrgName(orgName);
//            Event event = EventRepository.findById(eventId);
//            if(org == null || event == null)
//                return "Failure to assign organization event";
//            event.setEventOrg(org);
//            org.addEvents(event);
//            OrgRepository.save(org);
//            return "Organization "+ org.getOrgName() + " event assigned successfully";
//        }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/event/token/{accessToken}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new event\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    @Operation(summary = "Create an event and org connection", description = "Creates an event in the database according to the Event JSON body parameter connected to the org by the given token")
    public JsonStringResponse createEventandOrgConn(@io.swagger.v3.oas.annotations.parameters.RequestBody(
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
                            " }"))) @RequestBody Event event, @Parameter(description = "Token of user to be searched") @PathVariable String accessToken) {
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null || event == null)
            return new JsonStringResponse("0");
        eventRepository.save(event);
        String response = assignEventToOrgByID(user.getOrg().getOrgId().intValue(), event.getEventId().intValue());
        if (response.contains("Failure")){
            return new JsonStringResponse("0");
        }
        return new JsonStringResponse(Long.toString(event.getEventId()));
    }


}

