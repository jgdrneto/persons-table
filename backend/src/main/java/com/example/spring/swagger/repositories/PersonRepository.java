package com.example.spring.swagger.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.spring.swagger.models.Person;

import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, CrudRepository<Person,Long> {
}
