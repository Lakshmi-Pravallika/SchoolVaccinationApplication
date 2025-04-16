package com.school.vaccination.repositories;

import com.school.vaccination.model.VaccinationDrive;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationDriveRepository extends MongoRepository<VaccinationDrive, String> {
    List<VaccinationDrive> findByDriveDateBetween(LocalDate start, LocalDate end);
}
