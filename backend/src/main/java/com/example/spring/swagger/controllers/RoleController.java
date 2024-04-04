package com.example.spring.swagger.controllers;

import java.util.ArrayList;
import java.util.List;
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

import com.example.spring.swagger.models.Role;
import com.example.spring.swagger.models.Salary;
import com.example.spring.swagger.repositories.RoleRepository;
import com.example.spring.swagger.repositories.SalaryRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Tag(name = "Role", description = "Role management APIs")
@RestController
@RequestMapping("/api/role")
public class RoleController {
	
	@Autowired
    private RoleRepository repository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Operation(summary = "Obtain data from Role by id")
	@GetMapping(value = "/{id}", produces="application/json")
    public ResponseEntity<Role> obtainById(@PathVariable(value = "id") @Min(1) long id) 
    {

		Optional<Role> optEntity = repository.findById(id);

		if(optEntity.isPresent()){
			return new ResponseEntity<Role>(optEntity.get(), HttpStatus.OK);
		}else{
			throw new EntityNotFoundException("Not found entity with id=" + id);
		}
    }

    @Operation(summary = "List all roles")
    @GetMapping(produces="application/json")
    @PageableAsQueryParam
    public Page<Role> listAll(@Parameter(hidden = true) @NotNull final Pageable pageable) 
    {

        return repository.findAll(pageable);
    }

    @Operation(summary = "Create a new Role")
    @PostMapping(produces="application/json", consumes = "application/json")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role)
    {
		if(role.getId()==0){

            List<Salary> newSalaries = new ArrayList<>();
            for(long salaryId : role.getSalaryIds()){
                Optional<Salary> newSalary = salaryRepository.findById(salaryId);

                if(newSalary.isPresent()){
                    newSalaries.add(newSalary.get());
                }else{
                    throw new EntityNotFoundException("Not found Salary entity with id=" + salaryId);
                }
            }
            role.setSalaries(newSalaries);

            return new ResponseEntity<Role>(repository.save(role), HttpStatus.CREATED);
        }else{
			throw new EntityExistsException("Role id is not valid");
		}
		
    }
	
	@Operation(summary = "Update a role")
    @PutMapping(value = "/{id}", produces="application/json", consumes = "application/json")
    public ResponseEntity<Role> update(@PathVariable(value = "id") @Min(1) long id, @Valid @RequestBody Role newRole)
    {
        Optional<Role> oldRole = repository.findById(id);

        if(oldRole.isPresent()){
            newRole.setId(id);
            List<Salary> newSalaries = new ArrayList<>();
            for(long salaryId : newRole.getSalaryIds()){
                Optional<Salary> newSalary = salaryRepository.findById(salaryId);

                if(newSalary.isPresent()){
                    newSalaries.add(newSalary.get());
                }else{
                    throw new EntityNotFoundException("Not found Salary entity with id=" + salaryId);
                }
            }
            newRole.setSalaries(newSalaries);
            repository.save(newRole);
            return new ResponseEntity<Role>(newRole, HttpStatus.OK);
        }else{
            throw new EntityNotFoundException("Not found entity with id=" + id);
        }
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping(value = "/{id}", produces="application/json")
    public ResponseEntity<Role> Delete(@PathVariable(value = "id") @Min(1) long id)
    {
        Optional<Role> role = repository.findById(id);
        if(role.isPresent()){
            repository.deleteById(id);
            return new ResponseEntity<Role>(role.get(),HttpStatus.OK);
        }else{
            throw new EntityNotFoundException("Not found entity with id=" + id);
        }
    }

    @Operation(summary = "Obtain salary from role")
    @GetMapping(value = "/{id}/salary", produces="application/json")
    public ResponseEntity<Double> obtainSalary(@PathVariable(value = "id") @Min(1) long id)
    {
        Optional<Role> role = repository.findById(id);
        if(role.isPresent()){
            return new ResponseEntity<Double>(role.get().getTotalSalary(),HttpStatus.OK);
        }else{
            throw new EntityNotFoundException("Not found Role entity with id=" + id);
        }
    }
}