package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;

public interface ClassHourRepo extends JpaRepository<ClassHour, Integer> {

	List<ClassHour> findByRoomNo(int roomNo);

	boolean existsByRoomNoAndBeginsAtBetween(int roomNo, LocalDateTime beginTime, LocalDateTime endTime);

	List<ClassHour> findByUser(User users);

	List<ClassHour> findByAcademicProgram(AcademicProgram academicProgram);

	List<ClassHour> findByAcademicProgramAndBeginsAtAfter(AcademicProgram academicProgram, LocalDateTime beginsAt);

	boolean existsByAcademicProgramAndBeginsAtAfter(AcademicProgram academicProgram, LocalDateTime beginsAt);

	List<ClassHour> findByAcademicProgramAndBeginsAtBetween(AcademicProgram academicProgram, LocalDateTime beginsAt,
			LocalDateTime endsAt);

	List<ClassHour> findByClassStatusAndBeginsAt(ClassStatus classStatus, LocalDateTime beginsAt);

	List<ClassHour> findByClassStatusAndBeginsAtBefore(ClassStatus classStatus, LocalDateTime beginsAt);
//	@Query("select roomNo from ClassHour where beginsAt is between(?1, ?2) and roomNo=?3")
//	int findClasshoursByRoomNoByTime(LocalDateTime beginTime, LocalDateTime endTime, int roomNo);

//	boolean existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo( LocalDateTime beginsAt, LocalDateTime endsAt, int roomNo);
}
