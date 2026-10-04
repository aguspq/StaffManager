package com.agus.springboot.mappers;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class MappersTest {

    private EmployeeMapper employeeMapper;
    private ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        projectMapper = Mappers.getMapper(ProjectMapper.class);
        employeeMapper = Mappers.getMapper(EmployeeMapper.class);

        ReflectionTestUtils.setField(employeeMapper, "projectMapper", projectMapper);
    }

    // ==========================================
    // EMPLOYEE MAPPER TESTS
    // ==========================================

    @Test
    @DisplayName("EmployeeMapper - Should map EmployeeEntity to EmployeesDTO correctly")
    void shouldMapEmployeeEntityToDTO() {
        // Given
        DeptEntity dept = new DeptEntity();
        dept.setDeptno(10);
        dept.setDname("IT");

        EmployeeEntity entity = new EmployeeEntity();
        entity.setEmpno(1);
        entity.setEname("Agustin");
        entity.setJob("Developer");
        entity.setDept(dept);

        // When
        EmployeesDTO dto = employeeMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmpno()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Agustin");
        assertThat(dto.getJob()).isEqualTo("Developer");
        assertThat(dto.getDeptNo()).isEqualTo(10);
        assertThat(dto.getDeptName()).isEqualTo("IT");
    }

    @Test
    @DisplayName("EmployeeMapper - Should map EmployeesDTO to EmployeeEntity correctly")
    void shouldMapEmployeeDTOToEntity() {
        // Given
        EmployeesDTO dto = new EmployeesDTO();
        dto.setEmpno(1);
        dto.setName("Agustin");
        dto.setJob("Developer");

        // When
        EmployeeEntity entity = employeeMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getEmpno()).isEqualTo(1);
        assertThat(entity.getEname()).isEqualTo("Agustin");
        assertThat(entity.getJob()).isEqualTo("Developer");
    }

    @Test
    @DisplayName("EmployeeMapper - Should return null when input Entity or DTO is null")
    void shouldReturnNullWhenEmployeeInputIsNull() {
        assertThat(employeeMapper.toDto(null)).isNull();
        assertThat(employeeMapper.toEntity(null)).isNull();
    }
}