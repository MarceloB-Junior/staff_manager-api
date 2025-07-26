package com.api.staff_manager.repositories;

import com.api.staff_manager.models.DepartmentModel;
import com.api.staff_manager.models.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeModel, UUID> {
    boolean existsByNameAndDepartment(String name, DepartmentModel department);
}