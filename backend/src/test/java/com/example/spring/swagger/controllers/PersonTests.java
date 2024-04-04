package com.example.spring.swagger.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.spring.swagger.models.Person;
import com.example.spring.swagger.models.Role;
import com.example.spring.swagger.models.Salary;
import com.example.spring.swagger.models.SalaryType;
import com.example.spring.swagger.repositories.PersonRepository;
import com.example.spring.swagger.repositories.RoleRepository;
import com.example.spring.swagger.repositories.SalaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PersonController.class)
class PersonTests {

	@Autowired
    private MockMvc mvc;

    @MockBean
    private PersonRepository repository;

	@MockBean
    private RoleRepository roleRepository;

	@MockBean
    private SalaryRepository salaryRepository;

	@Autowired
  	private ObjectMapper objectMapper;

	private static Map<Long,Salary> obtainAllSalaries(){
		Map<Long,Salary> salaries = new HashMap<>();

		salaries.put(1L, new Salary(1L, "Vencimento Basico Estagiario", 400, SalaryType.CREDITO));
		salaries.put(2L, new Salary(2L, "Vencimento Basico Tecnico", 1000, SalaryType.CREDITO));
		salaries.put(3L, new Salary(3L, "Vencimento Basico Analista", 2500, SalaryType.CREDITO));
		salaries.put(8L, new Salary(8L, "Plano de Saude", 350, SalaryType.DEBITO));
		salaries.put(9L, new Salary(9L, "Previdencia", 11, SalaryType.DEBITO));
		
		return salaries;
	}

	private static Map<Long,Role> obtainAllRoles(){
		
		Map<Long,Salary> salaries = obtainAllSalaries();
		
		Map<Long,Role> roles = new HashMap<>();

		List<Salary>roleSalaries = new ArrayList<>();
		roleSalaries.add(salaries.get(1L));
		roles.put(1L, new Role(1L, "Estagiario", roleSalaries.stream().map(Salary::getId).toList(), roleSalaries));

		roleSalaries = new ArrayList<>();
		roleSalaries.add(salaries.get(2L));
		roleSalaries.add(salaries.get(9L));
		roleSalaries.add(salaries.get(8L));
		roles.put(2L, new Role(2L, "Tecnico", roleSalaries.stream().map(Salary::getId).toList(), roleSalaries));

		roleSalaries = new ArrayList<>();
		roleSalaries.add(salaries.get(3L));
		roleSalaries.add(salaries.get(9L));
		roleSalaries.add(salaries.get(8L));
		roles.put(3L, new Role(3L, "Analista", roleSalaries.stream().map(Salary::getId).toList(), roleSalaries));

		return roles;
	}	

	private static Map<Long,Person> obtainAllPersons(){
		
		Map<Long,Role> roles = obtainAllRoles();
		
		Map<Long,Person> persons = new HashMap<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		persons.put(1L, new Person(
			1L, 
			"Bianca Correia", 
			"São Paulo", 
			"BiancaAlmeidaCorreia@dayrep.com", 
			"18278-314", 
			"Rua Maria Tereza Moraes 227", 
			"Brazil", 
			"Hamay1935", 
			"(15) 6493-3898", 
			LocalDate.parse("09/03/1935",formatter),
			1L ,
			roles.get(1L)
			)
		);

		persons.put(4L, new Person(
			4L, 
			"Luana Melo", 
			"Paraná", 
			"LuanaCorreiaMelo@einrot.com", 
			"81560-700", 
			"Rua Eduardo Victor Piechnik 1370", 
			"Brazil", 
			"Othrom", 
			"(41) 8480-8722", 
			LocalDate.parse("06/26/1965",formatter),
			2L ,
			roles.get(2L)
			)
		);

		persons.put(875L, new Person(
			875L, 
			"Cauã Cunha", 
			"Espírito Santo", 
			"CauaPereiraCunha@dayrep.com", 
			"29106-285", 
			"Travessa Angelina Leal 98", 
			"Brazil", 
			"Thantaight", 
			"(27) 3941-9073", 
			LocalDate.parse("01/11/1966",formatter),
			3L ,
			roles.get(3L)
			)
		);

		return persons;
	}

