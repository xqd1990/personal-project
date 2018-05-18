package uk.ac.le.qx16.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.ac.le.qx16.pp.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	public User findByEmailAndPwd(String email, String pwd);
	public User findByEmail(String email);
}
