package com.example.ems.repository;

import com.example.ems.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Department entity operations.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Checks if a department exists with the given code.
     *
     * @param code the department code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Checks if another department (excluding the current one) exists with the given code.
     *
     * @param code the department code
     * @param id   the current department ID
     * @return true if exists, false otherwise
     */
    boolean existsByCodeAndIdNot(String code, Long id);
}
