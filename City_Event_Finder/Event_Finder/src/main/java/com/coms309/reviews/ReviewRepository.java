package com.coms309.reviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Corey Heithoff
 */

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    Review findById(int id);

    @Transactional
    void deleteById(int id);
}

