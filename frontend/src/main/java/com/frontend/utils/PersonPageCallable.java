package com.frontend.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frontend.models.Person;
import com.frontend.models.Role;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class PersonPageCallable implements Callable<List<Person>>{

    private String name;

    private String url;

    TypeReference<Page<Person>> personPageType;

    private Map<Long,Role> rolesMap;

    private MyHttpClient httpClient;

    @Override
    public List<Person> call() throws Exception {
        List<Person> newPersons = new ArrayList<>();

        Page<Person> personsPage = httpClient.get(this.url, this.personPageType, false);

        for(Person p : personsPage.getContent()){
            p.setObjRole(this.rolesMap.get(p.getRoleId()));
            newPersons.add(p);
        }
        log.info(this.name + " : Read " + newPersons.size() + " person(s)");

        return newPersons;
    }

}
