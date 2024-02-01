package com.school.sba;

import java.time.LocalDateTime;

public class Dummy {

	public static void main(String[] args) {
		LocalDateTime t=LocalDateTime.now();
		System.out.println(t.getDayOfWeek().getValue());
		
	}
}
