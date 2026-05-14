package com.agus.springboot.mappers;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.service.EmployeeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class})

public interface EmployeeMapper {
    @Mapping(source = "ename", target = "name") // source --> parameter (EmployeeEntity)  || target --> return (EmployeesDTO)
    @Mapping(source = "dept.deptno", target = "deptNo")
    @Mapping(source = "dept.dname", target = "deptName")
    @Mapping(source = "dept.loc", target = "deptLocation")
    @Mapping(source = "projects", target = "projectDTOSet")
    EmployeesDTO toDto(EmployeeEntity employee);

    @Mapping(source = "name", target = "ename")
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "dept", ignore = true)
    @Mapping(target = "active", ignore = true)
    EmployeeEntity toEntity(EmployeesDTO employeesDTO);
}
