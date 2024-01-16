package com.school.sba.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.repository.SchoolRepo;

@Service
public class SchoolService {

	@Autowired
	SchoolRepo repo;

	public School fetchSchoolById(int id) {
		School school = repo.findById(id).get();
		return school;
	}

	public List<School> fetchAllSchool() {
		List<School> schools = repo.findAll();
		return schools;
	}

	public void deleteSchool(int id) {
		School fetchSchoolById = fetchSchoolById(id);
		repo.delete(fetchSchoolById);
	}

	public void insertSchool(int schoolId, String shoolName, long contactNo, String emailId, String address) {
		School school = new School();
		school.setShoolName(shoolName);
		school.setContactNo(contactNo);
		school.setEmailId(emailId);
		school.setAddress(address);
		repo.save(school);
	}

	public void updateSchool(int schoolId, String shoolName, long contactNo, String emailId, String address) {
		School school = new School();
		school.setShoolName(shoolName);
		school.setContactNo(contactNo);
		school.setEmailId(emailId);
		school.setAddress(address);
		repo.saveAndFlush(school);
	}
}
