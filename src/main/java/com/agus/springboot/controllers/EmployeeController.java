package com.agus.springboot.controllers;

import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.service.EmployeeService;
import com.agus.springboot.service.EmployeesDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api-rest/employees")
public class EmployeeController {
    @Autowired
    private IEmployeeDAO employeeDAO;
    @Autowired
    private EmployeeService emplService;

    @GetMapping
    public ResponseEntity<Page<EmployeesDTO>> findAllEmployees(@PageableDefault(size = 5) Pageable pageable){ // @DeftaultSize... 5
        Page<EmployeesDTO> employeesDTOList = emplService.findAllEmployees(pageable);
        return ResponseEntity.ok(employeesDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeesDTO> findEmployeeByIdDTO (@PathVariable(value = "id") int id){

        return ResponseEntity.ok(emplService.findEmployeeByIdDTO(id));
    }


    @PostMapping
    public ResponseEntity<EmployeesDTO> saveEmployee(@Valid @RequestBody EmployeesDTO employee){ // new employee

        return ResponseEntity.ok(emplService.saveEmployee(employee));

    }
    // ------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<EmployeesDTO> updateEmployee(@RequestBody EmployeesDTO newEmpl,
                                            @PathVariable(value = "id") int id){

        return ResponseEntity.ok(emplService.updateEmployee(id, newEmpl));
    }


    // ------------------------------
    @GetMapping("/unassigned")
    public ResponseEntity<Page<EmployeesDTO>> getUnassigned(@PageableDefault(size = 5) Pageable pageable){
        return ResponseEntity.ok(emplService.findUnassignedEmployeesDTO(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") int id){

        emplService.deleteUser(id);

        return ResponseEntity.noContent().build(); // 204 is a standard code to deleted successfully
//                                                  same but without response. More "pro"

    }

    @PatchMapping("/{id}/dept/{deptNo}")
    public ResponseEntity<EmployeesDTO> reassignDeptToEmpl(@PathVariable(value = "id") int id,
                                                @PathVariable(value = "deptNo") int deptno){

        return ResponseEntity.ok(emplService.reassignDeptToEmployee(id, deptno));

    }

}
