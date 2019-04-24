package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired(required=false)
	private BCryptPasswordEncoder bCryptPasswordEncoder;
		
	public boolean saveUser(User user) {
		if(isExistUser(user.getName())!=null) return false;
		bCryptPasswordEncoder=new BCryptPasswordEncoder();
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return true;
	}
	
	public Iterable<User> getAll(){
		return userRepository.findAll();
	}
	
	public boolean login(String username, String password) {
		bCryptPasswordEncoder=new BCryptPasswordEncoder();
		User user = isExistUser(username);
		if(user!=null) return bCryptPasswordEncoder.matches(password, user.getPassword());
		return false;
	}
	
	public User isExistUser(String username) {
		Iterable<User> listUsers;
		listUsers= userRepository.findAll();
		if(listUsers!=null) {
			for(User user: listUsers) 
				if(user!=null && user.getName().equals(username)) return user;					
		}	
		return null;
	}
}
