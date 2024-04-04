package com.example.spring.swagger.controllers;

import java.util.Optional;

import org.hibernate.PersistentObjectException;
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

import com.example.spring.swagger.models.Salary;
import com.example.spring.swagger.repositories.SalaryRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Tag(name = "Salary", description = "Salary management APIs")
@RestController
@RequestMapping("/api/salary")
public class SalaryController {
	
	@Autowired
    private SalaryRepository repository;

    @Operation(summary = "Obtain data from Salary by id")
	@GetMapping(value = "/{id}", produces="application/json")
    public ResponseEntity<Salary> obtainById(@PathVariable(value = "id") @Min(1) long id) 
    {

		Optional<Salary> optEntity = repository.findById(id);

		if(optEntity.isPresent()){
			return new ResponseEntity<Salary>(optEntity.get(), HttpStatus.OK);
		}else{
			throw new EntityNotFoundException("Not found entity with id=" + id);
		}
    }

    @SuppressWarnings("null")
    @Operation(summary = "List all salaries")
    @GetMapping(produces="application/json")
    @PageableAsQueryParam
    public Page<Salary> listAll(@Parameter(hidden = true) @NotNull final Pageable pageable) 
    {
        return this.repository.findAll(pageable);
    }

    @Operation(summary = "Create a new Salary")
    @PostMapping(produces="application/json", consumes = "application/json")
    public ResponseEntity<Salary> create(@Valid @RequestBody Salary salary)
    {
		if(salary.getId()==0){
            try{
			    return new ResponseEntity<Salary>(repository.save(salary), HttpStatus.CREATED);
            }catch(Exception ex){
                throw new PersistentObjectException("Error to create salary with id=" + salary.getId());
            }
        }else{
			throw new EntityExistsException("Salary id is not valid");
		}
		
    }
	
	@Operation(summary = "Update a salary")
    @PutMapping(value = "/{id}", produces="application/json", consumes = "application/json")
    public ResponseEntity<Salary> update(@PathVariable(value = "id") @Min(1) long id, @Valid @RequestBody Salary newSalary)
    {
        Optional<Salary> oldSalary = repository.findById(id);

        if(oldSalary.isPresent()){
            try{
                newSalary.setId(id);
                repository.save(newSalary);
                return new ResponseEntity<Salary>(newSalary, HttpStatus.OK);
            }catch(Exception ex){
                throw new PersistentObjectException("Error to update salary with id=" + id);
            }
        }else{
            throw new EntityNotFoundException("Not found salary entity with id=" + id);
        }
    }

    @Operation(summary = "Delete a salary")
    @DeleteMapping(value = "/{id}", produces="application/json")
    public ResponseEntity<Salary> Delete(@PathVariable(value = "id") @Min(1) long id)
    {
        Optional<Salary> salary = repository.findById(id);
        if(salary.isPresent()){
            repository.deleteById(id);
            return new ResponseEntity<Salary>(salary.get(),HttpStatus.OK);
        }else{
            throw new EntityNotFoundException("Not found entity with id=" + id);
        }
    }
}