package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.dto.UserRequest;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.ConstraintViolationException;
import com.school.sba.service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	int count;

	@PostMapping(path = "/users/register")
	public Object registerUser(@RequestBody @Validated UserRequest userRequest) {
		if (userRequest.getUserRole() == UserRole.ADMIN) {
			count += 1;
		}
		if (count == 1 || userRequest.getUserRole()!=UserRole.ADMIN) {
			return userService.register(userRequest);
		} else {
			throw new ConstraintViolationException("There is already an admin", HttpStatus.IM_USED,
					"More than 1 admin is not allowed");
		}
	}

	@DeleteMapping(path = "/users/{userId}")
	public Object deleteUser(@PathVariable int userId) {
		return userService.deleteUser(userId);
	}
}
