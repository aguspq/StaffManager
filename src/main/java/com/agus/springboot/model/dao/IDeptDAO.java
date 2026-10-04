package com.agus.springboot.model.dao;

import com.agus.springboot.dto.DepartmentDTO;
import com.agus.springboot.model.entities.DeptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;

public interface IDeptDAO extends CrudRepository<DeptEntity, Integer> {
    Page<DeptEntity> findByIsActiveTrue(Pageable pageable);
}
