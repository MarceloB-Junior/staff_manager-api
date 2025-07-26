package com.api.staff_manager.services.impl;

import com.api.staff_manager.dtos.requests.EmployeeRequest;
import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import com.api.staff_manager.dtos.responses.EmployeeViewResponse;
import com.api.staff_manager.exceptions.custom.DepartmentNotFoundException;
import com.api.staff_manager.exceptions.custom.EmployeeExistsException;
import com.api.staff_manager.exceptions.custom.EmployeeNotFoundException;
import com.api.staff_manager.mappers.EmployeeMapper;
import com.api.staff_manager.repositories.DepartmentRepository;
import com.api.staff_manager.repositories.EmployeeRepository;
import com.api.staff_manager.services.PhotoService;
import com.api.staff_manager.services.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final PhotoService photoService;

    @Override
    @Transactional
    public EmployeeViewResponse save(EmployeeRequest request) {
        log.debug("Trying to save employee with request: {}", request);

        log.debug("Trying to find a department with id: {}", request.departmentId());
        var department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() ->
                        new DepartmentNotFoundException("Department not found with id: " + request.departmentId())
                );
        if (employeeRepository.existsByNameAndDepartment(request.name(), department)) {
            log.error("Employee with name '{}' already exists in department", request.name());
            throw new EmployeeExistsException("An employee with this name already exists in the department.");
        }
        var employee = employeeMapper.toEntity(request);
        employee.setDepartment(department);
        var savedEmployee = employeeRepository.save(employee);
        log.debug("Successfully save a employee with request: {}", request);
        return employeeMapper.toViewResponse(savedEmployee);
    }

    @Override
    public Page<EmployeeViewResponse> findAll(Pageable pageable) {
        log.debug("Trying to find all employees with pageable: {}", pageable);
        return employeeRepository.findAll(pageable).map(employeeMapper::toViewResponse);
    }

    @Override
    public EmployeeDetailsResponse findById(UUID id) {
        log.debug("Trying to find a employee with id: {}", id);
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toDetailsResponse(employee);
    }

    @Override
    public EmployeeDetailsResponse update(EmployeeRequest request, UUID id) {
        log.debug("Trying to update employee with id: {} and request: {}", id, request);
        var department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() ->
                        new DepartmentNotFoundException("Department not found with id: " + request.departmentId())
                );
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        if(employeeRepository.existsByNameAndDepartment(request.name(), department)
                && !employee.getName().equals(request.name())) {
            log.error("Another employee with name '{}' already exists in the department", request.name());
            throw new EmployeeExistsException("An employee with this name already exists in the department.");
        }

        employee.setName(request.name());
        employee.setPosition(request.position());
        employee.setSalary(request.salary());

        if (!employee.getDepartment().getDepartmentId().equals(request.departmentId())) {
            log.debug("Changing employee's department from {} to {}", employee.getDepartment().getDepartmentId(),
                    request.departmentId());
            employee.setDepartment(department);
        }
        var updatedEmployee = employeeRepository.save(employee);
        log.debug("Successfully update an employee with id: {}", updatedEmployee.getEmployeeId());
        return employeeMapper.toDetailsResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Trying to find a employee with id: {} to delete", id);
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        if (employee.getEmployeePhoto() != null) {
            photoService.deletePhoto(id);
        }
        employeeRepository.delete(employee);
        log.debug("Successfully deleted employee with id: {}", id);
    }
}