	private <T> Page<T> createPage(List<T> persons){

		Pageable pageRequest = PageRequest.of(0, 20);

		int start = (int) pageRequest.getOffset();
		int end = Math.min((start + pageRequest.getPageSize()), persons.size());

		List<T> pageContent = new ArrayList<>();
		if(!persons.isEmpty()){
			pageContent.addAll(persons.subList(start, end));
		}
		return new PageImpl<>(pageContent, pageRequest, persons.size());
	}

	@SuppressWarnings("null")
	private void populeAllEntities(){
		
		for(Entry<Long,Salary> entrySalaries : obtainAllSalaries().entrySet()){
			long salaryKey = entrySalaries.getKey();
			Optional<Salary> optSalary = Optional.of(entrySalaries.getValue());

			Mockito.when(salaryRepository.findById(salaryKey)).thenReturn(optSalary);

		}

		for(Entry<Long,Role> entryRoles : obtainAllRoles().entrySet()){
			long roleKey = entryRoles.getKey();
			Optional<Role> optRole = Optional.of(entryRoles.getValue());

			Mockito.when(roleRepository.findById(roleKey)).thenReturn(optRole);
		}

		for(Entry<Long,Person> entryPerson : obtainAllPersons().entrySet()){
			long personKey = entryPerson.getKey();
			Optional<Person> optPerson = Optional.of(entryPerson.getValue());

			Mockito.when(repository.findById(personKey)).thenReturn(optPerson);
		}

		Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(createPage(new ArrayList<>(obtainAllPersons().values())));

	}


	@Test
	public void obtainAllPersonsTest() throws Exception {
		
		this.populeAllEntities();

		this.mvc.perform(MockMvcRequestBuilders.get("/api/person"))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(3))
      	.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(4))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content[2].id").value(875));
		
	}

	@Test
	public void findPersonTest() throws Exception {

		this.populeAllEntities();

		for(Entry<Long,Person> entryPerson : obtainAllPersons().entrySet()){
			long id = entryPerson.getKey();
			Person person = entryPerson.getValue();
		
			this.mvc.perform(MockMvcRequestBuilders.get("/api/person/{id}", id))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(person.getName()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.roleId").value(person.getRoleId()));
		}
	}

	@SuppressWarnings("null")
	@Test
	public void updatePersonTest() throws Exception {

		this.populeAllEntities();

		Person p = obtainAllPersons().values().iterator().next();
		p.setName("Lucas");
		p.setRoleId(2);

		Mockito.when(repository.save(any(Person.class))).thenReturn(p);

		this.mvc.perform(MockMvcRequestBuilders.put("/api/person/{id}", p.getId())
		.contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(p)))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(p.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(p.getName()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.roleId").value(p.getRoleId()));

	}

	@SuppressWarnings("null")
	@Test
	public void createPersonTest() throws Exception {

		this.populeAllEntities();

		Map<Long,Role> roles = obtainAllRoles();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		Person newPerson = new Person(
			0, 
			"Afonso Ribeiro", 
			"Espírito Santo", 
			"CauaPereiraCunha@dayrep.com", 
			"29106-285", 
			"Travessa Angelina Leal 98", 
			"Brazil", 
			"Thantaight", 
			"(27) 3941-9073", 
			LocalDate.parse("01/11/1966",formatter),
			3L,
			roles.get(3L)
			);

		String entity = objectMapper.writeValueAsString(newPerson);

		newPerson.setId(900L);

		Mockito.when(repository.save(any(Person.class))).thenReturn(newPerson);

		this.mvc.perform(MockMvcRequestBuilders.post("/api/person")
		.contentType(MediaType.APPLICATION_JSON)
		.content(entity))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(newPerson.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newPerson.getName()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.roleId").value(newPerson.getRoleId()));
		
	}

	@Test
	public void removePersonTest() throws Exception {
		
		this.populeAllEntities();

		Person person = obtainAllPersons().values().iterator().next();

		this.mvc.perform(MockMvcRequestBuilders.delete("/api/person/{id}", person.getId()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(person.getName()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.roleId").value(person.getRoleId()));
		
	}

	@Test
	public void calculeSalaryTest() throws Exception {
		
		this.populeAllEntities();

		for(Person person : obtainAllPersons().values()){
			MvcResult mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/api/person/{id}/salary", person.getId()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		
			double salary = Double.parseDouble(mvcResult.getResponse().getContentAsString());

			assertEquals(person.getRole().getTotalSalary(), salary, 0);

		}

	}
}
