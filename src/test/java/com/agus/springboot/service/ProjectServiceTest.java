package com.agus.springboot.service;

import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.dao.IProjectDAO;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.model.entities.ProjectEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)

class ProjectServiceTest {
    @Mock
    private IEmployeeDAO employeeDAO;
    @Mock private IProjectDAO projectDAO;
    @InjectMocks ProjectService projectService;

    @Test
    @DisplayName("Assign project to employee")
    void assignProjectToEmployee_Should_Succeed(){
//       1-ARRANGE
        int idEmpl = 1;
        int idProj = 10;
        ProjectEntity project = new ProjectEntity();
        EmployeeEntity employee = new EmployeeEntity();

        project.setId(idProj);
        employee.setEmpno(idEmpl);

        Mockito.when(projectDAO.findById(idProj)).thenReturn(Optional.of(project));
        Mockito.when(employeeDAO.findById(idEmpl)).thenReturn(Optional.of(employee));

//        2- ACT
        projectService.assignProjectToEmployee(idEmpl, idProj);

//        3- ASSERT
        // Check if the employee was added to the project's set
        assertTrue(project.getEmployees().contains(employee), "Employee should be in the project's set");
        // Check if the project was added to the employee's set
        assertTrue(employee.getProjects().contains(project), "Project should be in the employee's set");


//        4- VERIFY
        verify(projectDAO, times(1)).findById(idProj);
        verify(employeeDAO, times(1)).findById(idEmpl);
        verify(employeeDAO, times(1)).save(employee);

    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when PROJECT doesn't exists ")
    void assignProjectToEmployee_ShouldThrowException_WhenProjNotFound(){
        int idProj = 99;
        int idEmpl = 999;

        Mockito.when(projectDAO.findById(idProj)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.assignProjectToEmployee(idEmpl,idProj);
        });

        verify(projectDAO, times(1)).findById(idProj);

        verify(employeeDAO, never()).findById(anyInt());
        verify(employeeDAO, never()).save(any());

        }

    @Test
    @DisplayName("Throws ResourceNotFoundException when EMPLOYEE doesn't exists ")
    void assignProjectToEmployee_ShouldThrowException_WhenEmplNotFound() {
        int idProj = 99;
        int idEmpl = 999;
        ProjectEntity project = new ProjectEntity();

        Mockito.when(projectDAO.findById(idProj)).thenReturn(Optional.of(project));
        Mockito.when(employeeDAO.findById(idEmpl)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
           projectService.assignProjectToEmployee(idEmpl, idProj);
        });

        verify(employeeDAO, times(1)).findById(idEmpl);
        verify(projectDAO, times(1)).findById(idProj);
        verify(employeeDAO, never()).save(any());

    }

    @Test
    @DisplayName("Deletes project")
    void deleteProject_Succeed() {
        int idProj = 999;
        ProjectEntity project = new ProjectEntity();

        Mockito.when(projectDAO.findById(idProj)).thenReturn(Optional.of(project));

        projectService.deleteProject(idProj);

        assertFalse(project.getIsActive());

        verify(projectDAO, times(1)).findById(idProj);
        verify(projectDAO, times(1)).save(project);
    }
}
