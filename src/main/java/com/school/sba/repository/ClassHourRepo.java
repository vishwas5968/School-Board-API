package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;

public interface ClassHourRepo extends JpaRepository<ClassHour, Integer> {

	List<ClassHour> findByRoomNo(int roomNo);

	boolean existsByRoomNoAndBeginsAtBetween(int roomNo, LocalDateTime beginTime, LocalDateTime endTime);

//	@Query("select roomNo from ClassHour where beginsAt is between(?1, ?2) and roomNo=?3")
//	int findClasshoursByRoomNoByTime(LocalDateTime beginTime, LocalDateTime endTime, int roomNo);
	
//	boolean existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo( LocalDateTime beginsAt, LocalDateTime endsAt, int roomNo);
}
