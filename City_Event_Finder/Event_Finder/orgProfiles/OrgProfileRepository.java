package com.coms309.orgProfiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Corey Heithoff
 */

@Repository
public interface OrgProfileRepository extends JpaRepository<OrgProfile, Long>{
    OrgProfile findById(int id);

    @Transactional
    void deleteById(int id);
}
