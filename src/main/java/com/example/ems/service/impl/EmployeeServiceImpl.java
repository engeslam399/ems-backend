package com.example.ems.service.impl;

import com.example.ems.model.Employee;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of Employee operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return employeeRepository.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeAndIdNot(String code, Long id) {
        return employeeRepository.existsByCodeAndIdNot(code, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> searchEmployees(String searchTerm, Long departmentId, Double minSalary, Double maxSalary) {
        return employeeRepository.searchEmployees(searchTerm, departmentId, minSalary, maxSalary);
    }
}
