package com.school.vaccination.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.school.vaccination.model.Student;
import com.school.vaccination.model.VaccinationStatus;
import com.school.vaccination.repositories.StudentRepository;

@Service
@RequiredArgsConstructor
public class VaccinationStatusService {

    private final StudentRepository studentRepository;

    // Update vaccination status of a student
    public VaccinationStatus updateVaccinationStatus(String studentId, String driveId, boolean vaccinated) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));

        VaccinationStatus status = student.getVaccinationStatuses().stream()
                .filter(s -> s.getDriveId().equals(driveId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vaccination status not found"));

        status.setVaccinated(vaccinated);
        return status;
    }
}
