package com.agus.springboot.service;

import com.agus.springboot.model.entities.ProjectEntity;
import jakarta.validation.constraints.*;

import java.util.Set;

public class EmployeesDTO {
    private Integer empno;
    @NotBlank(message = "The field: 'ename' can't be blank")
    @Size(max = 50, message = "Name size must be under 50 characters")
    private String name;
    @NotBlank (message = "The field 'job' can't be blank")
    private String job;
    @NotNull
    @Min(1)
    private Integer deptNo;
    private String deptName;
    private String deptLocation;
    private Set<ProjectDTO> projectDTOSet;
    public EmployeesDTO(){

    }

    public EmployeesDTO(Integer empno, String name, String job,Integer deptNo, String deptName, String deptLocation, Set<ProjectDTO> projectEntitySet ){
        this.empno = empno;
        this.name = name;
        this.job = job;
        this.deptNo = deptNo;
        this.deptName = deptName;
        this.deptLocation = deptLocation;
        this.projectDTOSet = projectEntitySet;
    }

    public Integer getEmpno(){
        return empno;
    }
    public void setEmpno(Integer empno){
        this.empno = empno;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getJob() { return job; }
    public void setJob(String job){
        this.job = job;
    }
    public Integer getDeptNo() {return deptNo;}
    public void setDeptNo(Integer deptNo){
        this.deptNo = deptNo;
    }
    public String getDeptName(){ return deptName;}
    public void setDeptName(String deptName){
        this.deptName = deptName;
    }
    public String getDeptLocation(){return deptLocation;}
    public void setDeptLocation(String deptLocation){
        this.deptLocation = deptLocation;
    }

    public Set<ProjectDTO> getProjectDTOSet() {
        return projectDTOSet;
    }
    public void setProjectDTOSet(Set<ProjectDTO> projectDTOSet) { this.projectDTOSet = projectDTOSet; }
}
