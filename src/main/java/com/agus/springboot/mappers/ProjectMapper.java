package com.agus.springboot.mappers;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.model.entities.ProjectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO toDto(ProjectEntity project);
    ProjectEntity toEntity (ProjectDTO projectDTO);
}
