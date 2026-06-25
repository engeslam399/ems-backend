package com.example.ems.service;

import com.example.ems.model.Employee;
import java.util.List;

/**
 * Service interface for Employee operations.
 */
public interface EmployeeService {

    /**
     * Retrieves all employees.
     *
     * @return list of employees
     */
    List<Employee> findAll();

    /**
     * Finds an employee by ID.
     *
     * @param id the employee ID
     * @return the employee, or null if not found
     */
    Employee findById(Long id);

    /**
     * Saves an employee.
     *
     * @param employee the employee to save
     * @return the saved employee
     */
    Employee save(Employee employee);

    /**
     * Deletes an employee by ID.
     *
     * @param id the employee ID
     */
    void deleteById(Long id);

    /**
     * Checks if an employee exists with the given code.
     *
     * @param code the employee code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Checks if another employee (excluding the current one) exists with the given code.
     *
     * @param code the employee code
     * @param id   the employee ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Searches and filters employees by given parameters.
     *
     * @param searchTerm   searches name or code (case-insensitive)
     * @param departmentId filter by department id
     * @param minSalary    filter by minimum salary
     * @param maxSalary    filter by maximum salary
     * @return list of matching employees
     */
    List<Employee> searchEmployees(String searchTerm, Long departmentId, Double minSalary, Double maxSalary);
}
