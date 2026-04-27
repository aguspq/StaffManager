package com.agus.springboot.controllers;

import com.agus.springboot.dto.ProjectDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class) // test without DB, security...
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc; // fake client

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper; // translates object --> JSON

    @Test
    @DisplayName("GET /api-rest/projects/{id} - Success")
    void getProjectById_ShouldReturnProject() throws Exception {
//        1- ARRANGE
        int idProj = 1;
        ProjectDTO projectDTO = new ProjectDTO("Ex Proj", "Project desc");
        projectDTO.setId(idProj);
//        Mock service
        when(projectService.findProjectById(idProj)).thenReturn(projectDTO);

//        2-3 ACT + ASSERT
        mockMvc.perform(get("/api-rest/projects/" + idProj))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // it is a JSON?
                .andExpect(jsonPath("$.id").value(idProj)) // JSON ID = 1?
                .andExpect(jsonPath("$.name").value("Ex Proj")) // correct name?
                .andExpect(jsonPath("$.description").value("Project desc")); // correct description?
    }

    @Test
    @DisplayName("PATCH /api-rest/projects/{id} - Not Found")
    void deleteProject_ShouldReturn404_WhenProjectNotFound() throws Exception {
        int nonValidId  = 99;
        ProjectDTO projectDTO = new ProjectDTO("Ex Proj", "Project desc");
        projectDTO.setId(nonValidId);

        doThrow(new ResourceNotFoundException("Not found"))
                .when(projectService).deleteProject(nonValidId);

        mockMvc.perform(patch("/api-rest/projects/" + nonValidId))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("POST /api-rest/projects - Success")
    void saveProject_ShouldSaveProject() throws Exception{
        int idProj = 1;
        ProjectDTO projectDTO = new ProjectDTO("Ex Proj", "Project desc");
        projectDTO.setId(idProj);

        when(projectService.saveProject(any(ProjectDTO.class))).thenReturn(projectDTO);

        mockMvc.perform(post("/api-rest/projects")
                .contentType(MediaType.APPLICATION_JSON) // 1. We tell then tha I send a JSON
                .content(objectMapper.writeValueAsString(projectDTO))) // 2. Send real JSON
                .andExpect(status().isCreated()) // 3. Expect el 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 4. Server returns JSON
                .andExpect(jsonPath("$.id").value(idProj)); // 5. Check has an ID
    }

    @Test
    @DisplayName("PUT /api-rest/projects/{id} - Success")
    void updateProject_ShouldReturnUpdatedProject() throws Exception{
        int idProj = 1;
        ProjectDTO oldProject = new ProjectDTO("Ex Proj", "Project desc");
        oldProject.setId(idProj);

        ProjectDTO updatedProject = new ProjectDTO();
        updatedProject.setId(idProj);
        updatedProject.setName("New name");
        updatedProject.setDescription("New description");

        when(projectService.updateProject(any(), eq(idProj))).thenReturn(updatedProject);

        mockMvc.perform(put("/api-rest/projects/" + idProj)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject))) // 1rst parentheses

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 4. Server returns JSON
                .andExpect(jsonPath("$.id").value(idProj)) // JSON ID = 1?
                .andExpect(jsonPath("$.name").value("New name")) // correct name?
                .andExpect(jsonPath("$.description").value("New description"));

    }

}
