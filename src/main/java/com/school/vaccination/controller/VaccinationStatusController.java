package com.school.vaccination.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.vaccination.model.Student;
import com.school.vaccination.model.VaccinationDrive;
import com.school.vaccination.model.VaccinationStatus;
import com.school.vaccination.repositories.StudentRepository;
import com.school.vaccination.repositories.VaccinationDriveRepository;
import com.school.vaccination.service.StudentService;
import com.school.vaccination.service.VaccinationStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class VaccinationStatusController {

	@Autowired
	private VaccinationStatusService vaccinationStatusService;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private VaccinationDriveRepository driveRepository;

	@Autowired
	private StudentService studentService;

	@PostMapping("/students")
	public ResponseEntity<String> registerForDrive(@RequestBody Student student) {
		Student savedStudent = studentRepository.save(student);

		return ResponseEntity.ok("Student registered for vaccination drive.");
	}

	// Update vaccination status for a student
	@PutMapping("/students/{id}")
	public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student updatedStudent) {
		return studentRepository.findById(id).map(student -> {
			student.setName(updatedStudent.getName());
			student.setStudentClass(updatedStudent.getStudentClass());
			student.setStudentId(updatedStudent.getStudentId());
			Student saved = studentRepository.save(student);
			return ResponseEntity.ok(saved);
		}).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{studentId}/vaccination-status")
	public ResponseEntity<List<VaccinationStatus>> getVaccinationStatuses(@PathVariable("studentId") String studentId) {
		Student student = studentRepository.findById(studentId).orElse(null);

		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(student.getVaccinationStatuses());
	}

	@DeleteMapping("/{studentId}/vaccination/{driveId}")
	public ResponseEntity<String> removeVaccinationEntry(@PathVariable("studentId") String studentId, @PathVariable("driveId") String driveId) {
		Student student = studentRepository.findById(studentId).orElse(null);

		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		boolean removed = student.getVaccinationStatuses().removeIf(status -> status.getDriveId().equals(driveId));

		if (!removed) {
			return ResponseEntity.badRequest().body("Vaccination entry not found for this drive.");
		}

		studentRepository.save(student);
		return ResponseEntity.ok("Vaccination entry removed.");
	}

	@GetMapping("/students")
	public ResponseEntity<List<Student>> getAllStudents() {
		List<Student> students = studentRepository.findAll();
		return ResponseEntity.ok(students);
	}

	// US 1.1
	@GetMapping("/dashboard")
	public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
		long totalStudents = studentRepository.count();
		long vaccinatedStudents = studentRepository.countVaccinatedStudents();
		double percentageVaccinated = totalStudents == 0 ? 0 : (vaccinatedStudents * 100.0) / totalStudents;

		LocalDate today = LocalDate.now();
		LocalDate next30 = today.plusDays(30);
		List<VaccinationDrive> upcomingDrives = driveRepository.findByDriveDateBetween(today, next30);

		Map<String, Object> response = new HashMap<>();
		response.put("totalStudents", totalStudents);
		response.put("vaccinatedStudents", vaccinatedStudents);
		response.put("percentageVaccinated", percentageVaccinated);
		response.put("upcomingDrives", upcomingDrives);

		return ResponseEntity.ok(response);
	}

	// US 1.2
	@PostMapping("/students/bulk-upload")
	public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			List<Student> students = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				if (tokens.length >= 3) {
					Student student = new Student();
					student.setName(tokens[1]);
					student.setStudentClass(tokens[2]);
					student.setStudentId(tokens[0]);
					students.add(student);
				}
			}
			studentRepository.saveAll(students);
			return ResponseEntity.ok("Bulk upload successful.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing CSV file.");

		}
	}

	// US 1.3
	@GetMapping("/report")
	public ResponseEntity<List<Student>> getVaccinationReport(@RequestParam(required = false, name="vaccineName") String vaccineName) {
		List<Student> all = studentRepository.findAll();

		if (vaccineName != null && !vaccineName.isEmpty()) {
			all = all.stream()
					.filter(s -> s.getVaccinationStatuses().stream()
							.anyMatch(v -> v.getVaccineName().equalsIgnoreCase(vaccineName)))
					.collect(Collectors.toList());
		}
		return ResponseEntity.ok(all);
	}

	// US 1.4
	@PostMapping("/drives")
	public ResponseEntity<String> createDrive(@RequestBody VaccinationDrive drive) {
		if (drive.getDriveDate().isBefore(LocalDate.now().plusDays(15))) {
			return ResponseEntity.badRequest().body("Drives must be scheduled at least 15 days in advance.");
		}

		boolean overlap = driveRepository
				.findByDriveDateBetween(drive.getDriveDate().minusDays(1), drive.getDriveDate().plusDays(1)).stream()
				.anyMatch(d -> d.getApplicableClasses().equals(drive.getApplicableClasses()));

		if (overlap) {
			return ResponseEntity.badRequest().body("A drive already exists around that date for the same class.");
		}

		driveRepository.save(drive);
		return ResponseEntity.ok("Drive created successfully.");
	}

	// US 1.5
	@PutMapping("/drives/{id}")
	public ResponseEntity<?> updateDrive(@PathVariable(name="id") String id, @RequestBody VaccinationDrive updatedDrive) {
		return driveRepository.findById(id).map(existing -> {
			if (existing.getDriveDate().isBefore(LocalDate.now())) {
				return ResponseEntity.badRequest().body("Cannot edit past or ongoing drives.");
			}

			existing.setDriveDate(updatedDrive.getDriveDate());
			existing.setAvailableDoses(updatedDrive.getAvailableDoses());
			existing.setVaccineName(updatedDrive.getVaccineName());
			existing.setApplicableClasses(updatedDrive.getApplicableClasses());
			driveRepository.save(existing);
			return ResponseEntity.ok(existing);
		}).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/students/{studentId}/vaccination/{driveId}")
	public ResponseEntity<Student> updateStatus(@PathVariable("studentId") String studentId,
			@PathVariable("driveId") String driveId,

			@RequestParam("vaccinated") boolean vaccinated) {
		Student status = vaccinationStatusService.updateVaccinationStatus(studentId, driveId, vaccinated);
		return ResponseEntity.ok(status);
	}

	@PostMapping("/{studentId}/register/{driveId}")
	public ResponseEntity<String> registerStudentToDrive(@PathVariable("studentId") String studentId,
			@PathVariable("driveId") String driveId) {
		studentService.registerStudentForDrive(studentId, driveId);
		return ResponseEntity.ok("Student registered for the drive.");
	}
}
