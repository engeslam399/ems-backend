package com.example.ems.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.ems.model.Department;
import com.example.ems.model.Employee;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    private Department devDept;
    private Department hrDept;

    @BeforeEach
    void setUp() {
        // Clear database if any records exist
        employeeService.findAll().forEach(e -> employeeService.deleteById(e.getId()));
        departmentService.findAll().forEach(d -> departmentService.deleteById(d.getId()));

        devDept = Department.builder()
                .code("DEV")
                .name("Development")
                .description("Software Development")
                .build();
        devDept = departmentService.save(devDept);

        hrDept = Department.builder()
                .code("HR")
                .name("Human Resources")
                .description("HR Department")
                .build();
        hrDept = departmentService.save(hrDept);
    }

    @Test
    void testSaveAndFindEmployee() {
        Employee employee = Employee.builder()
                .code("EMP001")
                .name("Alice Smith")
                .dateOfBirth(LocalDate.of(1990, 5, 12))
                .address("123 Street")
                .mobile("1234567890")
                .salary(5000.0)
                .department(devDept)
                .build();

        Employee saved = employeeService.save(employee);
        assertNotNull(saved.getId());

        Employee found = employeeService.findById(saved.getId());
        assertNotNull(found);
        assertEquals("Alice Smith", found.getName());
        assertEquals("DEV", found.getDepartment().getCode());
    }

    @Test
    void testDuplicateEmployeeCode() {
        Employee employee1 = Employee.builder()
                .code("EMP001")
                .name("Alice Smith")
                .salary(5000.0)
                .department(devDept)
                .build();
        employeeService.save(employee1);

        // Check existsByCode
        assertTrue(employeeService.existsByCode("EMP001"));
        assertFalse(employeeService.existsByCode("EMP002"));

        // Check existsByCodeAndIdNot
        Employee employee2 = Employee.builder()
                .code("EMP002")
                .name("Bob Jones")
                .salary(6000.0)
                .department(devDept)
                .build();
        Employee saved2 = employeeService.save(employee2);

        // Code EMP001 is already taken by employee1. So checking EMP001 with Bob's ID should return true (taken by someone else)
        assertTrue(employeeService.existsByCodeAndIdNot("EMP001", saved2.getId()));
        // Code EMP002 is taken by Bob. Checking EMP002 with Bob's ID should return false (it's his own code)
        assertFalse(employeeService.existsByCodeAndIdNot("EMP002", saved2.getId()));
    }

    @Test
    void testSearchEmployees() {
        Employee emp1 = Employee.builder()
                .code("EMP001")
                .name("Alice Smith")
                .salary(5000.0)
                .department(devDept)
                .build();
        employeeService.save(emp1);

        Employee emp2 = Employee.builder()
                .code("EMP002")
                .name("Bob Jones")
                .salary(7500.0)
                .department(hrDept)
                .build();
        employeeService.save(emp2);

        Employee emp3 = Employee.builder()
                .code("EMP003")
                .name("Charlie Adams")
                .salary(4000.0)
                .department(devDept)
                .build();
        employeeService.save(emp3);

        // 1. Search by name term
        List<Employee> results = employeeService.searchEmployees("Alice", null, null, null);
        assertEquals(1, results.size());
        assertEquals("EMP001", results.get(0).getCode());

        // 2. Search by code term (case insensitive)
        results = employeeService.searchEmployees("emp002", null, null, null);
        assertEquals(1, results.size());
        assertEquals("Bob Jones", results.get(0).getName());

        // 3. Filter by department
        results = employeeService.searchEmployees(null, devDept.getId(), null, null);
        assertEquals(2, results.size());

        // 4. Filter by salary min/max
        results = employeeService.searchEmployees(null, null, 4500.0, 8000.0);
        assertEquals(2, results.size()); // Alice (5000) and Bob (7500)
    }
}
