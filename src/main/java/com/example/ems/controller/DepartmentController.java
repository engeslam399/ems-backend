package com.example.ems.controller;

import com.example.ems.dto.DepartmentDto;
import com.example.ems.exception.DuplicateCodeException;
import com.example.ems.model.Department;
import com.example.ems.mapper.DepartmentMapper;
import com.example.ems.service.DepartmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing departments.
 * Enables Cross-Origin Resource Sharing (CORS) for Angular requests on port
 * 4200.
 */

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    /**
     * Retrieves all departments.
     *
     * @return list of department DTOs
     */
    @GetMapping
    public List<DepartmentDto> listDepartments() {
        return departmentService.findAll().stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new department.
     *
     * @param departmentDto the department details
     * @return the created department DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto saveDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        // Validate duplicate department code
        if (departmentDto.getCode() != null && !departmentDto.getCode().trim().isEmpty()) {
            if (departmentService.existsByCode(departmentDto.getCode())) {
                throw new DuplicateCodeException("code", "Department code already exists");
            }
        }

        Department department = departmentMapper.toEntity(departmentDto);
        Department saved = departmentService.save(department);
        return departmentMapper.toDto(saved);
    }
}
