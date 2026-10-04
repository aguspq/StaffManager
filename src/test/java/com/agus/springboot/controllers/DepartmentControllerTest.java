package com.agus.springboot.controllers;

import com.agus.springboot.dto.DepartmentDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.service.DepartmentService;
import com.agus.springboot.util.JwtTokenValidator;
import com.agus.springboot.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DepartmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    // just in case
    @Autowired
    private ObjectMapper objectMapper; // translates object --> JSON


    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;


    @Test
    @DisplayName("findAllDepts")
    void findAllDepts_ShouldReturnListOfDepartments() throws Exception{
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setDeptNo(10);
        departmentDTO.setName("IT");

        Page<DepartmentDTO> page = new PageImpl<>(List.of(departmentDTO));

        when(departmentService.findAllDepartments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api-rest/dept"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].deptNo").value(10))
                .andExpect(jsonPath("$.content[0].name").value("IT"));

    }


    @Test
    @DisplayName("findDeptById")
    void findDeptById_ShouldReturnDepartment_WhenExists ()throws Exception{
        int deptId = 1;
        DepartmentDTO dept = new DepartmentDTO();
        dept.setName("Sales");
        dept.setDeptNo(deptId);

        when(departmentService.findDeptById(deptId)).thenReturn(dept);

        mockMvc.perform(get("/api-rest/dept/" + deptId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Sales"))
                .andExpect(jsonPath("$.deptNo").value(deptId));


    }

    @Test
    @DisplayName("FAIL - findDeptById -")
    void findDeptById_ShouldReturn404_WhenNotFound() throws Exception {
        int deptId = 999;

        when(departmentService.findDeptById(deptId)).thenThrow(new ResourceNotFoundException("Department not found with ID: " + deptId));

        mockMvc.perform(get("/api-rest/dept/" + deptId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Department not found with ID: " + deptId));


        verify(departmentService, times(1)).findDeptById(deptId);
    }

    @Test
    @DisplayName("SUCCESS - saveDept - ")
    void saveDept_ShouldReturn201Created() throws Exception {
        int deptId = 1;
        DepartmentDTO inputDto = new DepartmentDTO();
        inputDto.setName("IT");
        inputDto.setLocation("ALC");

        DepartmentDTO savedDto = new DepartmentDTO();
        savedDto.setDeptNo(deptId);
        savedDto.setName("IT");
        savedDto.setLocation("ALC");

        when(departmentService.saveDept(any(DepartmentDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api-rest/dept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deptNo").value(deptId))
                .andExpect(jsonPath("$.name").value("IT"))
                .andExpect(jsonPath("$.location").value("ALC"));


    }

    @Test
    @DisplayName("FAIL - saveDept - ")
    void saveDept_ShouldReturn400BadRequest_WhenInvalidBody() throws Exception{
        DepartmentDTO invalidDept = new DepartmentDTO();

        mockMvc.perform(post("/api-rest/dept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDept)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"));

        verify(departmentService, never()).saveDept(any());

    }

    @Test
    @DisplayName("SUCCESS - updateDepartment - Should return 200 OK when department is updated")
    void updateDepartment_ShouldReturn200() throws Exception{
        int deptId = 1;

        DepartmentDTO newDept = new DepartmentDTO();
        newDept.setName("IT");

        DepartmentDTO updatedDept = new DepartmentDTO();
        updatedDept.setDeptNo(deptId);
        updatedDept.setLocation("New loc");
        updatedDept.setName("IT");


        when(departmentService.updateDepartment(eq(deptId), any(DepartmentDTO.class))).thenReturn(updatedDept);

        mockMvc.perform(put("/api-rest/dept/" + deptId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDept)))
                .andDo(print()) // Imprime toda la petición y respuesta HTTP
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("IT"))
                .andExpect(jsonPath("$.deptNo").value(deptId))
                .andExpect(jsonPath("$.location").value("New loc"));



    }

    @Test
    @DisplayName("FAIL - updateDepartment - Should return 404 Not Found when ID does not exist")
    void updateDepartment_ShouldReturn404_WhenNotFound() throws Exception{
        int invalidId = 999;
        DepartmentDTO newDept = new DepartmentDTO();

        when(departmentService.updateDepartment(eq(invalidId), any(DepartmentDTO.class))).thenThrow(new ResourceNotFoundException("Department with ID: " + invalidId + " not found"));

        mockMvc.perform(put("/api-rest/dept/" + invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDept)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Department with ID: " + invalidId + " not found"));


    }

    @Test
    @DisplayName("SUCCESS - deleteDept - Should return 204 No Content when department is deleted")
    void deleteDept_ShouldReturn204NoContent() throws Exception{
        int deptId = 1;

        doNothing().when(departmentService).deleteDept(deptId);

        mockMvc.perform(delete("/api-rest/dept/" + deptId))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDept(deptId);
    }

    @Test
    @DisplayName("FAIL - deleteDept - Should return 404 Not Found when ID does not exist")
    void deleteDept_ShouldReturn404_WhenNotFound() throws Exception{
        int invalidId = 999;

        doThrow(new ResourceNotFoundException("Department with ID: " + invalidId + " not found"))
                .when(departmentService).deleteDept(invalidId);

        mockMvc.perform(delete("/api-rest/dept/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Department with ID: " + invalidId + " not found"));

        verify(departmentService, times(1)).deleteDept(invalidId);
    }

}
