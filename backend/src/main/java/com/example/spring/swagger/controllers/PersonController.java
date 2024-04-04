package com.example.spring.swagger.controllers;

import java.util.Optional;

import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.swagger.models.Person;
import com.example.spring.swagger.models.Role;
import com.example.spring.swagger.repositories.PersonRepository;
import com.example.spring.swagger.repositories.RoleRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Tag(name = "Person", description = "Person management APIs")
@RestController
@RequestMapping("/api/person")
public class PersonController {
	
	@Autowired
    private PersonRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Operation(summary = "Obtain data from Person by id")
	@GetMapping(value = "/{id}", produces="application/json")
    public ResponseEntity<Person> obtainById(@PathVariable(value = "id") @Min(1) long id) 
    {

		Optional<Person> optPerson = repository.findById(id);

		if(optPerson.isPresent()){
			return new ResponseEntity<Person>(optPerson.get(), HttpStatus.OK);
		}else{
			throw new EntityNotFoundException("Not found entity with id=" + id);
		}
    }

    @Operation(summary = "List all persons")
    @GetMapping(produces="application/json")
    @PageableAsQueryParam
    public Page<Person> listAll(@Parameter(hidden = true) @NotNull final Pageable pageable) 
    {

        return repository.findAll(pageable);

    }
     
    @Operation(summary = "Create a new Person")
    @PostMapping(produces="application/json", consumes = "application/json")
    public ResponseEntity<Person> create(@Valid @RequestBody Person person)
    {
		if(person.getId()==0){

            if(person.getRoleId()!=0){
                Optional<Role> optionalNewRole = roleRepository.findById(person.getRoleId());

                if(optionalNewRole.isPresent()){
                    person.setRole(optionalNewRole.get());
                }else{
                    throw new EntityNotFoundException("Not found role entity with id=" + person.getRoleId());
                }
            }
            
            return new ResponseEntity<Person>(repository.save(person), HttpStatus.CREATED);
        }else{
			throw new EntityExistsException("Person id is not valid");
		}
		
    }
	
	@Operation(summary = "Update a person")
    @PutMapping(value = "/{id}", produces="application/json", consumes = "application/json")
    public ResponseEntity<Person> update(@PathVariable(value = "id") @Min(1) long id, @Valid @RequestBody Person newPerson)
    {
        Optional<Person> oldPerson = repository.findById(id);

        if(oldPerson.isPresent()){
           
            newPerson.setId(id);
            Optional<Role> optionalNewRole = roleRepository.findById(newPerson.getRoleId());

            if(optionalNewRole.isPresent()){
                Role newRole = optionalNewRole.get();
                newPerson.setRole(newRole);
            }
            repository.save(newPerson);
            return new ResponseEntity<Person>(newPerson, HttpStatus.OK);

        }else{
            throw new EntityNotFoundException("Not found entity with id=" + id);
        }
    }

    @Operation(summary = "Delete a person")
    @DeleteMapping(value = "/{id}", produces="application/json")
    public ResponseEntity<Person> Delete(@PathVariable(value = "id") @Min(1) long id)
    {
        Optional<Person> person = repository.findById(id);
        if(person.isPresent()){
            repository.deleteById(id);
            return new ResponseEntity<Person>(person.get(),HttpStatus.OK);
        }else{
            throw new EntityNotFoundException("Not found entity with id=" + id);
        }
    }

    @Operation(summary = "Obtain person salary")
    @GetMapping(value = "/{id}/salary", produces="application/json")
    public ResponseEntity<Double> obtainSalary(@PathVariable(value = "id") @Min(1) long id)
    {
        Optional<Person> person = repository.findById(id);
        if(person.isPresent()){

            Person p = person.get();

            if(p.getRole() != null){
                return new ResponseEntity<Double>(p.getRole().getTotalSalary(),HttpStatus.OK);
            }else{
                return new ResponseEntity<Double>(0.0,HttpStatus.OK);
            }
        }else{
            throw new EntityNotFoundException("Not found Person entity with id=" + id);
        }
    }
}