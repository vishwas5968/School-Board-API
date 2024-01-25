package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.UnauthorizedException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.SchoolRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {

	@Autowired
	AcademicProgramRepo programRepo;

	@Autowired
	SchoolRepo schoolRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ResponseStructure<AcademicProgramResponse> structure;

	@Autowired
	ResponseStructure<List<AcademicProgramResponse>> responseStructure;

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUserToAcademicProgram(int programId, int userId) {
		AcademicProgram academicProgram = programRepo.findById(programId)
				.orElseThrow(() -> new UserNotFoundException(
						"Academic Program with given ID is not registered in the database", HttpStatus.NOT_FOUND,
						"No such Academic Program in database"));
		return userRepo.findById(userId).map(u -> {
			if (u.getUserRole().equals(UserRole.ADMIN)) {
				throw new UnauthorizedException(
						"User with given ID is an admin so the program cannot be registered to it",
						HttpStatus.BAD_REQUEST, "No such mapping possible");
			} else if (!(academicProgram.getSubjects().contains(u.getSubject()))) {
				throw new UnauthorizedException(
						"User with given ID contains a subject which is not present in the respective academic program",
						HttpStatus.BAD_REQUEST, "No such mapping possible");
			} else {
				academicProgram.getUsers().add(u);
				programRepo.save(academicProgram);
				structure.setData(mapToAcademicResponseProgram(academicProgram));
				structure.setStatus(HttpStatus.ACCEPTED.value());
				structure.setMessage("Added user to academic program");
				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.ACCEPTED);
			}
		}).orElseThrow(() -> new UserNotFoundException("User with given ID is not registered in the database",
				HttpStatus.NOT_FOUND, "No such user in database"));

	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> saveAcademicProgram(
			AcademicProgramRequest programRequest, int schoolId) {
		AcademicProgram program = programRepo.save(mapToAcademicProgram(programRequest));
		School school = schoolRepo.findById(schoolId).get();
		school.getAcademicPrograms().add(program);
		program.setSchool(school);
		programRepo.save(program);
		schoolRepo.save(school);
		structure.setData(mapToAcademicResponseProgram(program));
		structure.setMessage("Academic Program saved to the database");
		structure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> fetchAllAcademicProgram(int schoolId) {
		School school = schoolRepo.findById(schoolId).get();
		List<AcademicProgram> listAcademicProgram = school.getAcademicPrograms();
		List<AcademicProgramResponse> responses = new ArrayList<>();
		for (AcademicProgram academicProgram : listAcademicProgram) {
			responses.add(mapToAcademicResponseProgram(academicProgram));
		}
		responseStructure.setData(responses);
		responseStructure.setMessage("Academic Program saved to the database");
		responseStructure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
	}

	public AcademicProgram mapToAcademicProgram(AcademicProgramRequest programRequest) {
		return AcademicProgram.builder().programType(programRequest.getProgramType())
				.programName(programRequest.getProgramName()).beginsAt(programRequest.getBeginsAt())
				.endsAt(programRequest.getEndsAt()).build();
	}

	public AcademicProgramResponse mapToAcademicResponseProgram(AcademicProgram academicProgram) {
		return AcademicProgramResponse.builder().programId(academicProgram.getProgramId())
				.programType(academicProgram.getProgramType()).programNameString(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt()).endsAt(academicProgram.getEndsAt()).build();
	}

}