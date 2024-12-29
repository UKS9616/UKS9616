package com.coms309.reviews;

import com.coms309.events.Event;
import com.coms309.events.EventRepository;
import com.coms309.users.JsonStringResponse;
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
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller used for reviews
 *
 * @author Corey Heithoff
 */

@RestController
@Tag(name = "Review Controller", description = "Controller for Review-related operations")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewService reviewService;


    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/review")
    @Operation(summary = "Get all reviews", description = "Returns a list of all existing reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<Review> getAllReview(){
        return reviewRepository.findAll();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/review/{id}")
    @Operation(summary = "Get a review by id", description = "Returns a specific review by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the review",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Review.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public Review getReviewById(@Parameter(description = "ID of review to be searched") @PathVariable int id){
        return reviewRepository.findById(id);
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/review/page/{pageNum}")
    @Operation(summary = "Get reviews by page number", description = "Returns a page of reviews by given page number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review page found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<Review> getReviewByPage(@Parameter(description = "Number of review page to be searched") @PathVariable int pageNum){
        return reviewService.pageView(reviewRepository.findAll(), pageNum);
    }

    // THIS IS THE LIST OPERATION
    @GetMapping(path = "/event/review/{id}/page/{pageNum}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found an event's review page",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    @Operation(summary = "Get an event's review page by id", description = "Returns a page of reviews for a specific event by the given id")
    public List<Review> getEventReviewPage(@Parameter(description = "id of event to be searched") @PathVariable int id,
                                           @Parameter(description = "Number of review page to be searched") @PathVariable int pageNum){
        Event event = eventRepository.findById(id);
        if(event == null)
            return null;
        if(event.getReviews() == null)
            return null;
        return reviewService.pageView(event.getReviews(), pageNum);
    }



    // THIS IS THE READ OPERATION
    @GetMapping(path = "/review/token/{accessToken}")
    @Operation(summary = "Get a review by token", description = "Returns all reviews for a specific user given by their access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review list found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<Review> getReviewByAccessToken(@Parameter(description = "Token of users' reviews to be searched") @PathVariable String accessToken){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null)
            return null;
        if(user.getReviews() == null)
            return null;
        return user.getReviews();
    }

    // THIS IS THE READ OPERATION
    @GetMapping(path = "/review/page/{pageNum}/token/{accessToken}")
    @Operation(summary = "Get review page by token", description = "Returns review page for a specific user given by their access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review page found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content)})
    public List<Review> getReviewPageByAccessToken(@Parameter(description = "Token of users' reviews to be searched") @PathVariable String accessToken,
                                                   @Parameter(description = "Number of review page to be searched") @PathVariable int pageNum){
        User user = userRepository.findByLoginToken(accessToken);
        if(user == null)
            return null;
        if(user.getReviews() == null)
            return null;
        return reviewService.pageView(user.getReviews(), pageNum);
    }

    // THIS IS THE CREATE OPERATION
    @PostMapping(path = "/review")
    @Operation(summary = "Create a review", description = "Creates a review in the database according to the Review JSON body parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new review\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse createReview(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Review to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class),
                    examples = @ExampleObject(value = "{ \"content\": \"Review message\", " +
                            "\"rating\": \"event rating\", " +
                            "\"reviewDate\": \"date of review creation\"" +
                            "}"))) @RequestBody Review review) {
        //System.out.println(review);
        if (review == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        reviewRepository.save(review);
        return new JsonStringResponse(Long.toString(review.getReviewId()));
    }

    // THIS IS THE UPDATE OPERATION
    @PutMapping("/review/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the review",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Review.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    @Operation(summary = "Update a review by id", description = "Update a specific review by id in the database according to the Review JSON body parameter")
    public Review updateReview(@Parameter(description = "ID of review to be updated") @PathVariable int id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Review to update", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class),
                    examples = @ExampleObject(value = "{ \"content\": \"Review message\", " +
                            "\"rating\": \"event rating\", " +
                            "\"reviewDate\": \"date of review creation\"" +
                            "}"))) @RequestBody Review request){
        Review review = reviewRepository.findById(id);
        if(review == null || request == null)
            return null;
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setReviewDate(request.getReviewDate());
        reviewRepository.save(review);
        return reviewRepository.findById(id);
    }

    // THIS IS THE DELETE OPERATION
    @DeleteMapping(path = "/review/{id}")
    @Operation(summary = "Delete a review by id", description = "Delete a specific review from the database by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review deleted successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Review deleted successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid review ID provided.")))})
    public String deleteReview(@Parameter(description = "ID of review to be deleted") @PathVariable int id){
        Review review = reviewRepository.findById(id);
        if(review == null){
            return "failed deleting review";
        }
        if(review.getReviewUser() != null){
            review.getReviewUser().deleteReview(review);
            review.setReviewUser(null);
        }
        if(review.getReviewEvent() != null){
            review.getReviewEvent().deleteReview(review);
            review.setReviewEvent(null);
        }
        reviewRepository.deleteById(id);
        //System.out.println(reviewRepository.count());
        return "Review deleted successfully";
    }

    @PutMapping("/review/{reviewId}/user/{userId}")
    @Operation(summary = "Assign a review to a user by id", description = "Assign the review given by the reviewId to a specific user given by the userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review assigned to user successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Review assigned to user successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid ID provided.")))})
    String assignUserToReview(@Parameter(description = "ID of review to be assigned") @PathVariable int reviewId, @Parameter(description = "ID of user to be assigned") @PathVariable int userId){
        Review review = reviewRepository.findById(reviewId);
        User user = userRepository.findById(userId);
        if (review == null || user == null)
            return "Error: user or review not found";

        user.addReview(review);
        review.setReviewUser(user);
        reviewRepository.save(review);
        return "User assigned to review #" + review.getReviewId() + " successfully";

    }

    @PutMapping("/review/{reviewId}/user/token/{accessToken}")
    @Operation(summary = "Assign a review to a user by token", description = "Assign the review given by the reviewId to a specific user given by their access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review assigned to user successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Review assigned to user successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid parameters provided.")))})
    String assignUserToReviewByToken(@Parameter(description = "ID of review to be assigned") @PathVariable int reviewId, @Parameter(description = "Token of user to be assigned") @PathVariable String accessToken){
        Review review = reviewRepository.findById(reviewId);
        User user = userRepository.findByLoginToken(accessToken);
        if (review == null || user == null)
            return "Error: user or review not found";

        user.addReview(review);
        review.setReviewUser(user);
        reviewRepository.save(review);
        return "User assigned to review #" + review.getReviewId() + " successfully";

    }

    @PutMapping("/review/{reviewId}/event/{eventId}")
    @Operation(summary = "Assign a review to an event by id", description = "Assign the review given by the review Id to a specific event given by the event Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review assigned to event successfully",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Review assigned to event successfully"))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid ID provided.")))})
    String assignEventToReview(@Parameter(description = "ID of review to be assigned") @PathVariable int reviewId, @Parameter(description = "ID of event to be assigned")@PathVariable int eventId){
        Review review = reviewRepository.findById(reviewId);
        Event event = eventRepository.findById(eventId);
        if (review == null || event == null)
            return "Error: event or review not found";

        event.addReview(review);
        review.setReviewEvent(event);
        reviewRepository.save(review);

        // updating the rating the of the organization hosting the event
        if(event.getEventOrg() == null){
            return "Error: organization not found";
        }
        if(event.getEventOrg().getOrgProf() == null){
            return "Error: organization profile not found";
        }
        double oldRating = event.getEventOrg().getOrgProf().getRating();
        int oldNumOfRatings = event.getEventOrg().getOrgProf().getNumOfRatings();
        double newRating = ( ( oldRating * oldNumOfRatings ) + review.getRating() ) / ( oldNumOfRatings + 1 );
        event.getEventOrg().getOrgProf().setRating(newRating);
        event.getEventOrg().getOrgProf().setNumOfRatings(oldNumOfRatings + 1);
        eventRepository.save(event);

        return "Event assigned to review #" + review.getReviewId() + " successfully";

    }

    // This endpoint creates a review and assigns its table connections
    @PostMapping(path = "/review/user/{userId}/event/{eventId}")
    @Operation(summary = "Create a review and assign it to user and event by id", description = "Creates a review according to the Review JSON body parameter and assigns it to a specific user and event given by the userId and eventId, respectively")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review created and assigned successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new review\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse createReviewAndConnections(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Review to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class),
                    examples = @ExampleObject(value = "{ \"content\": \"Review message\", " +
                            "\"rating\": \"event rating\", " +
                            "\"reviewDate\": \"date of review creation\"" +
                            "}")))@RequestBody Review review
                            , @Parameter(description = "ID of user to be assigned") @PathVariable int userId
                            , @Parameter(description = "ID of event to be assigned")@PathVariable int eventId) {
        System.out.println(review);
        if (review == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        User user = userRepository.findById(userId);
        Event event = eventRepository.findById(eventId);
        if (user == null || event == null)
            return new JsonStringResponse("0");
        reviewRepository.save(review);

        String response = assignUserToReview(review.getReviewId().intValue(), userId);
        String response2 = assignEventToReview(review.getReviewId().intValue(), eventId);
        if( response.startsWith("Error") || response2.startsWith("Error")){
            return new JsonStringResponse("0");
        }

        return new JsonStringResponse(Long.toString(review.getReviewId()));
    }

    // This endpoint creates a review and assigns its table connections using the user's token
    @PostMapping(path = "/review/event/{eventId}/user/token/{accessToken}")
    @Operation(summary = "Create a review and assign it to user and event by token", description = "Creates a review according to the Review JSON body parameter and assigns it an event by the given eventId and a specific user given by their access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review created and assigned successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"id of new review\"}")) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonStringResponse.class),
                            examples = @ExampleObject(value = "{ \"strResponse\": \"error message\"}")) }) })
    public JsonStringResponse createReviewAndConnectionsByToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Review to create", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class),
                    examples = @ExampleObject(value = "{ \"content\": \"Review message\", " +
                            "\"rating\": \"event rating\", " +
                            "\"reviewDate\": \"date of review creation\"" +
                            "}"))) @RequestBody Review review
                            , @Parameter(description = "ID of event to be assigned") @PathVariable int eventId
                            , @Parameter(description = "Token of user to be assigned") @PathVariable String accessToken) {
        System.out.println(review);
        if (review == null)
            return new JsonStringResponse("0"); // telling frontend there was an error
        User user = userRepository.findByLoginToken(accessToken);
        Event event = eventRepository.findById(eventId);
        if (user == null || event == null)
            return new JsonStringResponse("0");
        reviewRepository.save(review);

        String response = assignUserToReviewByToken(review.getReviewId().intValue(), accessToken);
        String response2 = assignEventToReview(review.getReviewId().intValue(), eventId);
        if( response.startsWith("Error") || response2.startsWith("Error")){
            return new JsonStringResponse("0");
        }

        return new JsonStringResponse(Long.toString(review.getReviewId()));
    }



}

