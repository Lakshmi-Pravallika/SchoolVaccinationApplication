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
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final VaccinationDriveRepository vaccinationDriveRepository;

    
	public Student registerStudentForDrive(String studentId, String driveId) {
		Optional<Student> studentOpt = studentRepository.findById(studentId);
		if (studentOpt.isEmpty()) {
			throw new RuntimeException("Student not found");
		}
		Student student = studentOpt.get();

		
		VaccinationDrive drive = vaccinationDriveRepository.findById(driveId)
				.orElseThrow(() -> new RuntimeException("Drive not found"));
		
		List<VaccinationStatus> statusList = student.getVaccinationStatuses().stream()
	            .filter(status -> status.getVaccineName().equals(drive.getVaccineName()) 
	                    && status.getDateOfVaccination().equals(drive.getDriveDate())) // Check the same date
	            .collect(Collectors.toList());

	    if (statusList.size() > 0) {
	        throw new RuntimeException("Student already registered for the drive on the same date.");
	    }
		
		List<VaccinationStatus> moreThanTwiceList = student.getVaccinationStatuses().stream()
				.filter(status -> status.getVaccineName().equals(drive.getVaccineName())).collect(Collectors.toList());

		if (moreThanTwiceList.size() >= 2) {
			throw new RuntimeException("Student already registered for the same vaccine twice");
		}

		
		VaccinationStatus status = new VaccinationStatus();
		status.setVaccineName(drive.getVaccineName());
		status.setDateOfVaccination(drive.getDriveDate());
		status.setDriveId(driveId);
		status.setVaccinated(false); 

		student.getVaccinationStatuses().add(status);
		return studentRepository.save(student);
	}

    
    public List<Student> getAllStudentsVaccinationStatuses() {
        return studentRepository.findAll();
    }

    
    public Student getVaccinationStatusByStudentId(String studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
