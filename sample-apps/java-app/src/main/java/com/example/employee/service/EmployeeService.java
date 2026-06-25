package com.example.employee.service;

import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    // Get employee by ID
    public Employee getEmployeeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    // Create new employee
    public Employee createEmployee(Employee employee) {
        if (repository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + employee.getEmail());
        }
        if (employee.getStatus() == null) {
            employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        }
        return repository.save(employee);
    }

    // Update existing employee
    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = getEmployeeById(id);

        // Check email uniqueness if changed
        if (!existing.getEmail().equals(updated.getEmail()) && repository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + updated.getEmail());
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setDesignation(updated.getDesignation());
        existing.setSalary(updated.getSalary());
        existing.setDateOfJoining(updated.getDateOfJoining());
        existing.setStatus(updated.getStatus());

        return repository.save(existing);
    }

    // Delete employee
    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // Get employees by department
    public List<Employee> getByDepartment(String department) {
        return repository.findByDepartment(department);
    }

    // Get employees by status
    public List<Employee> getByStatus(Employee.EmployeeStatus status) {
        return repository.findByStatus(status);
    }

    // Search employees by name
    public List<Employee> searchByName(String name) {
        return repository.searchByName(name);
    }
}
