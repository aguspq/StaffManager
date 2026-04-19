package com.agus.springboot.service;

import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.dao.IProjectDAO;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.model.entities.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    @Autowired
    private IProjectDAO projectDAO;
    @Autowired
    private IEmployeeDAO employeeDAO;

    // READ
    public Page<ProjectDTO> findAllProjects(Pageable pageable){
        return projectDAO.findByIsActiveTrue(pageable)
                .map(this::convertEntityToDTO);
    }

    private ProjectDTO convertEntityToDTO(ProjectEntity project){
        ProjectDTO projectDTO = new ProjectDTO(
                project.getName(),
                project.getDescription()
        );
        projectDTO.setId(project.getId());

        return  projectDTO;
    }

    public ProjectDTO saveProject(ProjectDTO projectDTO){
        // DTO --> entity + save() --> return DTO
        ProjectEntity project = new ProjectEntity();

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());


        return convertEntityToDTO(projectDAO.save(project));
    }

    public ProjectDTO updateProject(ProjectDTO projectDTO, int id){
        ProjectEntity project = projectDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + id + " not found"));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());

        return convertEntityToDTO(projectDAO.save(project));
    }
    public void deleteProject(int id){
        ProjectEntity project = projectDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + id + " not found"));

        project.setIsActive(false);

        projectDAO.save(project);
    }

    public void assignProjectToEmployee(int idEmployee, int idProject){
        ProjectEntity project = projectDAO.findById(idProject)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + idProject + " not found"));
        EmployeeEntity employee = employeeDAO.findById(idEmployee)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID: " + idEmployee + " not found"));

        project.getEmployees().add(employee);
        employee.getProjects().add(project);

        employeeDAO.save(employee);
    }
}
