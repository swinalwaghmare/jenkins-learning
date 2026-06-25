package com.example.employee;

import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class EmployeeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApiApplication.class, args);
    }

    // Seed some sample data on startup
    @Bean
    CommandLineRunner seedData(EmployeeRepository repo) {
        return args -> {
            repo.save(Employee.builder()
                    .firstName("Arjun").lastName("Sharma")
                    .email("arjun.sharma@company.com")
                    .department("Engineering").designation("Senior Developer")
                    .salary(95000.0).dateOfJoining(LocalDate.of(2021, 3, 15))
                    .status(Employee.EmployeeStatus.ACTIVE).build());

            repo.save(Employee.builder()
                    .firstName("Priya").lastName("Reddy")
                    .email("priya.reddy@company.com")
                    .department("HR").designation("HR Manager")
                    .salary(75000.0).dateOfJoining(LocalDate.of(2019, 7, 1))
                    .status(Employee.EmployeeStatus.ACTIVE).build());

            repo.save(Employee.builder()
                    .firstName("Rahul").lastName("Kumar")
                    .email("rahul.kumar@company.com")
                    .department("Engineering").designation("DevOps Engineer")
                    .salary(88000.0).dateOfJoining(LocalDate.of(2022, 1, 10))
                    .status(Employee.EmployeeStatus.ON_LEAVE).build());

            repo.save(Employee.builder()
                    .firstName("Sneha").lastName("Patel")
                    .email("sneha.patel@company.com")
                    .department("Finance").designation("Financial Analyst")
                    .salary(70000.0).dateOfJoining(LocalDate.of(2020, 5, 20))
                    .status(Employee.EmployeeStatus.ACTIVE).build());

            System.out.println("✅ Sample employees seeded successfully!");
        };
    }
}
