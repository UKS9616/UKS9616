package com.coms309.events;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    List<Event> pageView(List<Event> eventScope, int pageNum){
        int count = 1;
        List<Event> pageEvent = new ArrayList<>();
        for (Event event : eventScope){
            if ((count <= (pageNum*5)) && (count > (pageNum*5-5))){
                // page #1: count <= 5 and count > 0
                // page #2: count <= 10 and count > 5
                // page #3: count <= 15 and count > 10
                pageEvent.add(event);
            }
            count += 1;
        }
        return pageEvent;
    }
}
