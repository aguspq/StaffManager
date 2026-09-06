package com.agus.springboot.service;

import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.mappers.ProjectMapper;
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
    @Mock
    private IProjectDAO projectDAO;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    ProjectService projectService;

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
    @DisplayName("Throws ResourceNotFoundException when PROJECT not found ")
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
    @DisplayName("Throws ResourceNotFoundException when EMPLOYEE not found ")
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
    @DisplayName("Soft deletes project by setting isActive to false")
    void deleteProject_Succeed() {
        int idProj = 999;
        ProjectEntity project = new ProjectEntity();

        Mockito.when(projectDAO.findById(idProj)).thenReturn(Optional.of(project));

        projectService.deleteProject(idProj);

        assertFalse(project.getIsActive());

        verify(projectDAO, times(1)).findById(idProj);
        verify(projectDAO, times(1)).save(project);
    }

    @Test
    @DisplayName("Returns ProjectDTO when project exists")
    void findProjectById_ShouldReturnDTO_WhenProjectExists(){
        // arrange
        int idProject = 1;
        // DB
        ProjectEntity project = new ProjectEntity();
        project.setName("Agus");
        // mapper
        ProjectDTO expectedDto = new ProjectDTO();
        expectedDto.setName("Agus");

        Mockito.when(projectDAO.findById(idProject)).thenReturn(Optional.of(project));
        Mockito.when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // act
        ProjectDTO result = projectService.findProjectById(idProject);

        // assert
        assertNotNull(result);
        assertEquals(expectedDto.getName(), result.getName());

        // verify
        verify(projectDAO, times(1)).findById(idProject);
        verify(projectMapper, times(1)).toDto(project);

    }


    @Test
    @DisplayName("Throws ResourceNotFoundException when project not found")
    void findProjectById_ShouldThrowException_WhenProjectNotFound(){
        int nonValidId = 999;

        Mockito.when(projectDAO.findById(nonValidId)).thenReturn(Optional.empty());

        // act & assert
//        we call it here to catch the exception
        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findProjectById(nonValidId);
        });

        verify(projectDAO, times(1)).findById(nonValidId);
        verify(projectMapper, never()).toDto(any());

    }

    @Test
    @DisplayName("Saves project and returns saved ProjectDTO")
    void saveProject_ShouldSaveAndReturnDTO(){
        int idProject = 1;
        //
        ProjectDTO inputDto = new ProjectDTO();
        inputDto.setName("New Project");

//        entity created by Mapper  (before DB)
        ProjectEntity entityToSave = new ProjectEntity();
        entityToSave.setName("New Project");

//        after .save() (with ID)
        ProjectEntity savedEntity = new ProjectEntity();
        savedEntity.setName("New Project");
        savedEntity.setIsActive(true);
        savedEntity.setId(idProject);


        ProjectDTO savedDto = new ProjectDTO();
        savedDto.setId(idProject);
        savedDto.setName("New Project");

        Mockito.when(projectMapper.toEntity(inputDto)).thenReturn(entityToSave);
        Mockito.when(projectDAO.save(entityToSave)).thenReturn(savedEntity);
        Mockito.when(projectMapper.toDto(savedEntity)).thenReturn(savedDto);

        // act
        ProjectDTO result = projectService.saveProject(inputDto);

        // assert

        assertNotNull(result);
        assertEquals(idProject, result.getId());
        assertEquals("New Project", result.getName());
        assertTrue(entityToSave.getIsActive(), "The service must set isActive to true before saving");

        verify(projectMapper, times(1)).toEntity(inputDto);
        verify(projectDAO, times(1)).save(entityToSave);
        verify(projectMapper, times(1)).toDto(savedEntity);
    }

}
