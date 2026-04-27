package com.agus.springboot.controllers;

import com.agus.springboot.dto.ProjectDTO;
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

    @Operation(summary = "Get project by Id", description = "Fetches a single project's data using their unique database ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findProjectById(@PathVariable(value = "id") int id){
        return ResponseEntity.ok(projectService.findProjectById(id));
    }

    @Operation(summary = "List all projects", description = "Retrieves a paginated list of  projects. Default size is 5")
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

    @Operation(summary = "Update project", description = "Updates a project by its ID.")
    @PutMapping("/{id}") // UPDATE
    public ResponseEntity<ProjectDTO> updateProject(@RequestBody ProjectDTO project,
                                           @PathVariable(value = "id") int id){
        return ResponseEntity.ok(projectService.updateProject(project, id));
    }

    @Operation(summary = "Delete project", description = "Soft deletes a project by setting its isActive field to false.")
    @PatchMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable(value = "id") int id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign project to employee",
            description = "Establishes a bidirectional relationship between an employee and a project. " +
            "It updates the join table 'employee_project' ensuring data consistency.")
    @PatchMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<?> assignProjectToEMployee(@PathVariable(value = "employeeId") int emplId,
                                                     @PathVariable(value = "projectId") int projId){
        projectService.assignProjectToEmployee(emplId, projId);
        return ResponseEntity.noContent().build();
    }
}
