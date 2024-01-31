package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduledJobs {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AcademicProgramService programService;
//
//	@Scheduled(fixedDelay = 1000L)
//	public void test() {
//		System.out.println("Scheduled job");
//	}

	@Scheduled(fixedDelay = 10000L)	
	public void delete() {
		String msgString =userService.deleteUser();
		log.info(msgString);
		
		log.info(programService.deleteAcademicProgram());
	}

}
