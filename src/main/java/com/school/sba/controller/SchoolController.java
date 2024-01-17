package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.service.SchoolService;

@RestController
public class SchoolController {
	
	@Autowired
	SchoolService schoolService;

	@PostMapping(path = "/users/{userId}/schools")
	public Object registerSchool(@PathVariable int userid,@RequestBody SchoolRequest schoolRequest) {
		return schoolService.registerSchool(userid,schoolRequest);
	}
}
