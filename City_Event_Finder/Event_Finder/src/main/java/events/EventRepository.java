package com.coms309.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{
    Event findById(int id);

    @Transactional
    void deleteById(int id);


    Event findByName(String name);


//    List<Event> findByNameContainingIgnoreCaseOrOrg_OrgNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrDate(
//            String name, String orgName, String location, LocalDate date
//    );



    @Transactional
    @Query("SELECT e FROM Event e " +
            "LEFT JOIN e.org o " +
            "WHERE (:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:org IS NULL OR (o IS NOT NULL AND LOWER(o.orgName) LIKE LOWER(CONCAT('%', :org, '%')))) " +
            "AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
            "AND (:time IS NULL OR e.startTime = :time)" +
            "AND (:date IS NULL OR e.date = :date)")

    List<Event> searchEventsByKeyword(
            @Param("keyword") String event,
            @Param("org") String org,
            @Param("location") String location,
            @Param("date") LocalDate date,
            @Param("description") String description,
            @Param("time") LocalTime time

    );


    @Query("SELECT e FROM Event e JOIN e.users_rsvp u WHERE u.id = :userId AND e.date < :currentDate ORDER BY e.date DESC")
    List<Event> findPastEventsForUser(@Param("userId") int userId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT e FROM Event e WHERE e.org.id = :orgId AND e.date < :currentDate ORDER BY e.date DESC, e.startTime DESC")
    List<Event> findPastEventsForOrg(@Param("orgId") int orgId, @Param("currentDate") LocalDate currentDate);


}

