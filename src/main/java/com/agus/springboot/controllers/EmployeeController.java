package com.agus.springboot.controllers;

import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.agus.springboot.service.EmployeeService;
import com.agus.springboot.service.EmployeesDTO;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("api-rest/employees")
public class EmployeeController {
    @Autowired
    private IEmployeeDAO employeeDAO;
//    @Autowired
//    private IDeptDAO deptDAO;
    @Autowired
    private EmployeeService emplService;

    @GetMapping
    public ResponseEntity<List<EmployeesDTO>> findAllEmployees(){
        List<EmployeesDTO> employeesDTOList = emplService.findAllEmployees();
        return ResponseEntity.ok(employeesDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeEntity> findEmployeesById(@PathVariable(value = "id") int id){
        Optional<EmployeeEntity> employee = employeeDAO.findById(id);

        if(employee.isPresent()){
            return ResponseEntity.ok().body(employee.get());
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<EmployeesDTO> findEmployeeByIdDTO (@PathVariable(value = "id") int id){
        EmployeesDTO emplDTO = emplService.findEmployeeByIdDTO(id);

        if(emplDTO != null){
            return ResponseEntity.ok().body(emplDTO);
        } else
            return ResponseEntity.notFound().build();
    }

//    @PostMapping
//    public EmployeeEntity saveEmployee(@Validated @RequestBody EmployeeEntity employee){
//        return employeeDAO.save(employee);
//    }

    @PostMapping
    public ResponseEntity<?> saveEmployee(@Valid @RequestBody EmployeesDTO employee){ // new employee
        try{
            EmployeesDTO savedDTO = emplService.saveEmployee(employee);
            return ResponseEntity.ok(savedDTO);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    // ------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeesDTO newEmpl,
                                            @PathVariable(value = "id") int id){
        EmployeesDTO emplDTO = emplService.updateEmployee(id, newEmpl);
        if(emplDTO != null)
            return ResponseEntity.ok().body(emplDTO);

        return ResponseEntity.notFound().build();
    }


    // ------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") int id){

        boolean deleted = emplService.deleteUser(id);
        if (deleted){
            return ResponseEntity.ok().body("User deleted");
//            return ResponseEntity.noContent().build(); // El código 204 es el estándar para borrar con éxito
//            same but without response. More "pro"
        }

        return ResponseEntity.notFound().build();
    }

}
