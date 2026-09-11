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
        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setDeptNo(idDept);

        DeptEntity dept = new DeptEntity();
        dept.setDeptno(idDept);

        EmployeeEntity employee = new EmployeeEntity();
        EmployeesDTO outputDto = new EmployeesDTO();
        outputDto.setEmpno(idEmpl);

        Mockito.when(deptDAO.findById(inputDto.getDeptNo())).thenReturn(Optional.of(dept));
        Mockito.when(employeeMapper.toEntity(inputDto)).thenReturn(employee);
        Mockito.when(employeeDAO.save(employee)).thenReturn(employee);
        Mockito.when(employeeMapper.toDto(employee)).thenReturn(outputDto);

//        act
        EmployeesDTO savedEmpl = employeeService.saveEmployee(inputDto);

//        assert
        assertNotNull(savedEmpl);
        assertEquals(idEmpl, savedEmpl.getEmpno());

//         verify
        verify(deptDAO, times(1)).findById(idDept);
        verify(employeeMapper, times(1)).toEntity(inputDto);
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
    @DisplayName("Throws IllegalArgumentException if DTO has ID")
    void saveEmployee_ShouldThrowException_WhenIdIsProvided() {
        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setEmpno(1);

        assertThrows(IllegalArgumentException.class, () ->{
            employeeService.saveEmployee(inputDto);
        });

        verify(deptDAO, never()).findById(anyInt());
        verify(employeeDAO, never()).save(any());

    }

    @Test
    @DisplayName("Soft Deletes employee")
    void deleteUser_ShouldSetChangeActiveToFalse_WhenEmployeeExists() {
//        arrange
        final int idEmpl = 10;

        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmpno(idEmpl);
        employee.setActive(true);

        Mockito.when(employeeDAO.findById(idEmpl)).thenReturn(Optional.of(employee));

//        act
        employeeService.deleteUser(idEmpl);

//        assert
        assertFalse(employee.getActive(), "'Active' should change to 'false'");

        verify(employeeDAO, times(1)).findById(idEmpl);
        verify(employeeDAO, times(1)).save(employee);
    }

    @Test
    @DisplayName("Try to apply soft delete but employee does not exists")
    void deleteUser_ShouldThrowException_WhenEmployeeDoesNotExists(){
//        arrange
        final int idEmpl = 999;

        Mockito.when(employeeDAO.findById(idEmpl)).thenReturn(Optional.empty());

//        assert
        assertThrows(ResourceNotFoundException.class, () ->{
            employeeService.deleteUser(idEmpl);
        });
//        verify
        verify(employeeDAO, times(1)).findById(idEmpl);
        verify(employeeDAO, never()).save(any());

    }

    @Test
    @DisplayName("Updates employee")
    void updateEmployee_ShouldReturnUpdatedDTO(){
//        arrange
        final int input = 1;

        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setName("New name");
        inputDto.setJob("New job");

        EmployeeEntity employeeDb = new EmployeeEntity();
        employeeDb.setEmpno(1);
        employeeDb.setEname("Old name");
        employeeDb.setJob("Old job");


        EmployeesDTO expectedDto = new EmployeesDTO();
        expectedDto.setName("New name");
        expectedDto.setJob("New job");

        Mockito.when(employeeDAO.findById(input)).thenReturn(Optional.of(employeeDb));
        Mockito.when(employeeDAO.save(employeeDb)).thenReturn(employeeDb);
        Mockito.when(employeeMapper.toDto(employeeDb)).thenReturn(expectedDto);

//        act
        EmployeesDTO outputDto = employeeService.updateEmployee(input, inputDto);


//        assert
        assertNotNull(outputDto);

        assertEquals("New name", employeeDb.getEname());
        assertEquals("New job", employeeDb.getJob());


//        verify

        verify(employeeDAO, times(1)).findById(input);
        verify(employeeDAO, times(1)).save(employeeDb);
        verify(employeeMapper, times(1)).toDto(employeeDb);
        verify(deptDAO, never()).findById(any());

    }

    @Test
    @DisplayName("Updates employee with dept")
    void updateEmployee_ShouldReturnUpdatedDTO_withDept(){
        final int idEmployee = 1;
        final int idDept = 10;

//        arrange
        EmployeeEntity dbEmployee = new EmployeeEntity();
        dbEmployee.setEmpno(idEmployee);
        dbEmployee.setEname("Old name");
        dbEmployee.setJob("Old job");

        DeptEntity dbDept = new DeptEntity();
        dbDept.setDeptno(idDept);

        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setDeptNo(idDept);
        inputDto.setName("Agus");
        inputDto.setJob("Dev");

        // return
        EmployeesDTO expectedDto = new EmployeesDTO();
        expectedDto.setEmpno(idEmployee);
        expectedDto.setName("Agus");
        expectedDto.setJob("Dev");
        expectedDto.setDeptNo(idDept);

        Mockito.when(employeeDAO.findById(idEmployee)).thenReturn(Optional.of(dbEmployee));
        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.of(dbDept));
        Mockito.when(employeeDAO.save(dbEmployee)).thenReturn(dbEmployee);
        Mockito.when(employeeMapper.toDto(dbEmployee)).thenReturn(expectedDto);

