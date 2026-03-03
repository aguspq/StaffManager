package com.agus.springboot.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "dept", schema = "public", catalog = "employeeAPC2425")
public class DeptEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "deptno")
    private Integer deptno;
    @Basic
    @Column(name = "dname")
    private String dname;
    @Basic
    @Column(name = "loc")
    private String loc;

    @OneToMany(mappedBy = "dept")
    @JsonManagedReference
    private Set<EmployeeEntity> employees;


    public Integer getDeptno() {
        return deptno;
    }

    public void setDeptno(int deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
    public Set<EmployeeEntity> getEmployees() { return employees; }

    public void setEmployees (Set<EmployeeEntity> employees) { this.employees = employees; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeptEntity that)) return false;
        return Objects.equals(getDeptno(), that.getDeptno()) && Objects.equals(getDname(), that.getDname()) && Objects.equals(getLoc(), that.getLoc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeptno(), getDname(), getLoc());
    }
}
