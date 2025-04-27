package com.school.vaccination.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Document(collection = "vaccination_drives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationDrive {

	@Id
	private String driveId;
	private String vaccineName;
	@Field("drive_date")  // MongoDB annotation
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate driveDate;
	private int availableDoses;
	private String applicableClasses;
	private boolean completed;
}
