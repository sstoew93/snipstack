package org.example.final_project.user.repository;

import org.example.final_project.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByIsActive(Boolean isActive);

    List<User> findAllByCreatedOnAfter(LocalDateTime createdOnDateAfter);
}
