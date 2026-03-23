package com.agus.springboot.controllers;

import com.agus.springboot.model.dao.IProjectDAO;
import com.agus.springboot.model.entities.ProjectEntity;
import com.agus.springboot.service.ProjectDTO;
import com.agus.springboot.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api-rest/projects")
@Tag(name = "Project Management", description = "Operations for creating, editing, and deleting projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping // READ
    public ResponseEntity<Page<ProjectDTO>> findAllProjects(@PageableDefault(size = 5) Pageable pageable){
        return ResponseEntity.ok(projectService.findAllProjects(pageable));
    }

    @PostMapping // CREATE
    @Operation(summary = "Create new project", description = "Saves a project to the database and assigns it a unique ID.")
    public ResponseEntity<ProjectDTO> saveProject(@Valid @RequestBody ProjectDTO project){
//        return ResponseEntity.ok(projectService.saveProject(project));
        return new ResponseEntity<>(projectService.saveProject(project), HttpStatus.CREATED);

    }

    @PutMapping("/{id}") // UPDATE
    public ResponseEntity<ProjectDTO> updateProject(@RequestBody ProjectDTO project,
                                           @PathVariable(value = "id") int id){
        return ResponseEntity.ok(projectService.updateProject(project, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable(value = "id") int id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
