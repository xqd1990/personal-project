package uk.ac.le.qx16.pp.service;

import uk.ac.le.qx16.pp.entities.User;

public interface UserService {
	public void userRegister(User user);
	public User userLogin(String email, String pwd);
}
