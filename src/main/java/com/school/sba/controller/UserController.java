package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.service.UserService;

@RestController
public class UserController {

	@Autowired
	private	UserService userService;

	@PostMapping(path = "/users/register")
	public Object registerUser(@RequestBody @Validated UserRequest userRequest) {
		return userService.register(userRequest);
	}

	@DeleteMapping(path = "/users/{userId}")
	public Object deleteUser(@PathVariable int userId) {
		return userService.deleteUser(userId);
	}

	@GetMapping(path = "/users/{userId}")
	public Object findUserById(@PathVariable int userId) {
		return userService.findUserById(userId);
	}
}
