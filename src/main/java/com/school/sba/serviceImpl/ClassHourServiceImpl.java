package com.school.sba.serviceImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.ConstraintViolationException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private ClassHourRepo classHourRepo;

	@Autowired
	private SubjectRepo subjectRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ResponseStructure<String> structure;

//	@Autowired
//	private ResponseStructure<List<ClassHour>> structure2;

	private boolean isBreakTime(LocalDateTime beginsAt, LocalDateTime endsAt, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();

		return ((breakTimeStart.isAfter(beginsAt.toLocalTime()) && breakTimeStart.isBefore(endsAt.toLocalTime()))
				|| breakTimeStart.equals(beginsAt.toLocalTime()));
	}

	private boolean isLunchTime(LocalDateTime beginsAt, LocalDateTime endsAt, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		beginsAt = beginsAt.minusHours(12);
		return (lunchTimeStart.equals(beginsAt.toLocalTime()));
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> registerClassHour(int programId) {
		return academicProgramRepo.findById(programId).map(academicProgarm -> {
			School school = academicProgarm.getSchool();
			Schedule schedule = school.getSchedule();
			if (schedule != null) {
				int classHourPerDay = schedule.getClassHoursPerDay();
				int classHourLength = (int) schedule.getClassHourInMinutes().toMinutes();

				LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
				LocalDateTime lunchTimeStart = currentTime.with(schedule.getLunchTime());
				LocalDateTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
				LocalDateTime breakTimeStart = currentTime.with(schedule.getBreakTime());
				LocalDateTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
				int days;
				if (currentTime.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
					days = 6;
				} else {
					days = 12;
				}
//				for (int day = currentTime.getDayOfWeek().getValue(); day <= DayOfWeek.SATURDAY.getValue(); day++) {
				for (int day = currentTime.getDayOfWeek().getValue(); day <= days; day++) {
					LocalDateTime beginsAt = currentTime.with(schedule.getOpensAt());
					LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);
					for (int hour = 1; hour <= classHourPerDay + 2; hour++) {
						ClassHour classHour = new ClassHour();
						if (beginsAt.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
							day = day - 1;
							break;
						}
						boolean lunchTime = (isLunchTime(beginsAt, endsAt, schedule));
						boolean breaks = (isBreakTime(beginsAt, endsAt, schedule));
						if (!isLunchTime(beginsAt, endsAt, schedule)) {
							if (!isBreakTime(beginsAt, endsAt, schedule)) {
								classHour.setBeginsAt(beginsAt);
								classHour.setEndsAt(endsAt);
								classHour.setClassStatus(ClassStatus.NOT_SCHEDULED);
								beginsAt = endsAt;
								endsAt = endsAt.plusMinutes(classHourLength);
							} else {
								classHour.setBeginsAt(beginsAt);
								endsAt = endsAt.minusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
								classHour.setEndsAt(beginsAt.plusHours(schedule.getBreakLengthInMinutes().toMinutes()));
								classHour.setEndsAt(endsAt);
								classHour.setClassStatus(ClassStatus.BREAK_TIME);
								beginsAt = endsAt;
								endsAt = endsAt.plusMinutes(classHourLength);
							}
						} else {
							classHour.setBeginsAt(beginsAt);
							classHour.setEndsAt(endsAt);
							classHour.setClassStatus(ClassStatus.LUNCH_TIME);
							beginsAt = beginsAt.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
							endsAt = endsAt.plusMinutes(classHourLength);
						}
						classHour.setAcademicProgram(academicProgarm);
						classHourRepo.save(classHour);
					}
					currentTime = currentTime.plusDays(1);
				}
			} else
				throw new UserNotFoundException(
						"The school does not contain any schedule, please provide a schedule to the school", null,
						null);

			structure.setData("ClassHour generated successfully for the academic progarm");
			structure.setMessage("Class Hour generated for the current week successfully");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<ResponseStructure<String>>(HttpStatus.CREATED);
		}).orElseThrow(() -> new UserNotFoundException("Invalid Program Id", HttpStatus.BAD_REQUEST, ""));
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> createClassHoursForNextweek(int programId) {
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime nextMonday = currentTime.plusDays(6 - currentTime.getDayOfWeek().getValue());
		AcademicProgram program = academicProgramRepo.findById(programId).orElseThrow();
//		if (classHourRepo.existsByAcademicProgramAndBeginsAtAfter(program, nextMonday)) {
//			structure.setData("New Classhour cannot be created for next week");
//			structure.setMessage("Classhour already present for next week");
//			structure.setStatus(HttpStatus.CREATED.value());
//			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
//		} else {
		List<ClassHour> hours = classHourRepo.findByAcademicProgramAndBeginsAtAfter(program, nextMonday);
		List<ClassHour> classHours2 = new ArrayList<>();
		hours.forEach((hour) -> {
			ClassHour newClassHour = createNewClassHour(hour);
			newClassHour.setClassStatus(ClassStatus.UPCOMING);
			classHours2.add(newClassHour);
		});
		classHours2.forEach((hour) -> {
			LocalDateTime plusDays = hour.getBeginsAt().plusDays(7);
			hour.setBeginsAt(plusDays);
			classHourRepo.save(hour);
		});
		structure.setData("generated");
		structure.setMessage("New Classhour created for next week");
		structure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
	}

	@Override
	public void updateClassHourStatusToOngoing() {
		LocalDateTime now = LocalDateTime.now();
		List<AcademicProgram> academicPrograms = academicProgramRepo.findAll();
		academicPrograms.forEach((program) -> {
			List<ClassHour> classHours = classHourRepo.findByClassStatusAndBeginsAt(ClassStatus.UPCOMING, now);
			classHours.forEach((hour) -> {
				hour.setClassStatus(ClassStatus.ONGOING);
			});
			classHourRepo.saveAll(classHours);
		});
		academicProgramRepo.saveAll(academicPrograms);
	}

	@Override
	public void updateClassHourStatusToCompleted() {
		LocalDateTime now = LocalDateTime.now();
		List<AcademicProgram> academicPrograms = academicProgramRepo.findAll();
		academicPrograms.forEach((program) -> {
			List<ClassHour> hours = classHourRepo.findByClassStatusAndBeginsAtBefore(ClassStatus.ONGOING, now);
			hours.forEach((hour) -> {
				hour.setClassStatus(ClassStatus.COMPLETED);
			});
			classHourRepo.saveAll(hours);
		});
		academicProgramRepo.saveAll(academicPrograms);
	}

//	}

//	@Override
//	public ResponseEntity<ResponseStructure<List<ClassHour>>> createClassHoursForNextweek(int programId) {
//		AcademicProgram academicProgram = academicProgramRepo.findById(programId).get();
//		List<ClassHour> classHours = academicProgram.getClassHours();
//		List<ClassHour> classHours2 = new ArrayList<>();
//		classHours.forEach((hour) -> {
//			ClassHour newClassHour = createNewClassHour(hour);
//			classHours2.add(newClassHour);
//		});
//		classHours2.forEach((hour) -> {
//			LocalDateTime plusDays = hour.getBeginsAt().plusDays(7);
//			hour.setBeginsAt(plusDays);
//			classHourRepo.save(hour);
//		});
//		structure.setData("generated");
//		structure.setMessage("New Classhour created for next week");
//		structure.setStatus(HttpStatus.CREATED.value());
//		return new ResponseEntity<ResponseStructure<List<ClassHour>>>(structure2, HttpStatus.CREATED);
//	}

	@Override
	public ClassHour createNewClassHour(ClassHour classHour) {
		ClassHour hour = new ClassHour();
		hour.setAcademicProgram(classHour.getAcademicProgram());
		hour.setBeginsAt(classHour.getBeginsAt());
		hour.setClassStatus(classHour.getClassStatus());
		hour.setEndsAt(classHour.getEndsAt());
		hour.setRoomNo(classHour.getRoomNo());
		hour.setSubjects(classHour.getSubjects());
		hour.setUser(classHour.getUser());
		return hour;
	}

	@Override
	public Object updateClassHour(List<ClassHourUpdateRequest> updateRequests) {
		updateRequests.forEach((req) -> {
			int userId = req.getUserId();
			User user = userRepo.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User with given ID is not registered in the database",
							HttpStatus.NOT_FOUND, "No such user in database"));
			int roomNo = req.getRoomNo();
			int hourId = req.getClassHourId();
			ClassHour classHour = classHourRepo.findById(hourId).orElseThrow(
					() -> new UserNotFoundException("ClassHour with given ID is not registered in the database",
							HttpStatus.NOT_FOUND, "No such ClassHour in database"));
			int subjectId = req.getSubjectId();
			Subject subject = subjectRepo.findById(subjectId).orElseThrow(
					() -> new UserNotFoundException("Subject with given ID is not registered in the database",
							HttpStatus.NOT_FOUND, "No such Subject in database"));
			if (!classHourRepo.existsByRoomNoAndBeginsAtBetween(roomNo, classHour.getBeginsAt().minusMinutes(1),
					classHour.getEndsAt().plusMinutes(1))) {
				if (user.getUserRole().equals(UserRole.TEACHER)) {
					classHour.setRoomNo(roomNo);
					classHour.setSubjects(subject);
					classHour.setUser(user);
					classHourRepo.save(classHour);
				} else {
					throw new ConstraintViolationException("Invalid User Id", HttpStatus.BAD_REQUEST, "");
				}
			} else {
				throw new UserNotFoundException("Class Hour already contains Room No", HttpStatus.BAD_REQUEST, "");
			}
		});
		return "ClassHour updated";
	}

	@Override
	public Object deleteClassHours(List<ClassHour> classHours) {
//		 classHourRepo.findAll(classHours).orElseThrow();
//		program.setDeleted(true);
		return "Program Soft Deleted";
	}

	@SuppressWarnings("resource")
	@Override
	public ResponseEntity<ResponseStructure<String>> insertClassHoursFromExcel(int programId,
			ExcelRequestDto excelRequestDto) {
		AcademicProgram academicProgram = academicProgramRepo.findById(programId)
				.orElseThrow(() -> new UserNotFoundException(
						"Academic Program with given ID is not registered in the database", HttpStatus.NOT_FOUND,
						"No such Academic Program in database"));
		LocalDateTime fromDate = LocalDateTime.of(excelRequestDto.getFromDate(), (LocalTime.MIDNIGHT));
		LocalDateTime endDate = LocalDateTime.of(excelRequestDto.getEndDate(), (LocalTime.MIDNIGHT)).plusDays(1);
		List<ClassHour> classHours = classHourRepo.findByAcademicProgramAndBeginsAtBetween(academicProgram, fromDate,
				endDate);
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int rowNumber = 0;
		Row header = sheet.createRow(rowNumber);
		header.createCell(0).setCellValue("Date");
		header.createCell(1).setCellValue("Begin Time");
		header.createCell(2).setCellValue("End Time");
		header.createCell(3).setCellValue("Subject");
		header.createCell(4).setCellValue("Teacher");
		header.createCell(5).setCellValue("Room No");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (ClassHour hour : classHours) {
			Row row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue(dateFormatter.format(hour.getBeginsAt()));
			row.createCell(1).setCellValue(timeFormatter.format(hour.getBeginsAt()));
			row.createCell(2).setCellValue(timeFormatter.format(hour.getEndsAt()));
			row.createCell(5).setCellValue(hour.getRoomNo());

			if (hour.getSubjects() == null)
				row.createCell(3).setCellValue("");
			else
				row.createCell(3).setCellValue(hour.getSubjects().getSubjectName());

			if (hour.getUser() == null) {
				row.createCell(4).setCellValue("");
			} else {
				row.createCell(4).setCellValue(hour.getUser().getUsername());
			}
		}
		try {
			workbook.write(new FileOutputStream(excelRequestDto.getFilePath() + "\\test.xlsx"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public ResponseEntity<?> writeToExcel(MultipartFile file, LocalDate fromDate, LocalDate toDate, int programId) throws Exception {
		AcademicProgram program = academicProgramRepo.findById(programId)
				.orElseThrow(() -> new UsernameNotFoundException("Program with given Id not found"));
		LocalDateTime From = fromDate.atTime(LocalTime.MIDNIGHT);
		LocalDateTime To = toDate.atTime(LocalTime.MIDNIGHT);
		List<ClassHour> classhouslist = classHourRepo.findByAcademicProgramAndBeginsAtBetween(program, From, To);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		workbook.forEach((sheet) -> {
			int rowNumber = 0;
			Row header = sheet.createRow(rowNumber);
			header.createCell(0).setCellValue("Date");
			header.createCell(1).setCellValue("Begin Time");
			header.createCell(2).setCellValue("End Time");
			header.createCell(3).setCellValue("Subject");
			header.createCell(4).setCellValue("Teacher");
			header.createCell(5).setCellValue("Room No");
			for (ClassHour classhours : classhouslist) {
				Row row = sheet.createRow(++rowNumber);
				row.createCell(0).setCellValue(dateFormatter.format(classhours.getBeginsAt()));
				row.createCell(1).setCellValue(timeFormatter.format(classhours.getBeginsAt()));
				row.createCell(2).setCellValue(timeFormatter.format(classhours.getEndsAt()));
				if (classhours.getSubjects() == null)
					row.createCell(3).setCellValue("");
				else
					row.createCell(3).setCellValue(classhours.getSubjects().getSubjectName());

				if (classhours.getUser() == null) {
					row.createCell(4).setCellValue("");
				} else {
					row.createCell(4).setCellValue(classhours.getUser().getUsername());
				}
				row.createCell(5).setCellValue(classhours.getRoomNo());
			}
		});
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		byte[] byteData = outputStream.toByteArray();

		return ResponseEntity.ok().header("Content Disposition", "attachment; filename=" + file.getOriginalFilename())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteData);
	}
}