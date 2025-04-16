package com.school.vaccination.service;


import com.school.vaccination.model.Student;
import com.school.vaccination.model.VaccinationDrive;
import com.school.vaccination.model.VaccinationStatus;
import com.school.vaccination.repositories.StudentRepository;
import com.school.vaccination.repositories.VaccinationDriveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final VaccinationDriveRepository vaccinationDriveRepository;

    // Register a student for a vaccination drive
    public Student registerStudentForDrive(String studentId, String driveId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Student not found");
        }
        Student student = studentOpt.get();

        // Check if the drive exists
        VaccinationDrive drive = vaccinationDriveRepository.findById(driveId).orElseThrow(() -> new RuntimeException("Drive not found"));

        // Add the vaccination status for the drive
        VaccinationStatus status = new VaccinationStatus();
        status.setVaccineName(drive.getVaccineName());
        status.setDriveId(driveId);
        status.setVaccinated(false); // Set to false initially

        student.getVaccinationStatuses().add(status);
        return studentRepository.save(student);
    }

    // Get all students' vaccination status
    public List<Student> getAllStudentsVaccinationStatuses() {
        return studentRepository.findAll();
    }

    // Get vaccination status by studentId
    public Student getVaccinationStatusByStudentId(String studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
