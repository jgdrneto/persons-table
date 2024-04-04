package com.frontend.controllers;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.frontend.models.Person;
import com.frontend.models.Role;
import com.frontend.services.PersonService;
import com.frontend.services.RoleService;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
@Setter
@ViewScoped
@Controller
@RequestMapping("/")
public class IndexController implements Serializable{

    private static final long serialVersionUID = 1L;

    @Autowired
    PersonService personService;
    
    @Autowired
    RoleService roleService;

    private List<Person> persons;

    private List<Role> roles;

    private Person selectedPerson;
    
    public double calculeTotalSalary(Person p){
        return this.personService.calculeSalary(p);
    }

    private List<Person> loadDataFromDatabase(){

        List<Person> persons = new ArrayList<>();

        try {
            persons = personService.findAll();
            log.info("Loaded " + persons.size() + " person(s) record(s)");
        } catch (IOException | InterruptedException | ExecutionException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Pessoa","Error ao obter os dados do banco"));
        }

        return persons;
    }
    
    @PostConstruct
    public void init() {
        try {
            this.roles = roleService.findAll();
        } catch (IOException | InterruptedException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Cargo","Error ao obter os dados do banco"));
        }

        this.persons = Collections.synchronizedList(new ArrayList<>());
        this.persons.addAll(this.loadDataFromDatabase());
        this.selectedPerson = new Person();
    }

    public void reset(){
        this.persons.clear();
        this.persons.addAll(loadDataFromDatabase());
    }

    public void createEmptyPerson(){
        this.selectedPerson = new Person();
    }

    public void savePerson(){
        
        if(this.selectedPerson.getId() == 0){
            //Create
            log.info("Create Person: " + this.selectedPerson);

            try {
                this.selectedPerson = this.personService.save(this.selectedPerson);
                this.persons.add(this.selectedPerson);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pessoa","Registro criado"));

                // Oculta dialog
                PrimeFaces.current().executeScript("PF('personDialog').hide()");
                // Update table
                PrimeFaces.current().ajax().update("form-persons:menssages", "form-persons:persons-table");
                // Reset
                this.selectedPerson = null;

            } catch (IOException | InterruptedException e) {
                log.error("Error to create person", e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pessoa","Erro ao criar o registro"));
                PrimeFaces.current().ajax().update("form-persons:menssages");
            }
        }
        else {
            //Update
            log.info("Update Person: " + this.selectedPerson);
            try {
                
                this.selectedPerson.setObjRole(null);

                Person updatedPerson = this.personService.save(this.selectedPerson);

                this.selectedPerson.setObjRole(updatedPerson.getObjRole());

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pessoa","Registro Atualizado"));

                // Hide dialog
                PrimeFaces.current().executeScript("PF('personDialog').hide()");
                // Update table
                PrimeFaces.current().ajax().update("form-persons:menssages", "form-persons:persons-table");
                // Reset
                this.selectedPerson = null;

            } catch (IOException | InterruptedException e) {
                log.error("Error to update person", e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pessoa","Erro ao atualizar o registro"));
                PrimeFaces.current().ajax().update("form-persons:menssages");
            }
            
        }
        
    }

    public void removePerson(){
        log.info("Remove Person: " + this.selectedPerson);
        try {
            this.personService.remove(this.selectedPerson.getId());

            // Eliminar el registro de la lista de cuentas
            this.persons.remove(this.selectedPerson);
            // Reset del objeto seleccionado de la tabla
            this.selectedPerson = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pessoa","Registro removido"));
            //Update table
            PrimeFaces.current().ajax().update("form-persons:menssages", "form-persons:persons-table");

        } catch (IOException | InterruptedException e) {
            log.error("Error to remove person", e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pessoa","Erro ao remover o registro"));
            PrimeFaces.current().ajax().update("form-persons:menssages");
        }
        
    }
}
