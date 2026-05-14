package com.agus.springboot.mappers;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.model.entities.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO toDto(ProjectEntity project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "employees", ignore = true)
    ProjectEntity toEntity (ProjectDTO projectDTO);
}
