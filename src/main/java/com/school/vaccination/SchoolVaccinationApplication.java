package com.school.vaccination;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoolVaccinationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolVaccinationApplication.class, args);
        System.out.println("🚀 School Vaccine Tracker Backend is running...");
    }

}
