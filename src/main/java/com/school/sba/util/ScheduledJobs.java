package com.school.sba.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.ClassHourService;
import com.school.sba.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@SuppressWarnings("unused")
public class ScheduledJobs {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AcademicProgramService programService;
	
	@Autowired
	private ClassHourService classHourService;
	
	@Autowired
	private AcademicProgramRepo academicProgramRepo;
//
//	@Scheduled(fixedDelay = 1000L)
//	public void test() {
//		System.out.println("Scheduled job");
//	}
//
//	@Scheduled(fixedDelay = 10000L)	
//	public void delete() {
//		String msgString =userService.deleteUser();
//		log.info(msgString);
//		
//		log.info(programService.deleteAcademicProgram());
//	}

//	<minute> <hour> <day-of-month> <month> <day-of-week> <command>
//	@Scheduled(cron = "* * * * MON")
	public void generateClasshourEveryMonday() {
		List<AcademicProgram> programs = academicProgramRepo.findAll();
		programs.forEach((program) -> {
			if (program.isAutoRepeat()) {
				classHourService.createClassHoursForNextweek(program.getProgramId());
			}
		});
	}
	
//	@Scheduled(cron = "* /5 * * *")
	public void updateClassStatus() {
		classHourService.updateClassHourStatusToOngoing();
	}
	
//	@Scheduled(cron = "* /30 * * *")
	public void updateClassStatusCompleted() {
		classHourService.updateClassHourStatusToCompleted();
	}
	
}