package com.school.sba.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.school.sba.enums.ClassStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClassHour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int classHourId;
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;

	@ManyToOne
	private List<Subject> subjects;
	
	@ManyToOne
	private List<User> users;
	
	@ManyToOne
	private List<AcademicProgram> programs;
}
