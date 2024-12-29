package com.coms309.reviews;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    List<Review> pageView(List<Review> reviewScope, int pageNum){
        int count = 1;
        List<Review> pageReview = new ArrayList<>();
        for (Review review : reviewScope){
            if ((count <= (pageNum*5)) && (count > (pageNum*5-5))){
                // page #1: count <= 5 and count > 0
                // page #2: count <= 10 and count > 5
                // page #3: count <= 15 and count > 10
                pageReview.add(review);
            }
            count += 1;
        }
        return pageReview;
    }
}

