package com.agus.springboot.service;

import com.agus.springboot.dto.DepartmentDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    private IDeptDAO deptDAO;

    @InjectMocks
    private DepartmentService departmentService;


    @Test
    @DisplayName("SUCCESS - findAllDepartments - Returns only active departments mapped to DTOs")
    void findAllDepartments_ShouldReturnOnlyActiveDepartments(){
        DeptEntity activeDept = new DeptEntity(1, "IT", "Madrid", true);
        DeptEntity inactiveDept = new DeptEntity(2, "HR", "Barcelona", false);

        List<DeptEntity> deptEntityList = List.of(activeDept, inactiveDept);

        Mockito.when(deptDAO.findAll()).thenReturn(deptEntityList);

        List<DepartmentDTO> resultList = departmentService.findAllDepartments();

        assertNotNull(resultList);
        assertEquals(1, resultList.size(), "Should return only active departments");
        assertEquals(activeDept.getDname(), resultList.get(0).getName());

        verify(deptDAO, times(1)).findAll();


    }

    @Test
    @DisplayName("SUCCESS - findDeptById - Returns DepartmentDTO when department exists and is active")
    void findDeptById_ShouldReturnDeptWhenDeptIsActive(){
        int idDept = 1;

        DeptEntity dbDept = new DeptEntity();
        dbDept.setDeptno(idDept);
        dbDept.setDname("IT");
        dbDept.setIsActive(true);

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.of(dbDept));

        DepartmentDTO resultDto = departmentService.findDeptById(idDept);

        assertNotNull(resultDto);
        assertEquals("IT", resultDto.getName());

        verify(deptDAO, times(1)).findById(idDept);
    }

    @Test
    @DisplayName("FAIL - findDeptById - Throws ResourceNotFoundException when department not found")
    void findDeptById_ShouldThrowResourceNotFoundException(){
        int idDept = 99;

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.findDeptById(idDept);
        });

        verify(deptDAO, times(1)).findById(idDept);
    }


    // when is not active


    @Test
    @DisplayName("FAIL - findDeptById - Throws ResourceNotFoundException when department is not active")
    void findDeptById_ShouldThrowResourceNotFoundException_WhenDeptIsNotActive(){
        int idDept = 1;

        DeptEntity dbDept = new DeptEntity();
        dbDept.setDeptno(idDept);
        dbDept.setDname("IT");
        dbDept.setIsActive(false);

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.of(dbDept));

        assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.findDeptById(idDept);
        });

        verify(deptDAO, times(1)).findById(idDept);
    }

    @Test
    @DisplayName("SUCCESS - saveDept - Should save dept when is ok")
    void saveDept_ShouldSaveDept(){
        DepartmentDTO inputDto = new DepartmentDTO();
        inputDto.setName("IT");
        inputDto.setLocation("Madrid");

        DeptEntity savedEntity = new DeptEntity();
        savedEntity.setDeptno(10);
        savedEntity.setDname("IT");
        savedEntity.setLoc("Madrid");
        savedEntity.setIsActive(true);

        Mockito.when(deptDAO.save(any(DeptEntity.class))).thenReturn(savedEntity);

        DepartmentDTO resultDto = departmentService.saveDept(inputDto);

        assertNotNull(resultDto);
        assertEquals(10, resultDto.getDeptNo());
        assertEquals("IT", resultDto.getName());
        assertEquals("Madrid", resultDto.getLocation());

        verify(deptDAO, times(1)).save(any(DeptEntity.class));

    }

    @Test
    @DisplayName("FAIL - saveDept - Throws ResourceNotFoundException when ID is provided")
    void saveDept_ShouldThrowResourceNotFoundException_WhenDeptHasId(){
        DepartmentDTO inputDto = new DepartmentDTO();
        inputDto.setDeptNo(1);

        assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.saveDept(inputDto);
        });

        verify(deptDAO, never()).save(any(DeptEntity.class));
    }


    @Test
    @DisplayName("SUCCESS - updateDepartment")
    void updateDepartment_ShouldReturnDepartmentDTO(){
        int idDept = 1;

        DepartmentDTO inputDto = new DepartmentDTO();
        inputDto.setDeptNo(idDept);
        inputDto.setName("New name");
        inputDto.setLocation("New loc");

        DeptEntity oldDept = new DeptEntity();
        oldDept.setDeptno(idDept);
        oldDept.setDname("Old name");
        oldDept.setLoc("Old loc");

        DeptEntity savedDept = new DeptEntity();
        savedDept.setDeptno(idDept);
        savedDept.setIsActive(true);
        savedDept.setDname("New name");
        savedDept.setLoc("New loc");

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.of(oldDept));
        Mockito.when(deptDAO.save(oldDept)).thenReturn(savedDept);

        DepartmentDTO result = departmentService.updateDepartment(idDept, inputDto);

        assertNotNull(result);
        assertEquals("New name", result.getName());
        assertEquals("New loc", result.getLocation());
        assertEquals(idDept, result.getDeptNo());

        verify(deptDAO,times(1)).findById(idDept);
        verify(deptDAO,times(1)).save(oldDept);

    }

    @Test
    @DisplayName("FAIL - updateDepartment - Throws ResourceNotFoundException when Id not found")
    void updateDepartment_ShouldThrowResourceNotFoundExceptionWhenIdNotFound(){
        int nonValidId = 99;
        DepartmentDTO deptDto = new DepartmentDTO();

        Mockito.when(deptDAO.findById(nonValidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(nonValidId, deptDto));

        verify(deptDAO, times(1)).findById(nonValidId);
        verify(deptDAO, never()).save(any());

    }

    @Test
    @DisplayName("FAIL - updateDepartment - Throws ResourceNotFoundException when Id not found")
    void updateDepartment_ShouldThrowResourceNotFoundExceptionWhenDeptIsNotActive(){
        int deptId = 1;

        DeptEntity deptEntity = new DeptEntity();
        deptEntity.setIsActive(false);

        DepartmentDTO deptDto = new DepartmentDTO();


        Mockito.when(deptDAO.findById(deptId)).thenReturn(Optional.of(deptEntity));

        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(deptId, deptDto));

        verify(deptDAO, times(1)).findById(deptId);
        verify(deptDAO, never()).save(any());

    }


    @Test
    @DisplayName("SUCCESS - deleteDept - Soft deletes department and set dept to null to all employees")
    void deleteDept_ShouldSoftDeleteDept(){
        int idDept = 1;

        EmployeeEntity employee = new EmployeeEntity();

        DeptEntity dbDept = new DeptEntity();
        dbDept.setIsActive(true);
        dbDept.setDeptno(idDept);
        dbDept.getEmployees().add(employee);

        employee.setDept(dbDept);

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.of(dbDept));
//        Mockito.when(deptDAO.save())

        departmentService.deleteDept(idDept);

        ArgumentCaptor<DeptEntity> deptCaptor = ArgumentCaptor.forClass(DeptEntity.class);


        verify(deptDAO, times(1)).findById(idDept);
        // we capture the saved Entity
        verify(deptDAO, times(1)).save(deptCaptor.capture());

        DeptEntity savedDept = deptCaptor.getValue();

        // we check the savedEntity
        assertFalse(savedDept.getIsActive());
        assertNull(employee.getDept());

    }


    @Test
    @DisplayName("f")
    void deleteDept_ShouldThrowResourceNotFoundException_WhenIdNotFound(){
        int idDept = 1;

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDept(idDept));

        verify(deptDAO, times(1)).findById(idDept);
        verify(deptDAO, never()).save(any());

    }

    @Test
    @DisplayName("When dept is not active")
    void deleteDept_ShouldThrowResourceNotFoundException_WhenDeptIsNotActive(){
        int idDept = 1;

        DeptEntity dbDept = new DeptEntity();
        dbDept.setIsActive(false);

        Mockito.when(deptDAO.findById(idDept)).thenReturn(Optional.of(dbDept));

        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDept(idDept));

        verify(deptDAO, times(1)).findById(idDept);
        verify(deptDAO, never()).save(any());

    }

}
