package com.agus.springboot.service;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
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

//Un Mock es un objeto "de mentira" o un simulador

// How to TEST (the 3 AAA's):
// 1- Arrange
// Create fake data and program Mocks
// 2- Act
// Call service method to check
// 3- Assert/Verify
// Check that the result is as expected


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private IEmployeeDAO employeeDAO;
    @Mock private IDeptDAO deptDAO;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Return DTO if Employee exists")
    void findEmployeeById_ShouldReturnDto_WhenEmployeeExists(){
        int id = 1;

        EmployeeEntity mockEntity = new EmployeeEntity();
        mockEntity.setEmpno(id);
        mockEntity.setEname("Agus");
        // write the mandatory

//        "trick" DB. DAO return X (mockEntity)
        Mockito.when(employeeDAO.findById(id)).thenReturn(Optional.of(mockEntity));

        // act
        EmployeesDTO result = employeeService.findEmployeeById(id);

        // verify
        assertNotNull(result);
        assertEquals("Agus", result.getName());

        verify(employeeDAO, times(1)).findById(id);
        // checks if I call the DAO
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
    @DisplayName(("Saves employee"))
    void saveEmployee_ShouldReturnSavedDto_WhenDataIsCorrect(){
        // 1. SET / ARRANGE
        // Create INPUT DTO (what user sends)
        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setName("Agus");
        inputDto.setJob("DEV");
        inputDto.setDeptNo(20);

        // Creamos las ENTIDADES para los Mocks "simuladas"
        DeptEntity deptEntity = new DeptEntity(20, "RESEARCH", "DALLAS", true);
        EmployeeEntity savedEntity = new EmployeeEntity("Agus", "DEV", deptEntity);
        savedEntity.setEmpno(100);

        // MOCKITO: Program answers
        Mockito.when(deptDAO.findById(20)).thenReturn(Optional.of(deptEntity));
        Mockito.when(employeeDAO.save(any(EmployeeEntity.class))).thenReturn(savedEntity);

        // 2. ACT
        // Llamamos con el DTO, como lo haría el Controller
        EmployeesDTO result = employeeService.saveEmployee(inputDto);

        // 3. ASSERT
        assertNotNull(result);
        assertEquals(100, result.getEmpno());
        assertEquals("Agus", result.getName());

        // 4. VERIFY
        verify(employeeDAO, times(1)).save(any(EmployeeEntity.class));
//        assertNotNull(result);
//        assertEquals("Agus", result.getName());

//        "deptno"	"dname"	"loc"	"isactive"
//        20	"RESEARCH"	"DALLAS"	true
    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when ID does not exists")
    public void saveEmployee_ShouldThrowException_WhenDeptDoesNotExist(){
        int nonExistentID = 99;

        // 1. SET / ARRANGE
        // Create INPUT DTO (what user sends)
        EmployeesDTO inputDto = new EmployeesDTO();
        inputDto.setName("Agus");
        inputDto.setJob("DEV");
        inputDto.setDeptNo(nonExistentID);

        // Creamos las ENTIDADES para los Mocks "simuladas"
        DeptEntity deptEntity = new DeptEntity(20, "RESEARCH", "DALLAS", true);
        EmployeeEntity savedEntity = new EmployeeEntity("Agus", "DEV", deptEntity);
        savedEntity.setEmpno(100);

        // MOCKITO: Program answers
        Mockito.when(deptDAO.findById(nonExistentID)).thenReturn(Optional.empty());
        Mockito.when(employeeDAO.save(any(EmployeeEntity.class))).thenReturn(savedEntity);

        // 2. ACT
        // Llamamos con el DTO, como lo haría el Controller
        EmployeesDTO result = employeeService.saveEmployee(inputDto);

        // verify
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.findEmployeeById(nonExistentID);
        });

        verify(employeeDAO, times(1)).findById(id);
    }

}
