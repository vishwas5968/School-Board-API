package com.school.sba.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {

	@Autowired
	private ClassHourService hourService;

	@PostMapping(path = "/academic-program/{programId}/class-hours")
	public ResponseEntity<ResponseStructure<String>> name(@PathVariable int programId) {
		return hourService.registerClassHour(programId);
	}

	@PutMapping(path = "/class-hours")
	public Object updateClassHour(@RequestBody List<ClassHourUpdateRequest> updateRequests) {
		return hourService.updateClassHour(updateRequests);
	}

	@PostMapping(path = "/class-hours/{programId}")
	public ResponseEntity<ResponseStructure<String>> createClassHoursForNextweek(@PathVariable int programId) {
		return hourService.createClassHoursForNextweek(programId);
	}

	@PostMapping(path = "/academic-program/{programId}/class-hours/write-excel")
	public ResponseEntity<ResponseStructure<String>> insertClassHoursFromExcel(@PathVariable int programId,
			@RequestBody ExcelRequestDto excelRequestDto) {
		System.out.println(excelRequestDto.getFromDate());
		return hourService.insertClassHoursFromExcel(programId, excelRequestDto);
	}
	
	@PostMapping(path = "/academic-program/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
	public ResponseEntity<?> writeToExcel(@RequestParam MultipartFile file, @PathVariable LocalDate fromDate,
			@PathVariable LocalDate toDate, @PathVariable int programId)throws Exception {
		return hourService.writeToExcel(file, fromDate, toDate, programId);

	}
	
//	@PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile file) {
//	  service.uploadFile(file);
//	  return new ResponseEntity<>("success", HttpStatus.OK);
//	}
}
