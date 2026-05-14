package com.agus.springboot.model.dao;

import com.agus.springboot.model.entities.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmployeeDAO extends JpaRepository<EmployeeEntity, Integer> {
    Page<EmployeeEntity> findByProjectsIsEmptyAndActiveTrue(Pageable pageable);

    Page<EmployeeEntity> findByActiveTrue(Pageable pageable);
}