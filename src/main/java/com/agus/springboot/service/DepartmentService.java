package com.agus.springboot.service;

import com.agus.springboot.exceptions.ResourceNotFoundException;
import com.agus.springboot.model.dao.IDeptDAO;
import com.agus.springboot.model.entities.DeptEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return new DepartmentDTO(
                dept.getDeptno(),
                dept.getDname(),
                dept.getLoc()
        );
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

        if(dept.getName() == null || dept.getName().isEmpty() || dept.getLocation() == null || dept.getLocation().isEmpty())
            throw new ResourceNotFoundException("Complete all fields to create the department\n" +
                    "Name: " + dept.getName() +
                    "\nLocation: " + dept.getLocation());

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
