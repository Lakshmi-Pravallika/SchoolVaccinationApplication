package com.school.vaccination.repositories;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.school.vaccination.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
	@Query("{ 'username': ?0 }")
    Optional<User> findByUsername(String username);
}

