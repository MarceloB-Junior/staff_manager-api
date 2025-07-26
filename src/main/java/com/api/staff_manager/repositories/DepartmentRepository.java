package com.api.staff_manager.repositories;

import com.api.staff_manager.models.DepartmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, UUID> {
    boolean existsByName(String name);
}