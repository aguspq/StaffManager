package com.agus.springboot.model.dao;

import com.agus.springboot.model.entities.security.UserEntity;
import org.mapstruct.control.MappingControl;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserDAO extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String username);
}
