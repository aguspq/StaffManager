package com.agus.springboot.service;

import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.dao.IEmployeeDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private IEmployeeDAO employeeDAO;

    @Autowired
    private IDeptDAO deptDAO;

    public EmployeesDTO saveEmployee(EmployeesDTO dto) {
        // 1. Buscar departamento
        if(dto.getEmpno() != null)
//            throw new RuntimeException("You can't pass an ID to create");
            throw new ResourceNotFoundException("You can't pass an ID to create");

//         Fast Fail, to force Spring to throw MY exception
//        if(dto.getDeptno() == null)
//            throw new ResourceNotFoundException("Department ID is mandatory to create an employee");

        Optional<DeptEntity> dept = deptDAO.findById(dto.getDeptNo());
        if (dept.isEmpty()) {
//            throw new RuntimeException("Department does not exist");
            throw new ResourceNotFoundException("Department with ID: " + dto.getDeptNo() + " not found");
        }
        // 2. Mapear la Entity
        EmployeeEntity emplEntity = new EmployeeEntity();
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
        return employeeDAO.findById(id)
                .map(this::convertEntityToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

    }


    public Page<EmployeesDTO> findAllEmployees(Pageable pageable) {
//        // convert List<EmployeeEntity> ==> List<EmployeesDTO>
//        NO stream() with Pageable
        return employeeDAO.findByIsActiveTrue(pageable)
                .map(this::convertEntityToDTO);

    }

//    INSTEAD OF AND LIST WE CONVERT AN OBJECT
    private EmployeesDTO convertEntityToDTO(EmployeeEntity employeeEntity){
        EmployeesDTO dto = new EmployeesDTO();
        dto.setEmpno(employeeEntity.getEmpno());
        dto.setName(employeeEntity.getEname());
        dto.setJob(employeeEntity.getJob());

        if(employeeEntity.getDept() != null){
            dto.setDeptNo(employeeEntity.getDept().getDeptno());
            dto.setDeptName(employeeEntity.getDept().getDname());
            dto.setDeptLocation(employeeEntity.getDept().getLoc());
        }

        return dto;

    }

    public void deleteUser(int id){
        EmployeeEntity employee = employeeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID: " + id + " not found"));

        employee.setActive(false);

        employeeDAO.save(employee);
    }

    public EmployeesDTO updateEmployee(int id, EmployeesDTO dtoUpdated){
//        Optional<EmployeeEntity> employeeEntityOptional = employeeDAO.findById(id);
//        1- get EmployeeEntity 2- Save it 3- return DTO (?)
        EmployeeEntity employee = employeeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID: " + id + " not found"));

        employee.setEname(dtoUpdated.getName());
        employee.setJob(dtoUpdated.getJob());

        if(dtoUpdated.getDeptNo() != null){
            DeptEntity dept = deptDAO.findById(dtoUpdated.getDeptNo())
                    .orElseThrow(() -> new ResourceNotFoundException("Dept with ID: " + dtoUpdated.getDeptNo() + " not found"));

            employee.setDept(dept);
        }

        return convertEntityToDTO(employeeDAO.save(employee));
    }

    public Page<EmployeesDTO> findUnassignedEmployeesDTO(Pageable pageable){
        Page<EmployeeEntity> nullEmplList = employeeDAO.findByDeptIsNullAndIsActiveTrue(pageable); // <-- filter

        return nullEmplList
                .map(this::convertEntityToDTO);

    }

    public EmployeesDTO reassignDeptToEmployee(int emplId, int deptno){
        EmployeeEntity employee = employeeDAO.findById(emplId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id" + emplId + " not found"));

        if(deptno > 0){
            DeptEntity dept = deptDAO.findById(deptno)
                    .orElseThrow(() -> new ResourceNotFoundException("Dept with id" + deptno + " not found"));

            if(dept.getIsActive())
                employee.setDept(dept);
            else throw new ResourceNotFoundException("Dept must be active");

        } else if(deptno == 0){
            employee.setDept(null);
        } else
            throw new ResourceNotFoundException("Dept number must be greater than 0");

        return convertEntityToDTO(employeeDAO.save(employee));


    }
}