//        act
        EmployeesDTO resultDto = employeeService.updateEmployee(idEmployee, inputDto);

//        assert
        assertNotNull(resultDto);
        assertEquals("Agus", dbEmployee.getEname());
        assertEquals("Dev", dbEmployee.getJob());
        assertEquals(idEmployee, dbEmployee.getEmpno());
        assertEquals(idDept, dbEmployee.getDept().getDeptno());

//        verify
        verify(employeeDAO, times(1)).findById(idEmployee);
        verify(deptDAO, times(1)).findById(idDept);
        verify(employeeDAO, times(1)).save(dbEmployee);
        verify(employeeMapper, times(1)).toDto(dbEmployee);
    }


    @Test
    @DisplayName("Throws ResourceNotFoundException when employee not found")
    void updateEmployee_ShouldThrowException_WhenEmployeeNotFound(){
        int nonValidId = 999;

        EmployeesDTO employee = new EmployeesDTO();

        Mockito.when(employeeDAO.findById(nonValidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(nonValidId, employee));

        verify(employeeDAO, times(1)).findById(nonValidId);
        verify(deptDAO, never()).findById(any());
        verify(employeeMapper, never()).toDto(any());
        verify(employeeDAO, never()).save(any());

    }



    @Test
    @DisplayName("Throws ResourceNotFoundException when Dept not found")
    void updateEmployee_ShouldThrowException_WhenDeptNotFound(){
        int nonValidId = 99;
        int employeeId = 1;

        EmployeeEntity dbEmployee = new EmployeeEntity();

        EmployeesDTO updatedEmployee = new EmployeesDTO();
        updatedEmployee.setDeptNo(nonValidId);

        Mockito.when(employeeDAO.findById(employeeId)).thenReturn(Optional.of(dbEmployee));
        Mockito.when(deptDAO.findById(nonValidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(employeeId, updatedEmployee));

        verify(employeeDAO, times(1)).findById(employeeId);
        verify(deptDAO, times(1)).findById(nonValidId);
        verify(employeeMapper, never()).toDto(any());
        verify(employeeDAO, never()).save(any());

    }

    @Test
    @DisplayName("Saves employee successfully when department is null")
    void saveEmployee_ShouldWork_WhenDeptIsNull() {
        int idEmpl = 1;

        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setName("New Employee");
        inputDto.setDeptNo(null);

        EmployeeEntity employeeEntity = new EmployeeEntity();
        EmployeesDTO outputDto = new EmployeesDTO();
        outputDto.setEmpno(idEmpl);

        Mockito.when(employeeMapper.toEntity(inputDto)).thenReturn(employeeEntity);
        Mockito.when(employeeDAO.save(employeeEntity)).thenReturn(employeeEntity);
        Mockito.when(employeeMapper.toDto(employeeEntity)).thenReturn(outputDto);

        // Act
        EmployeesDTO savedEmpl = employeeService.saveEmployee(inputDto);

        // Assert
        assertNotNull(savedEmpl);
        assertEquals(idEmpl, savedEmpl.getEmpno());

        // Verify
        verify(deptDAO, never()).findById(any()); // Comprobamos que no se consulta la BD si deptNo es null
        verify(employeeDAO, times(1)).save(employeeEntity);
    }

}
