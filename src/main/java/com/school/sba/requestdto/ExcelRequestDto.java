package com.school.sba.requestdto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRequestDto {

	private LocalDate fromDate;
	private LocalDate endDate;
	private String filePath;

}
