package com.agus.springboot.controllers;

import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.service.DepartmentDTO;
import com.agus.springboot.service.DepartmentService;
import com.agus.springboot.service.EmployeesDTO;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import static sun.rmi.transport.TransportConstants.Return;

@RestController
@RequestMapping("api-rest/dept")
public class DepartmentController {
    @Autowired
    private DepartmentService deptService;

    @GetMapping
    public List<DepartmentDTO> findAllDepts(){return deptService.findAllDepartments();}

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> findDeptById(@PathVariable(value = "id") int id){
        DepartmentDTO dept = deptService.findDeptById(id);

        return ResponseEntity.ok().body(dept);
    }

    @PostMapping // CREATE
    public ResponseEntity<?> saveDept (@Validated @RequestBody DepartmentDTO dept){
        DepartmentDTO newDept = deptService.saveDept(dept);

        return ResponseEntity.ok().body(newDept);
    }

//    public void deleteDept (@Validated @RequestBody DeptEntity dept){ deptDAO.delete(dept);}

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@RequestBody DepartmentDTO newDept,
                                              @PathVariable(value = "id") int id){
        DepartmentDTO dept = deptService.updateDepartment(id, newDept);

        return ResponseEntity.ok().body(dept);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteDept(@PathVariable(value = "id") int id){
        deptService.deleteDept(id);

        return ResponseEntity.noContent().build();

    }
}
