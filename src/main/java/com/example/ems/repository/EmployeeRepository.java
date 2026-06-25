package com.example.ems.repository;

import com.example.ems.model.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Employee entity operations.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

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
     * @param id   the current employee ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Advanced search and filter for employees.
     * All parameters are optional.
     *
     * @param searchTerm   searches name or code (case-insensitive)
     * @param departmentId filter by department id
     * @param minSalary    filter by minimum salary
     * @param maxSalary    filter by maximum salary
     * @return list of matching employees
     */
    @Query("SELECT e FROM Employee e WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:departmentId IS NULL OR e.department.id = :departmentId) AND " +
           "(:minSalary IS NULL OR e.salary >= :minSalary) AND " +
           "(:maxSalary IS NULL OR e.salary <= :maxSalary)")
    List<Employee> searchEmployees(
            @Param("searchTerm") String searchTerm,
            @Param("departmentId") Long departmentId,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary
    );
}
