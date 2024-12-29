package com.coms309.userProfiles;

import com.coms309.orgProfiles.OrgProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
    UserProfile findById(int id);

    @Transactional
    void deleteById(int id);
}
