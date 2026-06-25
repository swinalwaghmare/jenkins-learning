# Employee Management REST API

A Spring Boot REST API for managing employees with full CRUD operations.

## Tech Stack
- **Java 17**
- **Spring Boot 3.2**
- **Spring Data JPA**
- **H2 In-Memory Database**
- **Lombok**
- **Maven**

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application
```bash
mvn spring-boot:run
```
Server starts at: `http://localhost:8080`

### Run Tests
```bash
mvn test
```

### H2 Database Console
Available at: `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:employeedb`
- **Username:** `sa`
- **Password:** *(leave blank)*

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/employees` | Get all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| POST | `/api/employees` | Create new employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Delete employee |
| GET | `/api/employees?name=John` | Search by name |
| GET | `/api/employees?department=IT` | Filter by department |
| GET | `/api/employees?status=ACTIVE` | Filter by status |

---

## Sample Requests

### Create Employee
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Vikram",
    "lastName": "Singh",
    "email": "vikram.singh@company.com",
    "department": "Engineering",
    "designation": "Backend Developer",
    "salary": 85000,
    "dateOfJoining": "2024-01-15",
    "status": "ACTIVE"
  }'
```

### Get All Employees
```bash
curl http://localhost:8080/api/employees
```

### Search by Name
```bash
curl http://localhost:8080/api/employees?name=arjun
```

### Filter by Department
```bash
curl http://localhost:8080/api/employees?department=Engineering
```

### Update Employee
```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Arjun",
    "lastName": "Sharma",
    "email": "arjun.sharma@company.com",
    "department": "Engineering",
    "designation": "Lead Developer",
    "salary": 110000,
    "dateOfJoining": "2021-03-15",
    "status": "ACTIVE"
  }'
```

### Delete Employee
```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

---

## Employee Status Values
- `ACTIVE`
- `INACTIVE`
- `ON_LEAVE`

---

## Sample Employees (Auto-Seeded on Startup)
| Name | Department | Designation |
|------|-----------|-------------|
| Arjun Sharma | Engineering | Senior Developer |
| Priya Reddy | HR | HR Manager |
| Rahul Kumar | Engineering | DevOps Engineer |
| Sneha Patel | Finance | Financial Analyst |
