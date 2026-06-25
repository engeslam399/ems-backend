package com.example.ems.controller;

import com.example.ems.dto.DepartmentDto;
import com.example.ems.dto.EmployeeRequestDto;
import com.example.ems.dto.EmployeeResponseDto;
import com.example.ems.exception.DuplicateCodeException;
import com.example.ems.model.Department;
import com.example.ems.model.Employee;
import com.example.ems.mapper.EmployeeMapper;
import com.example.ems.service.DepartmentService;
import com.example.ems.service.EmployeeService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing employees.
 * Enables Cross-Origin Resource Sharing (CORS) for Angular requests on port
 * 4200.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final EmployeeMapper employeeMapper;

    /**
     * Lists employees, applying optional search filters.
     */
    @GetMapping
    public List<EmployeeResponseDto> listEmployees(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "minSalary", required = false) Double minSalary,
            @RequestParam(value = "maxSalary", required = false) Double maxSalary) {

        return employeeService.searchEmployees(searchTerm, departmentId, minSalary, maxSalary).stream()
                .map(employeeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single employee by ID.
     */
    @GetMapping("/{id}")
    public EmployeeResponseDto getEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        return employeeMapper.toResponseDto(employee);
    }

    /**
     * Creates a new employee. Handles validation, duplicate checking, and image
     * upload.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto saveEmployee(
            @ModelAttribute @Valid EmployeeRequestDto employeeDto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        // Validate duplicate employee code
        if (employeeDto.getCode() != null && !employeeDto.getCode().trim().isEmpty()) {
            if (employeeService.existsByCode(employeeDto.getCode())) {
                throw new DuplicateCodeException("code", "Employee code already exists");
            }
        }

        Employee employee = employeeMapper.toEntity(employeeDto);
        Department department = departmentService.findById(employeeDto.getDepartmentId());
        if (department == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department not found");
        }
        employee.setDepartment(department);

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                employee.setImage(imageFile.getBytes());
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading image upload");
        }

        Employee saved = employeeService.save(employee);
        return employeeMapper.toResponseDto(saved);
    }

    /**
     * Updates an existing employee.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmployeeResponseDto updateEmployee(
            @PathVariable("id") Long id,
            @ModelAttribute @Valid EmployeeRequestDto employeeDto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Employee existingEmployee = employeeService.findById(id);
        if (existingEmployee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }

        // Validate duplicate employee code (excluding current employee ID)
        if (employeeDto.getCode() != null && !employeeDto.getCode().trim().isEmpty()) {
            if (employeeService.existsByCodeAndIdNot(employeeDto.getCode(), id)) {
                throw new DuplicateCodeException("code", "Employee code already exists");
            }
        }

        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setId(id); // Ensure we are updating the correct employee
        Department department = departmentService.findById(employeeDto.getDepartmentId());
        if (department == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department not found");
        }
        employee.setDepartment(department);

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                employee.setImage(imageFile.getBytes());
            } else {
                // Retain existing image
                employee.setImage(existingEmployee.getImage());
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading image upload");
        }

        Employee saved = employeeService.save(employee);
        return employeeMapper.toResponseDto(saved);
    }

    /**
     * Deletes an employee.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        employeeService.deleteById(id);
    }

    /**
     * Serves the employee image.
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getEmployeeImage(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id);
        if (employee != null && employee.getImage() != null && employee.getImage().length > 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(employee.getImage());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    }
}
