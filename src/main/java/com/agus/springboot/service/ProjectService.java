package com.agus.springboot.service;

import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IProjectDAO;
import com.agus.springboot.model.entities.ProjectEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private IProjectDAO projectDAO;

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
}
