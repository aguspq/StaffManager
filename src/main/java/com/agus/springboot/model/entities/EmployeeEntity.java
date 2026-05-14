package com.agus.springboot.model.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employee", schema = "public", catalog = "employeeAPC2425")
public class EmployeeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "empno")
    private Integer empno;
    @Basic
    @Column(name = "ename")
    private String ename;
    @Basic
    @Column(name = "job")
    private String job;

    @ManyToOne
    @JoinColumn(name = "deptno", referencedColumnName = "deptno")
    private DeptEntity dept;

    @Column(name = "isactive")
    private Boolean active = true;
    @ManyToMany
    @JoinTable(name = "employee_project", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
    private Set<ProjectEntity> projects = new HashSet<>();

    public EmployeeEntity(){

    }

    public EmployeeEntity(String ename, String job, DeptEntity dept) {
        this.ename = ename;
        this.job = job;
        this.dept = dept;
    }

    public Integer getEmpno() {
        return empno;
    }

    public void setEmpno(Integer empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public DeptEntity getDept() {
        return dept;
    }

    public void setDept(DeptEntity dept) {
        this.dept = dept;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    public void setProjects(Set<ProjectEntity> projects) { this.projects = projects; }
    public Set<ProjectEntity> getProjects(){ return projects; }

    public void addProject(ProjectEntity project) {
        this.projects.add(project);
        project.getEmployees().add(this);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeEntity that)) return false;
        return Objects.equals(getEmpno(), that.getEmpno()) && Objects.equals(getEname(), that.getEname()) && Objects.equals(getJob(), that.getJob());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmpno(), getEname(), getJob());
    }

}
