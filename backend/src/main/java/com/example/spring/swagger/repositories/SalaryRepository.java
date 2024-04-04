package com.example.spring.swagger.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.spring.swagger.models.Salary;

import org.springframework.data.repository.CrudRepository;

public interface SalaryRepository extends PagingAndSortingRepository<Salary, Long>, CrudRepository<Salary,Long> {
}
