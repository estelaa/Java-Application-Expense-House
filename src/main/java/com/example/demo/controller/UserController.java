package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;


@Controller
@RestController
@RequestMapping(path="/user") 
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class UserController {
	
		@Autowired 
		private UserService userService;		

		@PostMapping(path="/save") 
		public @ResponseBody String addNewUser (@RequestBody User user) {		
			if(!userService.saveUser(user)) return "No Saved";
			return "Saved";	
		}
		
		@RequestMapping(value ="/login", method = RequestMethod.POST,
				consumes = MediaType.APPLICATION_JSON_VALUE,
	            produces = MediaType.APPLICATION_JSON_VALUE)
		public @ResponseBody boolean login(@RequestBody User user){					
			return userService.login(user.getName(), user.getPassword());
		}
	
		@GetMapping(path="/all")
		public @ResponseBody Iterable<User> getAllUsers() {
			return userService.getAll();
		}
		
		
		
}
