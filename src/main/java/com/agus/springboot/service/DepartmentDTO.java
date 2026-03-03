package com.agus.springboot.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class DepartmentDTO {
    private Integer deptNo;
//    check @Valid (in controller) to use annotations
    @NotBlank(message = "The field 'name' is mandatory")
    @Size(max = 50, message = "Name size must be under 50 characters")
    private String name;
    @NotBlank(message = "The field 'location' is mandatory")
    private String location;
    private List<EmployeesDTO> employeesDTOList;

    public DepartmentDTO(){

    }
    public DepartmentDTO(Integer deptNo, String name, String location, List<EmployeesDTO> employeesDTOList){
        this.deptNo = deptNo;
        this.name = name;
        this.location = location;
        this.employeesDTOList = employeesDTOList;
    }

    public Integer getDeptNo() {
        return deptNo;
    }
    public void setDeptNo(Integer deptNo) {
        this.deptNo = deptNo;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public List<EmployeesDTO> getEmployeesDTOList() { return employeesDTOList; }
    public void setEmployeesDTOList (List<EmployeesDTO> employeesDTOList) { this.employeesDTOList = employeesDTOList; }
}
