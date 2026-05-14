package com.agus.springboot.model.entities;

import jakarta.persistence.*;
//import jdk.incubator.foreign.SymbolLookup;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "dept", schema = "public", catalog = "employeeAPC2425")
public class DeptEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "deptno")
    private Integer deptno;
    @Column(name = "dname")
    private String dname;
    @Column(name = "loc")
    private String loc;
    @Column(name = "isactive")
    private Boolean isActive = true;
    @OneToMany(mappedBy = "dept", cascade = CascadeType.ALL)
    private Set<EmployeeEntity> employees = new HashSet<>(); // Inicialize ALLWAYS!!!

    public DeptEntity(){

    }

    public DeptEntity(int deptno, String dname, String loc, boolean isActive){
        this.deptno = deptno;
        this.dname = dname;
        this.loc = loc;
        this.isActive = isActive;
        this.employees = new HashSet<>();
    }



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
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

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
