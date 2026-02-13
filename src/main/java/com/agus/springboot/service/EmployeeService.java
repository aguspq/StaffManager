package com.agus.springboot.service;

import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private IEmployeeDAO employeeDAO;

    @Autowired
    private IDeptDAO deptDAO;

    public EmployeesDTO saveEmployee(EmployeesDTO dto) {
        // Mueve aquí la lógica de:
        // 1. Buscar departamento
        if (dto.getEmpno() != null && employeeDAO.existsById(dto.getEmpno())) {
            throw new RuntimeException("Employee already exists");
        }
        Optional<DeptEntity> dept = deptDAO.findById(dto.getDeptno());
        if (dept.isEmpty()) {
            throw new RuntimeException("Department does not exist");
        }
        // 2. Mapear la Entity
        EmployeeEntity emplEntity = new EmployeeEntity();
        emplEntity.setEmpno(dto.getEmpno());
        emplEntity.setEname(dto.getName());
        emplEntity.setJob(dto.getJob());
        // DEPT we use get() because is OPTIONAL
        emplEntity.setDept(dept.get());

        // 3. Guardar
        EmployeeEntity saved = employeeDAO.save(emplEntity);

        // 5. Devolver ese DTO
        return convertEntityToDTO(saved);
    }

    public EmployeesDTO findEmployeeByIdDTO(int id){
        Optional<EmployeeEntity> employee = employeeDAO.findById(id);

        if (employee.isEmpty()) {
            return null;
        }

        EmployeeEntity empl = employee.get();
        EmployeesDTO employeesDTO = new EmployeesDTO();

        employeesDTO.setEmpno(empl.getEmpno());
        employeesDTO.setName(empl.getEname());
        employeesDTO.setJob(empl.getJob());

        if(employee.get().getDept() != null){
            employeesDTO.setDeptNo(empl.getDept().getDeptno());
            employeesDTO.setDeptName(empl.getDept().getDname());
            employeesDTO.setDeptLocation(empl.getDept().getLoc());
        }
        return employeesDTO;
//        Thats the idea, but I need to do ==> DTO -> Entity -> DTO
//        EmployeesDTO empFound = employeeDAO.findById(id);
    }

    public List<EmployeesDTO> findAllEmployees() {
        // convert List<EmployeeEntity> ==> List<EmployeesDTO>
        List<EmployeeEntity> employeeEntityList = (List<EmployeeEntity>) employeeDAO.findAll();
        List<EmployeesDTO> listDTO = new ArrayList<>();

        for (EmployeeEntity empl : employeeEntityList) {
            listDTO.add(convertEntityToDTO(empl));
        }
        return listDTO;
    }

//    private List<EmployeesDTO> convertEntityToDTO(List<EmployeeEntity> employeeEntityList){
//
//    }
//    INSTEAD OF AND LIST WE CONVERT AN OBJECT
    private EmployeesDTO convertEntityToDTO(EmployeeEntity employeeEntity){
        return new EmployeesDTO(
                employeeEntity.getEmpno(),
                employeeEntity.getEname(),
                employeeEntity.getJob(),
                employeeEntity.getDept().getDeptno(),
                employeeEntity.getDept().getDname(),
                employeeEntity.getDept().getLoc()
        );
    }

    public boolean deleteUser(int id){
        boolean deleted = false; // not found

        boolean exists = employeeDAO.existsById(id);

        if(exists){
            employeeDAO.deleteById(id);
            deleted = true; // found
        }
        return deleted;
    }

    public EmployeesDTO updateEmployee(int id, EmployeesDTO dtoUpdated){
        Optional<EmployeeEntity> employeeEntityOptional = employeeDAO.findById(id);
        if(employeeEntityOptional.isPresent()){
            EmployeeEntity employee = employeeEntityOptional.get();
//            1- get EmployeeEntity 2- Save it 3- return DTO (?)
            employee.setEname(dtoUpdated.getName());
            employee.setJob(dtoUpdated.getJob());

            if(dtoUpdated.getDeptno() != null){
                Optional<DeptEntity> dept = deptDAO.findById(dtoUpdated.getDeptno());
                dept.ifPresent(employee::setDept);
//                dept.ifPresent(deptEntity -> employee.setDept(deptEntity));
            }
            EmployeeEntity savedEmployee = employeeDAO.save(employee);

            return convertEntityToDTO(savedEmployee);
        }

        return null;
    }
}
