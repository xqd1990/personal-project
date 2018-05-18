package uk.ac.le.qx16.pp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	@Transactional
	public User updateUser(User user) {
		// TODO Auto-generated method stub
		User u = userRepository.findOne(user.getId());
		u.setBirth(user.getBirth());
		u.setEmail(user.getEmail());
		u.setFirstname(user.getFirstname());
		u.setGender(user.getGender());
		u.setLastname(user.getLastname());
		u.setPostcode(user.getPostcode());
		u.setPwd(user.getPwd());
		u.setTel(user.getTel());
		return u;
	}

	@Override
	public User checkEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email);
	}
	
}
