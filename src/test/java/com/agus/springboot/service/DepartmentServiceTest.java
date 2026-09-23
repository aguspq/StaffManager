package com.agus.springboot.service;

import com.agus.springboot.dto.DepartmentDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.entities.DeptEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @DisplayName("SUCCESS - Should save dept when is ok")
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

}
