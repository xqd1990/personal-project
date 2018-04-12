package uk.ac.le.qx16.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.le.qx16.pp.entities.TwitterUser;

public interface TwitterUserRepository extends JpaRepository<TwitterUser, Long>{

}
