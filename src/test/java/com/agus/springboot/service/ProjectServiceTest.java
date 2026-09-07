package com.agus.springboot.service;

import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.exceptions.BusinessLogicException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        assertThrows(ResourceNotFoundException.class, () -> projectService.assignProjectToEmployee(idEmpl,idProj));
//assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.assignProjectToEmployee(idEmpl,idProj);
//        });

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

    @Test
    @DisplayName("Update project and returns updated Project DTO")
    void updateProject_ShouldUpdateAndReturnDTO_WhenProjectExists(){
        int idProject = 1;

        ProjectDTO inputDto = new ProjectDTO();
        inputDto.setName("New name");
        inputDto.setDescription("New desc");

        // db entity
        ProjectEntity existingEntity = new ProjectEntity();
        existingEntity.setId(idProject);
        existingEntity.setName("Old name");
        existingEntity.setDescription("Old desc");

        ProjectEntity updatedEntity = new ProjectEntity();
        updatedEntity.setId(idProject);
        updatedEntity.setName("New name");
        updatedEntity.setDescription("New desc");

        ProjectDTO expectedDto = new ProjectDTO();
        expectedDto.setId(idProject);
        expectedDto.setName("New name");
        expectedDto.setDescription("New desc");

        Mockito.when(projectDAO.findById(idProject)).thenReturn(Optional.of(existingEntity));
        Mockito.when(projectDAO.save(existingEntity)).thenReturn(updatedEntity);
        Mockito.when(projectMapper.toDto(updatedEntity)).thenReturn(expectedDto);


        ProjectDTO result = projectService.updateProject(inputDto, idProject);

        assertNotNull(result);
        assertEquals(idProject, result.getId());
        assertEquals("New name", result.getName());
        assertEquals("New desc", result.getDescription());

        assertEquals("New name", existingEntity.getName());
        assertEquals("New desc", existingEntity.getDescription());

        verify(projectDAO, times(1)).findById(idProject);
        verify(projectMapper, times(1)).toDto(updatedEntity);
        verify(projectDAO, times(1)).save(existingEntity);

    }


    @Test
    @DisplayName("Throws ResourceNotFoundException when project not found")
    void updateProject_ShouldThrowException_WhenProjectNotFound(){
        int nonValidProjId = 99;

        ProjectDTO projectDTO = new ProjectDTO();

        Mockito.when(projectDAO.findById(nonValidProjId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(projectDTO, nonValidProjId);
        });

        verify(projectDAO, times(1)).findById(nonValidProjId);
        verify(projectDAO, never()).save(any());
        verify(projectMapper, never()).toDto(any());

        }



    @Test
    @DisplayName("Returns paged ProjectDTOs when projects exist")
    void findAllProjects_ShouldReturnPagedDTOs(){
        int idProject = 1;

        Pageable pageable = PageRequest.of(0, 10); // Page 0, 10 elements per page

        ProjectEntity entity = new ProjectEntity();
        entity.setId(idProject);
        entity.setName("Paged Project");

        ProjectDTO expectedDto = new ProjectDTO();
        expectedDto.setId(idProject);
        expectedDto.setName("Paged Project");

        List<ProjectEntity> entityList = List.of(entity);
        Page<ProjectEntity> entityPage = new PageImpl<>(entityList, pageable, entityList.size());

        Mockito.when(projectDAO.findByIsActiveTrue(pageable)).thenReturn(entityPage);
        Mockito.when(projectMapper.toDto(entity)).thenReturn(expectedDto);

        Page<ProjectDTO> result = projectService.findAllProjects(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Paged Project", result.getContent().get(0).getName());


        verify(projectDAO, times(1)).findByIsActiveTrue(pageable);
        verify(projectMapper, times(1)).toDto(entity);
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when project not found")
    void deleteProject_ShouldThrowException_WhenProjectNotFound(){
        int nonValidId = 999;

        Mockito.when(projectDAO.findById(nonValidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(nonValidId));

        verify(projectDAO, times(1)).findById(nonValidId);
        verify(projectDAO, (never())).save(any());
    }


    @Test
    @DisplayName("Throws BusinessLogicException when employee.projects > MAX Projects")
    void assignProjectToEmployee_ShouldThrowBusinessLogicException_WhenEmployeeReachesMaxProjects(){
        int idNewProject = 4;
        int idEmployee = 1;

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(1);
        projectEntity.setIsActive(true);

        ProjectEntity projectEntity2 = new ProjectEntity();
        projectEntity2.setId(2);
        projectEntity2.setIsActive(true);

        ProjectEntity projectEntity3 = new ProjectEntity();
        projectEntity3.setId(3);
        projectEntity3.setIsActive(true);


        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setEmpno(idEmployee);
        employeeEntity.addProject(projectEntity);
        employeeEntity.addProject(projectEntity2);
        employeeEntity.addProject(projectEntity3);

        ProjectEntity newProject = new ProjectEntity();
        newProject.setId(idNewProject);
        newProject.setIsActive(true);


        Mockito.when(projectDAO.findById(idNewProject)).thenReturn(Optional.of(newProject));
        Mockito.when(employeeDAO.findById(idEmployee)).thenReturn(Optional.of(employeeEntity));

        assertThrows(BusinessLogicException.class, () -> projectService.assignProjectToEmployee(idEmployee, idNewProject));

        verify(projectDAO, times(1)).findById(idNewProject);
        verify(employeeDAO, times(1)).findById(idEmployee);
        verify(employeeDAO, never()).save(any());
    }



    @Test
    @DisplayName("Throws BusinessLogicException when project is not active")
    void assignProjectToEmployee_ShouldThrowBusinessLogicException_WhenProjectIsInactive(){
        int idEmployee = 1;
        int idProject = 10;

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(idProject);
        projectEntity.setIsActive(false);

        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setEmpno(idEmployee);

        Mockito.when(projectDAO.findById(idProject)).thenReturn(Optional.of(projectEntity));
        Mockito.when(employeeDAO.findById(idEmployee)).thenReturn(Optional.of(employeeEntity));

        assertThrows(BusinessLogicException.class, () -> projectService.assignProjectToEmployee(idEmployee, idProject));

        verify(projectDAO, times(1)).findById(idProject);
        verify(employeeDAO, times(1)).findById(idEmployee);
        verify(employeeDAO, never()).save(any());

    }

}
