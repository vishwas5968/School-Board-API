package com.school.sba.entity;

import java.time.LocalDate;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AcademicProgram {

	@Id
	@GeneratedValue
	private int programId;
	private ProgramType programType;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	
	@ManyToOne
	private School school;
}