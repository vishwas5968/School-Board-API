package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;

public interface AcademicProgramRepo extends JpaRepository<AcademicProgram, Integer> {

}