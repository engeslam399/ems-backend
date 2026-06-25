package com.example.ems.service;

import com.example.ems.model.Department;
import java.util.List;

/**
 * Service interface for Department operations.
 */
public interface DepartmentService {

    /**
     * Retrieves all departments.
     *
     * @return list of departments
     */
    List<Department> findAll();

    /**
     * Finds a department by ID.
     *
     * @param id the department ID
     * @return the department, or null if not found
     */
    Department findById(Long id);

    /**
     * Saves a department.
     *
     * @param department the department to save
     * @return the saved department
     */
    Department save(Department department);

    /**
     * Deletes a department by ID.
     *
     * @param id the department ID
     */
    void deleteById(Long id);

    /**
     * Checks if a department exists with the given code.
     *
     * @param code the code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Checks if another department (excluding the current one) exists with the given code.
     *
     * @param code the code
     * @param id   the department ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByCodeAndIdNot(String code, Long id);
}
