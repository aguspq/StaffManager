package com.agus.springboot.service;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.mappers.EmployeeMapper;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// A Mock is a "fake" object or a simulator

// How to TEST (the 3 AAA's):
// 1- Arrange
// Create "fake" data and program Mocks
// 2- Act
// Call service method to check
// 3- Assert/Verify
// Check that the result is as expected


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private IEmployeeDAO employeeDAO;
    @Mock private IDeptDAO deptDAO;
    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Return DTO if Employee exists")
    void findEmployeeById_ShouldReturnDto_WhenEmployeeExists(){
        final int emplId = 1;
        final String emplName = "Agus";

//         arrange
        EmployeeEntity dbEntity = new EmployeeEntity();
        dbEntity.setEmpno(emplId);

        EmployeesDTO emplDto = new EmployeesDTO();
        emplDto.setEmpno(emplId);
        emplDto.setName(emplName);

        Mockito.when(employeeDAO.findById(emplId)).thenReturn(Optional.of(dbEntity));
        Mockito.when(employeeMapper.toDto(dbEntity)).thenReturn(emplDto);

//        act
        EmployeesDTO savedEmployee = employeeService.findEmployeeById(emplId);

//            assert
        assertNotNull(savedEmployee);
        assertEquals(emplId, savedEmployee.getEmpno());
        assertEquals(emplName, savedEmployee.getName());

//        verify
        verify(employeeDAO, times(1)).findById(emplId);
        verify(employeeMapper, times(1)).toDto(dbEntity);

    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when ID does not exists")
    void findEmployeeById_ShouldThrowException_WhenIdDoesNotExist(){
        int id = 99;
    //Arrange. DAO returns an empty object
        Mockito.when(employeeDAO.findById(id)).thenReturn(Optional.empty());

        // verify
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.findEmployeeById(id);
        });

        verify(employeeDAO, times(1)).findById(id);

    }

    @Test
    @DisplayName("Saves employee")
    void saveEmployee_ShouldReturnSavedDto_WhenDataIsCorrect(){
        final int idEmpl = 10;
        final int idDept = 20;
        //        arrange
        EmployeesDTO intputDto = new EmployeesDTO();
        intputDto.setDeptNo(idDept);

        DeptEntity dept = new DeptEntity();
        dept.setDeptno(idDept);

        EmployeeEntity employee = new EmployeeEntity();
        EmployeesDTO outputDto = new EmployeesDTO();
        outputDto.setEmpno(idEmpl);

        Mockito.when(deptDAO.findById(intputDto.getDeptNo())).thenReturn(Optional.of(dept));
        Mockito.when(employeeMapper.toEntity(intputDto)).thenReturn(employee);
        Mockito.when(employeeDAO.save(employee)).thenReturn(employee);
        Mockito.when(employeeMapper.toDto(employee)).thenReturn(outputDto);

//        act
        EmployeesDTO savedEmpl = employeeService.saveEmployee(intputDto);

//        assert
        assertNotNull(savedEmpl);
        assertEquals(idEmpl, savedEmpl.getEmpno());

//         verify
        verify(deptDAO, times(1)).findById(idDept);
        verify(employeeMapper, times(1)).toEntity(intputDto);
        verify(employeeDAO, times(1)).save(employee);
        verify(employeeMapper, times(1)).toDto(employee);

    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when DEPT does not exists")
    public void saveEmployee_ShouldThrowException_WhenDeptNotFound(){
        int nonExistentID = 99;
        // Create INPUT DTO (what user sends)
        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setName("Agus");
        inputDto.setDeptNo(nonExistentID);

//        Arrange
        Mockito.when(deptDAO.findById(nonExistentID)).thenReturn(Optional.empty());


        // verify
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(inputDto);
        });

        verify(deptDAO, times(1)).findById(nonExistentID);
        verify(employeeDAO, never()).save(any());
        
    }

    @Test
    @DisplayName("Throw exception if DTO has ID")
    void saveEmployee_ShouldThrowException_WhenIdIsProvided() {
        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setEmpno(1);

        assertThrows(ResourceNotFoundException.class, () ->{
            employeeService.saveEmployee(inputDto);
        });

        verify(deptDAO, never()).findById(anyInt());
        verify(employeeDAO, never()).save(any());

    }

}
