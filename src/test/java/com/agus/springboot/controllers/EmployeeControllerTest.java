package com.agus.springboot.controllers;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.service.EmployeeService;
import com.agus.springboot.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("GET api-rest/employees/{id} - Success")
    void getEmployeeById_ShouldReturnEmployee() throws Exception{
        int idEmployee = 1;
        EmployeesDTO employeesDTO = new EmployeesDTO();
        employeesDTO.setEmpno(idEmployee);

        when(employeeService.findEmployeeById(idEmployee)).thenReturn(employeesDTO);

        mockMvc.perform(get("/api-rest/employees/" + idEmployee))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 4. Server returns JSON
                .andExpect(jsonPath("$.empno").value(idEmployee)); // JSON ID = 1?


    }

    @Test
    @DisplayName("GET api-rest/employees/{id} - Fail")
    void getEmployeeById_ShouldReturn404_WhenEmployeeNotFound() throws Exception{
        int nonValidId = 100;

        when(employeeService.findEmployeeById(nonValidId)).
                thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api-rest/employees/" + nonValidId))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("POST api-rest/employees/")
    void postCreateEmployee_ShouldCreateEmployee() throws Exception{
        int idEmployee = 1;
        EmployeesDTO newEmployee = new EmployeesDTO();
        newEmployee.setEmpno(idEmployee);
        newEmployee.setName("Agus");
        newEmployee.setJob("DEV");
        newEmployee.setDeptNo(10);

        when(employeeService.saveEmployee(any(EmployeesDTO.class))).thenReturn(newEmployee);

        mockMvc.perform(post("/api-rest/employees")
                .contentType(MediaType.APPLICATION_JSON) // 1. We tell then tha I send a JSON
                .content(objectMapper.writeValueAsString(newEmployee))) // 2. Send real JSON
                .andDo(print())                                         // to debug
                .andExpect(status().isCreated()) // 3. Expect el 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 4. Server returns JSON
                .andExpect(jsonPath("$.empno").value(idEmployee)); // 5. Check has an ID
    }

    @Test
    @DisplayName("PUT api-rest/employees/{id} - Success")
    void putUpdateEmployee_ShouldUpdate() throws Exception{
        
    }


}
