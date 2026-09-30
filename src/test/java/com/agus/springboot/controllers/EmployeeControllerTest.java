package com.agus.springboot.controllers;

import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.service.EmployeeService;
import com.agus.springboot.service.ProjectService;
import com.agus.springboot.util.JwtTokenValidator;
import com.agus.springboot.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @Test
    @DisplayName("GET api-rest/employees -- Success")
    void getAllEmployees_ShouldReturnPageEmployees () throws Exception {
        EmployeesDTO employee = new EmployeesDTO();
        employee.setEmpno(1);
        employee.setName("Agus");

        Page<EmployeesDTO> page = new PageImpl<>(List.of(employee));


        when(employeeService.findAllEmployees(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api-rest/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].empno").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Agus"));
    }

    @Test
    @DisplayName("GET api-rest/employees/unassigned - Success (Paginación)")
    void getUnassigned_ShouldReturnPagedUnassignedEmployees() throws Exception {
        EmployeesDTO employee = new EmployeesDTO();
        employee.setName("agus");
        employee.setEmpno(1);

        Page<EmployeesDTO> page = new PageImpl<>(List.of(employee));

        when(employeeService.findUnassignedEmployeesDTO(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api-rest/employees/unassigned"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].empno").value(1))
                .andExpect(jsonPath("$.content[0].name").value("agus"));
    }

    @Test
    @DisplayName("PATCH api-rest/employees/{id}/dept/{deptNo} - Success")
    void reassignDeptToEmpl_ShouldReturnUpdatedEmployee() throws Exception {
        int emplId = 1;
        int deptId = 10;

        EmployeesDTO employee = new EmployeesDTO();
        employee.setEmpno(emplId);
        employee.setDeptNo(deptId);

        when(employeeService.reassignDeptToEmployee(emplId, deptId)).thenReturn(employee);

        mockMvc.perform(patch("/api-rest/employees/" + emplId + "/dept/" + deptId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.empno").value(emplId))
                .andExpect(jsonPath("$.deptNo").value(deptId));
    }

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
        int idEmployee = 1;

        EmployeesDTO updatedEmployee = new EmployeesDTO();
        updatedEmployee.setEmpno(idEmployee);
        updatedEmployee.setName("Agus Updated");
        updatedEmployee.setJob("DEV");
        updatedEmployee.setDeptNo(10);

        when(employeeService.updateEmployee(eq(idEmployee), any(EmployeesDTO.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/api-rest/employees/" + idEmployee)
                .contentType(MediaType.APPLICATION_JSON) // 1. We tell then tha I send a JSON
                .content(objectMapper.writeValueAsString(updatedEmployee))) // 2. Send real JSON
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 4. Server returns JSON
                .andExpect(jsonPath("$.empno").value(idEmployee))
                .andExpect(jsonPath("$.name").value("Agus Updated"));
    }
    @Test
    @DisplayName("PUT api-rest/employees/{id} - FAIL")
    void putUpdateEmployee_ShouldFail() throws Exception{
        int idEmployee = 999;

        EmployeesDTO updatedEmployee = new EmployeesDTO();
        updatedEmployee.setName("Agus Updated");
        updatedEmployee.setJob("DEV");
        updatedEmployee.setDeptNo(10);

        when(employeeService.updateEmployee(eq(idEmployee), any(EmployeesDTO.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found"));


        mockMvc.perform(put("/api-rest/employees/" + idEmployee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("DELETE api-rest/employees/{id} -- SUCCESS")
    void deleteEmployee_ShouldDelete() throws Exception{
        int employeeId = 10;

        doNothing().when(employeeService).deleteUser(employeeId);

        mockMvc.perform(patch("/api-rest/employees/" + employeeId))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("DELETE api-rest/employees/{id} -- FAIL")
    void deleteEmployee_ShouldFail() throws Exception{
        int nonValidId = 999;

        doThrow(new ResourceNotFoundException("Employee not found"))
                .when(employeeService).deleteUser(nonValidId);

        mockMvc.perform(patch("/api-rest/employees/" + nonValidId))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PUT api-rest/employees/{id} - 400 Bad Request when payload is invalid")
    void putUpdateEmployee_ShouldReturn400_WhenInvalidBody() throws Exception {
        int idEmployee = 1;

        EmployeesDTO inputDto = new EmployeesDTO();

        mockMvc.perform(put("/api-rest/employees/" + idEmployee)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest());

    }



}