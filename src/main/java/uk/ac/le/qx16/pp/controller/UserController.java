package uk.ac.le.qx16.pp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.le.qx16.pp.entities.User;
import uk.ac.le.qx16.pp.service.UserService;

@Controller
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/login")
	public String login(String email, String pwd){
		
		return "";
	}
	
	@RequestMapping(value="/register")
	public String register(User user){
		
		return "reg_result";
	}
}
