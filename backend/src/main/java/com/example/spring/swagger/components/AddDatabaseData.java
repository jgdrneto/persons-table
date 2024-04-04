package com.example.spring.swagger.components;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.spring.swagger.dto.PersonWithRoleId;
import com.example.spring.swagger.dto.RoleSalary;
import com.example.spring.swagger.models.Person;
import com.example.spring.swagger.models.Role;
import com.example.spring.swagger.models.Salary;
import com.example.spring.swagger.repositories.PersonRepository;
import com.example.spring.swagger.repositories.RoleRepository;
import com.example.spring.swagger.repositories.SalaryRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

@Component
public class AddDatabaseData {

    @Value("salaryData.csv")
    private ClassPathResource salaryDataResource;

    @Value("roleData.csv")
    private ClassPathResource roleDataResource;

    @Value("rolesSalaries.csv")
    private ClassPathResource rolesSalariesDataResource;

    @Value("personData.csv")
    private ClassPathResource personDataResource;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    private <T> List<T> loadEntityFromCsv(Class<T> entityClass, ClassPathResource resource) throws IOException{

        return new CsvToBeanBuilder<T>(new InputStreamReader(resource.getInputStream()))
        .withType(entityClass)
            .build()
            .parse();
    }

    private Map<Long, List<Long>> creteMapRoleSalary(List<RoleSalary> rolesSalaries) {
        Map<Long,List<Long>> rolesSalariesMap = new HashMap<>();

        for(RoleSalary roleSalary : rolesSalaries){
            if(!rolesSalariesMap.containsKey(roleSalary.getRoleId())){
                rolesSalariesMap.put(roleSalary.getRoleId(), new ArrayList<>());
            }

            rolesSalariesMap.get(roleSalary.getRoleId()).add(roleSalary.getSalaryId());
        }

        return rolesSalariesMap;
    }


    private Map<Long,Role> updateRolesWithSalaries(Map<Long,Role> roles, Map<Long,Salary> salaries, Map<Long, List<Long>> mapRoleIdSalary, RoleRepository repoRepository) {
        
        Map<Long,Role> mapRoles= new HashMap<>();
        
        for(Entry<Long,Role> entryRole : roles.entrySet()){
            Role role = entryRole.getValue();
            List<Long> salaryIds = mapRoleIdSalary.get(role.getId());

            for(Long salaryId : salaryIds){
                if(role.getSalaries() == null){
                    role.setSalaries(new ArrayList<>());
                }
                role.getSalaries().add(salaries.get(salaryId));
            }
            
            mapRoles.put(role.getId(),roleRepository.save(role));

        }

        return mapRoles;
    }
    
    private List<Person> createPersons(List<PersonWithRoleId> personsWithRoleId, Map<Long,Role> roles, DateTimeFormatter dateTimeFormatter) {
        List<Person> persons = new ArrayList<>();

        for(PersonWithRoleId personDTO : personsWithRoleId){

            persons.add(new Person(personDTO.getId(), 
                personDTO.getName(), 
                personDTO.getCity(), 
                personDTO.getEmail(), 
                personDTO.getCep(), 
                personDTO.getAddress(), 
                personDTO.getCountry(), 
                personDTO.getUser(), 
                personDTO.getPhone(), 
                LocalDate.parse(personDTO.getBirthday(), dateTimeFormatter),
                personDTO.getRoleId(),
                roles.get(personDTO.getRoleId())
            ));

        }

        return persons;
    }

    @SuppressWarnings("null")
    @EventListener
    public void addDataPosReady(ApplicationReadyEvent event) throws IOException, URISyntaxException, CsvException {

        if(!salaryRepository.findAll().iterator().hasNext()){
            salaryRepository.saveAll(this.loadEntityFromCsv(Salary.class,salaryDataResource));
        }

        if(!roleRepository.findAll().iterator().hasNext()){
            roleRepository.saveAll(this.loadEntityFromCsv(Role.class,roleDataResource));
        }

        Map<Long,Salary> salaryMap = new HashMap<>();
        for(Salary salary : salaryRepository.findAll()){
            salaryMap.put(salary.getId(), salary);
        }

        Map<Long,Role> roleMap = new HashMap<>();
        for(Role role : roleRepository.findAll()){
            roleMap.put(role.getId(), role);
        }

        List<Role> emptyRoles = roleMap.values().stream().filter(role->role.getSalaries()==null || role.getSalaries().isEmpty()).collect(Collectors.toList());
        
        if(!emptyRoles.isEmpty()){
            List<RoleSalary> rolesSalaries = this.loadEntityFromCsv(RoleSalary.class,rolesSalariesDataResource);
        
            Map<Long,List<Long>> mapRoleIdSalary = this.creteMapRoleSalary(rolesSalaries);

            this.updateRolesWithSalaries(roleMap,salaryMap, mapRoleIdSalary, roleRepository);
        }

        if(!personRepository.findAll().iterator().hasNext()){
            List<PersonWithRoleId> personsWithRoleId = this.loadEntityFromCsv(PersonWithRoleId.class,personDataResource);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("[MM/dd/yyyy][M/dd/yyyy][MM/d/yyyy][M/d/yyyy]");
            List<Person> persons = this.createPersons(personsWithRoleId,roleMap, dateTimeFormatter);

            personRepository.saveAll(persons);
        }        
    }
    
}
