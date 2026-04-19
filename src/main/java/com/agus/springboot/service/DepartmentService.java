package com.agus.springboot.service;

import com.agus.springboot.dto.DepartmentDTO;
import com.agus.springboot.dto.EmployeesDTO;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private IDeptDAO deptDAO;

    public List<DepartmentDTO> findAllDepartments(){
        List<DeptEntity> deptEntityList = (List<DeptEntity>)deptDAO.findAll();
        return deptEntityList.stream()
                .filter(DeptEntity::getIsActive)
                .map(this::convertEntityToDTO)
                .toList();

    }

    private DepartmentDTO convertEntityToDTO (DeptEntity dept){
        DepartmentDTO dto = new DepartmentDTO();
        dto.setDeptNo(dept.getDeptno());
        dto.setName(dept.getDname());
        dto.setLocation(dept.getLoc());

        if(dept.getEmployees() != null){
////            Entity List
            List<EmployeeEntity> employees = new ArrayList<>(dept.getEmployees());

            dto.setEmployeesDTOList(employees.stream()
                    .map(this::convertEmplEntityToDTO)
                    .toList());
        }

        return dto;
    }

    private EmployeesDTO convertEmplEntityToDTO(EmployeeEntity employeeEntity){
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

    public DepartmentDTO findDeptById(int id){
        return deptDAO.findById(id)
                .filter(DeptEntity::getIsActive)
                .map(this::convertEntityToDTO)
                .orElseThrow(()-> new ResourceNotFoundException("Department not found or is inactive with ID: " + id));
    }

    public DepartmentDTO saveDept(DepartmentDTO dept){
        // DTO --> Entity --> Return dto
        if(dept.getDeptNo() != null)
            throw new ResourceNotFoundException("You can't pass an ID to create");

        DeptEntity deptEntity = new DeptEntity();
        deptEntity.setDname(dept.getName());
        deptEntity.setLoc(dept.getLocation());

        DeptEntity saved = deptDAO.save(deptEntity);

        return convertEntityToDTO(saved);
    }

    public void deleteDept(int id){
        DeptEntity dept = deptDAO.findById(id)
                .filter(DeptEntity::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Dept with ID: " + id + " not found"));

        dept.setIsActive(false);

//        List of employees
        for (EmployeeEntity empl : dept.getEmployees()){
            empl.setDept(null);
        }

        deptDAO.save(dept);

    }

    public DepartmentDTO updateDepartment(int id, DepartmentDTO newDept){
        DeptEntity dept = deptDAO.findById(id)
                .filter(DeptEntity::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Dept with ID: " + id + " not found"));

        if(newDept.getName() != null)
            dept.setDname(newDept.getName());

        if(newDept.getLocation() != null)
            dept.setLoc(newDept.getLocation());

        DeptEntity deptUpdated = deptDAO.save(dept);

        return convertEntityToDTO(deptUpdated);
    }

}
