package com.example.ems.mapper;

import com.example.ems.dto.EmployeeRequestDto;
import com.example.ems.dto.EmployeeResponseDto;
import com.example.ems.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper for Employee entity and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {DepartmentMapper.class})
public interface EmployeeMapper {

    @Mapping(target = "hasImage", expression = "java(employee.getImage() != null && employee.getImage().length > 0)")
    EmployeeResponseDto toResponseDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "department", ignore = true)
    Employee toEntity(EmployeeRequestDto employeeRequestDto);
}
