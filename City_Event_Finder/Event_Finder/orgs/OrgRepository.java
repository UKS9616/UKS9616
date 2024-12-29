package com.coms309.orgs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Corey Heithoff
 */

@Repository
public interface OrgRepository extends JpaRepository<Org, Long>{
    Org findById(int id);
    Org findByOrgName(String orgName);

    @Transactional
    void deleteById(int id);
}
