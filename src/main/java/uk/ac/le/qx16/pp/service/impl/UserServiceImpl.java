package uk.ac.le.qx16.pp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.le.qx16.pp.entities.User;
import uk.ac.le.qx16.pp.repository.UserRepository;
import uk.ac.le.qx16.pp.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	public void userRegister(User user) {
		// TODO Auto-generated method stub
		userRepository.save(user);
	}

	public User userLogin(String email, String pwd) {
		// TODO Auto-generated method stub
		return userRepository.findByEmailAndPwd(email, pwd);
	}
	
}
