package com.school.vaccination.repositories;

import com.school.vaccination.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    List<Student> findByStudentId(String studentId);

    @Query(value = "{ 'vaccinationStatuses.vaccinated': true }", count = true)
    long countVaccinatedStudents();
}
