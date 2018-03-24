package uk.ac.le.qx16.pp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/user")
public class UserController {
	
	@RequestMapping(value="/login")
	public String login(){
		
		return "";
	}
	
	@RequestMapping(value="/register")
	public String register(){
		
		return "";
	}
}
