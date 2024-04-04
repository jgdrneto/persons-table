package com.example.spring.swagger.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.spring.swagger.models.Role;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, CrudRepository<Role,Long> {
}
