package com.example.employee;

import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EmployeeRepository repository;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        testEmployee = repository.save(Employee.builder()
                .firstName("Test").lastName("User")
                .email("test@example.com")
                .department("IT").designation("Developer")
                .salary(60000.0)
                .dateOfJoining(LocalDate.now())
                .status(Employee.EmployeeStatus.ACTIVE)
                .build());
    }

    @Test
    void shouldReturnAllEmployees() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void shouldGetEmployeeById() throws Exception {
        mockMvc.perform(get("/api/employees/" + testEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        Employee newEmp = Employee.builder()
                .firstName("Jane").lastName("Doe")
                .email("jane.doe@example.com")
                .department("Marketing").designation("Manager")
                .salary(80000.0)
                .dateOfJoining(LocalDate.now())
                .status(Employee.EmployeeStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void shouldReturn404ForMissingEmployee() throws Exception {
        mockMvc.perform(get("/api/employees/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/" + testEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("deleted")));
    }
}
