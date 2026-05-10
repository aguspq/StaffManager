package com.agus.springboot.service;

import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.exceptions.BusinessLogicException;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.mappers.ProjectMapper;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.dao.IProjectDAO;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.model.entities.ProjectEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private static final int MAX_PROJECTS = 3;
    private final IProjectDAO projectDAO;

    private final IEmployeeDAO employeeDAO;
    private final ProjectMapper projectMapper;

    public ProjectService(IProjectDAO projectDAO, IEmployeeDAO employeeDAO, ProjectMapper projectMapper) {
        this.projectDAO = projectDAO;
        this.employeeDAO = employeeDAO;
        this.projectMapper = projectMapper;
    }

    // READ
    public Page<ProjectDTO> findAllProjects(Pageable pageable){
        return projectDAO.findByIsActiveTrue(pageable)
                .map(projectMapper::toDto);
    }

//    private ProjectDTO convertEntityToDTO(ProjectEntity project){
//        ProjectDTO projectDTO = new ProjectDTO(
//                project.getName(),
//                project.getDescription()
//        );
//        projectDTO.setId(project.getId());
//
//        return  projectDTO;
//    }

    public ProjectDTO saveProject(ProjectDTO projectDTO){
        // DTO --> entity + save() --> return DTO
        ProjectEntity project = projectMapper.toEntity(projectDTO);

        project.setIsActive(true);

        return projectMapper.toDto(projectDAO.save(project));
    }

    public ProjectDTO updateProject(ProjectDTO projectDTO, int id){
        ProjectEntity project = projectDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + id + " not found"));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());

        return projectMapper.toDto(projectDAO.save(project));

    }
    public void deleteProject(int id){
        ProjectEntity project = projectDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + id + " not found"));

        project.setIsActive(false);

        projectDAO.save(project);
    }

    @Transactional
    public void assignProjectToEmployee(int idEmployee, int idProject){
        ProjectEntity project = projectDAO.findById(idProject)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + idProject + " not found"));
        EmployeeEntity employee = employeeDAO.findById(idEmployee)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID: " + idEmployee + " not found"));

        if(employee.getProjects().size() >= MAX_PROJECTS)
            throw new BusinessLogicException("Employee has too many projects");

        if (!project.getIsActive())
            throw new BusinessLogicException(idProject +" is not active");

        employee.addProject(project);

        employeeDAO.save(employee);
    }

    public ProjectDTO findProjectById(int idProject){
        return projectMapper.toDto((projectDAO.findById(idProject)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"))));
    }

}
