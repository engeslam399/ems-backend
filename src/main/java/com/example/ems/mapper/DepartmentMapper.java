package com.example.ems.mapper;

import com.example.ems.dto.DepartmentDto;
import com.example.ems.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper for Department entity and DTO.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartmentMapper {

    DepartmentDto toDto(Department department);

    Department toEntity(DepartmentDto departmentDto);
}
