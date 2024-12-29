package com.coms309.admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Udip Shrestha
 */

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>{
    Admin findById(int id);

    @Transactional
    void deleteById(int id);
}

