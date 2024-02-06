package com.school.sba.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> registerClassHour(int programId);

	Object updateClassHour(List<ClassHourUpdateRequest> updateRequests);

	Object deleteClassHours(List<ClassHour> classHours);

	ResponseEntity<ResponseStructure<String>> createClassHoursForNextweek(int programId);

	ClassHour createNewClassHour(ClassHour classHour);

	void updateClassHourStatusToOngoing();

	void updateClassHourStatusToCompleted();

	ResponseEntity<ResponseStructure<String>> insertClassHoursFromExcel(int programId, ExcelRequestDto excelRequestDto);

	ResponseEntity<?> writeToExcel(MultipartFile file, LocalDate fromDate, LocalDate toDate, int programId)
			throws Exception;

}
