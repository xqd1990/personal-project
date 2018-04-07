package uk.ac.le.qx16.pp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import uk.ac.le.qx16.pp.entities.User;
import uk.ac.le.qx16.pp.service.UserService;

@Controller
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/login")
	public String login(String email, String pwd, HttpServletRequest req){
		User user = null;
		try{
			user = userService.userLogin(email, pwd);
			if(user!=null){
				req.getSession().setAttribute("user", user);
			}else{
				req.setAttribute("error", "Email or Password incorrect!");
			}
		}catch(RuntimeException e){
			req.setAttribute("error", "Some errors!");
		}
		return "forward:/index.jsp";
	}
	
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public String register(User user, HttpServletRequest req){
		try{
			userService.userRegister(user);
			req.setAttribute("reg_result", "Successfully Registered!");
		}catch(RuntimeException e){
			req.setAttribute("reg_result", e.getMessage());
		}
		return "register_result";
	}
	
	@RequestMapping(value="/logout")
	public String register(HttpServletRequest req){
		req.getSession().removeAttribute("user");
		return "redirect:/index.jsp";
	}
	
	
}
