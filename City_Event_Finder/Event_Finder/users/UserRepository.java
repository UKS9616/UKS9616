package com.coms309.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findById(int id);
    User findByUsername(String username);
    User findByLoginToken(String loginToken);

    @Transactional
    void deleteById(int id);

    List<User> findByUsernameContainingIgnoreCase(String username);
}
