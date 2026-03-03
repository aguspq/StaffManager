package com.agus.springboot.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

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
    @JsonBackReference
    private DeptEntity dept;


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
