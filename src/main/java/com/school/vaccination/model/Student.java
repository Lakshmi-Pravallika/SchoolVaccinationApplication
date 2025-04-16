package com.school.vaccination.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
   
    private String name;
    private String studentClass;
    @Id
    private String studentId;
    private List<VaccinationStatus> vaccinationStatuses = new ArrayList<>();
   
}

