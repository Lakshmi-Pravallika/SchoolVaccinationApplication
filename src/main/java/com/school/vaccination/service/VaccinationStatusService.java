package com.school.vaccination.service;


import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.school.vaccination.model.Student;
import com.school.vaccination.model.VaccinationStatus;
import com.school.vaccination.repositories.StudentRepository;

@Service
@RequiredArgsConstructor
public class VaccinationStatusService {

    private final StudentRepository studentRepository;

    // Update vaccination status of a student
    public Student updateVaccinationStatus(String studentId, String driveId, boolean vaccinated) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));

        VaccinationStatus status = student.getVaccinationStatuses().stream()
                .filter(s -> s.getDriveId().equals(driveId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vaccination status not found"));

        status.setVaccinated(vaccinated);
        List<VaccinationStatus> statuses = student.getVaccinationStatuses();

        for (VaccinationStatus s : statuses) {
            if (s.getDriveId().equals(driveId)) {
                s.setVaccinated(vaccinated);
                break;
            }
        }
        student.setVaccinationStatuses(statuses);
        studentRepository.save(student);
        return student;
    }
}
