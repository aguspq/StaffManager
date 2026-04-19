package com.agus.springboot.controllers;

import com.agus.springboot.service.EmployeeService;
import com.agus.springboot.dto.EmployeesDTO;
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
@RequestMapping("api-rest/employees")
@Tag(name = "Employee Management", description = "Operations for managing employees, their department assignments, and project involvements.")
public class EmployeeController {
    @Autowired
    private EmployeeService emplService;

    @GetMapping
    @Operation(summary = "List all employees", description = "Retrieves a paginated list of employees including their department and project details.")
    public ResponseEntity<Page<EmployeesDTO>> findAllEmployees(@PageableDefault(size = 5) Pageable pageable){ // @DeftaultSize... 5
        Page<EmployeesDTO> employeesDTOList = emplService.findAllEmployees(pageable);
        return ResponseEntity.ok(employeesDTOList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Fetches a single employee's data using their unique database ID.")
    public ResponseEntity<EmployeesDTO> findEmployeeById (@PathVariable(value = "id") int id){

        return ResponseEntity.ok(emplService.findEmployeeById(id));
    }


    @PostMapping
    @Operation(summary = "Create employee", description = "Registers a new employee. Validates required fields before persistence.")
    public ResponseEntity<EmployeesDTO> saveEmployee(@Valid @RequestBody EmployeesDTO employee){ // new employee

        return new ResponseEntity<>(emplService.saveEmployee(employee), HttpStatus.CREATED);

    }
    // ------------------------------
    @Operation(summary = "Update employee", description = "Updates personal or professional data of an existing employee.")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeesDTO> updateEmployee(@RequestBody EmployeesDTO newEmpl,
                                            @PathVariable(value = "id") int id){

        return ResponseEntity.ok(emplService.updateEmployee(id, newEmpl));
    }


    // ------------------------------
    @Operation(summary = "List unassigned employees", description = "Filters and returns employees who are not currently assigned to any project.")
    @GetMapping("/unassigned")
    public ResponseEntity<Page<EmployeesDTO>> getUnassigned(@PageableDefault(size = 5) Pageable pageable){
        return ResponseEntity.ok(emplService.findUnassignedEmployeesDTO(pageable));
    }

    @Operation(summary = "Soft delete employee", description = "Deactivates an employee account without removing the record from the database.")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") int id){

        emplService.deleteUser(id);

        return ResponseEntity.noContent().build(); // 204 is a standard code to deleted successfully
//                                                  same but without response. More "pro"

    }

    @Operation(summary = "Reassign department", description = "Moves an employee to a different department by updating the department ID.")
    @PatchMapping("/{id}/dept/{deptNo}")
    public ResponseEntity<EmployeesDTO> reassignDeptToEmpl(@PathVariable(value = "id") int id,
                                                @PathVariable(value = "deptNo") int deptno){

        return ResponseEntity.ok(emplService.reassignDeptToEmployee(id, deptno));

    }

    @Operation(summary = "Add employee to project", description = "Links an employee to a project. Ensures the relationship is persisted in the join table.")
    @PatchMapping("/{id}/project/{projId}")
    public ResponseEntity<Void> addToEmployeeToProject(@PathVariable(value = "id") int id,
                                                    @PathVariable(value = "projId")int projId){
        emplService.addToProject(id, projId);
        return ResponseEntity.noContent().build();
    }

}
