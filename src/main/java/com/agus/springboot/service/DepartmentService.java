package com.agus.springboot.service;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.entities.DeptEntity;
import com.agus.springboot.model.entities.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    @Autowired
    private IDeptDAO deptDAO;

    public List<DepartmentDTO> findAllDepartments(){
        List<DeptEntity> deptEntityList = (List<DeptEntity>)deptDAO.findAll();
        List<DepartmentDTO> dtoList = new ArrayList<>();

        for(DeptEntity dept : deptEntityList){
            dtoList.add(convertEntityToDTO(dept));
        }
        return dtoList;

    }

    private DepartmentDTO convertEntityToDTO (DeptEntity dept){
        DepartmentDTO dto = new DepartmentDTO();
        dto.setDeptNo(dept.getDeptno());
        dto.setName(dept.getDname());
        dto.setLocation(dept.getLoc());

        if(dept.getEmployees() != null){
//            Entity List
            List<EmployeeEntity> employees = new ArrayList<>(dept.getEmployees());
//            DTO List
            List<EmployeesDTO> employeesDTOList = new ArrayList<>();

            for (EmployeeEntity empl : employees){
                employeesDTOList.add(convertEmplEntityToDTO(empl));
            }

            dto.setEmployeesDTOList(employeesDTOList);
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
        Optional<DeptEntity> dept = deptDAO.findById(id);
//        return dept.map(d -> convertEntityToDTO(d)).orElse(null);
//        same but shorter
        return dept.map(this::convertEntityToDTO).orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
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
        Optional<DeptEntity> dept = deptDAO.findById(id);

        if(dept.isPresent()){
            deptDAO.deleteById(id);
        } else
            throw new ResourceNotFoundException("Department not found with ID: " + id);
    }

    public DepartmentDTO updateDepartment(int id, DepartmentDTO newDept){
        DeptEntity dept = deptDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dept with ID: " + id + " not found"));

        if(newDept.getName() != null)
            dept.setDname(newDept.getName());

        if(newDept.getLocation() != null)
            dept.setLoc(newDept.getLocation());

        DeptEntity deptUpdated = deptDAO.save(dept);

        return convertEntityToDTO(deptUpdated);
    }

}
