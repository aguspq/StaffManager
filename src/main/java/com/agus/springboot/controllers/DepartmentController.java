package com.agus.springboot.controllers;

import com.agus.springboot.dto.DepartmentDTO;
import com.agus.springboot.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import static sun.rmi.transport.TransportConstants.Return;

@RestController
@RequestMapping("api-rest/dept")
@Tag(name = "Department Management", description = "Administrative operations for company departments and locations.")
public class DepartmentController {
    @Autowired
    private DepartmentService deptService;

    @Operation(summary = "List all departments", description = "Retrieves a complete list of all company departments.")
    @GetMapping
    public List<DepartmentDTO> findAllDepts(){return deptService.findAllDepartments();}

    @Operation(summary = "Get department by ID", description = "Finds department details, including its location and name, by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> findDeptById(@PathVariable(value = "id") int id){
        DepartmentDTO dept = deptService.findDeptById(id);

        return ResponseEntity.ok().body(dept);
    }

    @Operation(summary = "Create department", description = "Creates a new organizational unit. Requires a valid department name.")
    @PostMapping // CREATE
    public ResponseEntity<DepartmentDTO> saveDept (@Valid @RequestBody DepartmentDTO dept){
        DepartmentDTO newDept = deptService.saveDept(dept);

        return new ResponseEntity<>(newDept, HttpStatus.CREATED);
    }

    @Operation(summary = "Update department", description = "Modifies existing department details such as name or location.")
    @PutMapping("/{id}") // we don't use @Valid to allow partial updates
    public ResponseEntity<DepartmentDTO> updateDepartment(@RequestBody DepartmentDTO newDept,
                                              @PathVariable(value = "id") int id){
        DepartmentDTO dept = deptService.updateDepartment(id, newDept);

        return ResponseEntity.ok().body(dept);
    }

    @Operation(summary = "Delete department", description = "Hard delete: Removes the department record from the database.")
    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteDept(@PathVariable(value = "id") int id){
        deptService.deleteDept(id);

        return ResponseEntity.noContent().build();

    }
}
