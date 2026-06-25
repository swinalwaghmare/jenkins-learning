package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // GET /api/employees — list all, or search by query param
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Employee.EmployeeStatus status) {

        if (name != null) return ResponseEntity.ok(service.searchByName(name));
        if (department != null) return ResponseEntity.ok(service.getByDepartment(department));
        if (status != null) return ResponseEntity.ok(service.getByStatus(status));
        return ResponseEntity.ok(service.getAllEmployees());
    }

    // GET /api/employees/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    // POST /api/employees
    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createEmployee(employee));
    }

    // PUT /api/employees/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id,
                                            @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(service.updateEmployee(id, employee));
    }

    // DELETE /api/employees/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.deleteEmployee(id);
        return ResponseEntity.ok(Map.of("message", "Employee with ID " + id + " deleted successfully"));
    }
}
