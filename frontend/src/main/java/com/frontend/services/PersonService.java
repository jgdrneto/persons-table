package com.frontend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frontend.configs.BackendConfigs;
import com.frontend.models.Person;
import com.frontend.models.Role;
import com.frontend.utils.MyHttpClient;
import com.frontend.utils.Page;
import com.frontend.utils.PersonPageCallable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class PersonService{

    @Autowired
    private BackendConfigs backendConfig;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MyHttpClient httpClient;

    @Autowired
    private ExecutorService executor;

    public double calculeSalary(Person p){

        if(p.getObjRole() == null){
            return 0;
        }else{
            return p.getObjRole().obtainTotalSalary();
        }
        
    }

    public List<Person> findAll() throws IOException, InterruptedException, ExecutionException {
        
        Map<Long,Role> rolesMap = roleService.findAllWithMap();

        TypeReference<Page<Person>> personPageType = new TypeReference<Page<Person>>() {};
        
        List<Person> persons = new ArrayList<>();

        Page<Person> personsFirstPage = httpClient.get(backendConfig.obtainPersonUrl(0,backendConfig.getDefaultPageSize()), personPageType, false);
        
        for(Person r : personsFirstPage.getContent()){
            r.setObjRole(rolesMap.get(r.getRoleId()));
        }

        persons.addAll(personsFirstPage.getContent());

        List<PersonPageCallable> callables = new ArrayList<>();

        for(int page=1; page<personsFirstPage.getTotalPages();page++){
            callables.add(new PersonPageCallable("Page " + page, backendConfig.obtainPersonUrl(page,backendConfig.getDefaultPageSize()), personPageType, rolesMap, httpClient));
        }

        List<Future<List<Person>>> futures = executor.invokeAll(callables);

        for(Future<List<Person>> newPersonPage : futures){
            persons.addAll(newPersonPage.get());
        }

        return persons;
    }

    public List<Person> resetData() throws IOException, InterruptedException, ExecutionException{
        return this.findAll();
    }

    public Person findById(Long personId) throws IOException, InterruptedException {
        return this.httpClient.get(this.backendConfig.obtainPersonUrl()+"/"+personId, new TypeReference<Person>() {}, true);
    }

    public Person save(Person person) throws IOException, InterruptedException {
        
        TypeReference<Person> personType = new TypeReference<Person>() {};
        
        Person newPerson = null;

        if(person.getId() == 0){
            newPerson = this.httpClient.post(this.backendConfig.obtainPersonUrl(), person, personType, true);
        }else{

            newPerson = this.httpClient.put(this.backendConfig.obtainPersonUrl()+"/"+person.getId(), person, personType, true);
        }
        if(newPerson.getRoleId() != 0){
            newPerson.setObjRole(roleService.findById(newPerson.getRoleId()));
        }
        return newPerson;
    }

    public Person remove(Long personId) throws IOException, InterruptedException {
        TypeReference<Person> personType = new TypeReference<Person>() {};
        
        return this.httpClient.delete(this.backendConfig.obtainPersonUrl()+"/"+personId, personType, true);

    }
}
