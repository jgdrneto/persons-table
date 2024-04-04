package com.frontend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frontend.configs.BackendConfigs;
import com.frontend.models.Salary;
import com.frontend.utils.MyHttpClient;
import com.frontend.utils.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryService{

    @Autowired
    private BackendConfigs backendConfig;

    @Autowired
    private MyHttpClient httpClient;

    public List<Salary> findAll() throws IOException, InterruptedException {
        
        TypeReference<Page<Salary>> type = new TypeReference<Page<Salary>>() {};

        List<Salary> salaries = new ArrayList<>();

        Page<Salary> salariesPage = this.httpClient.get(this.backendConfig.obtainSalaryUrl(0,backendConfig.getDefaultPageSize()), type, true);
        salaries.addAll(salariesPage.getContent());

        for(int page=1; page<salariesPage.getTotalPages();page++){
            salaries.addAll(this.httpClient.get(this.backendConfig.obtainSalaryUrl(page,backendConfig.getDefaultPageSize()), type, true).getContent());
        }

        return salaries;

    }

    public Map<Long,Salary> findAllWithMap() throws IOException, InterruptedException {
        
        TypeReference<Page<Salary>> type = new TypeReference<Page<Salary>>() {};

        Map<Long,Salary> salaries = new HashMap<>();

        Page<Salary> salariesFirstPage = this.httpClient.get(this.backendConfig.obtainSalaryUrl(0,backendConfig.getDefaultPageSize()), type, true);
        for(Salary s : salariesFirstPage.getContent()){
            salaries.put(s.getId(), s);
        }

        for(int page=1; page<salariesFirstPage.getTotalPages();page++){
            Page<Salary> salariesPage = this.httpClient.get(this.backendConfig.obtainSalaryUrl(page,backendConfig.getDefaultPageSize()), type, true);
            for(Salary s : salariesPage.getContent()){
                salaries.put(s.getId(), s);
            }
        }

        return salaries;

    }

    public Salary findById(Long salaryId) throws IOException, InterruptedException {
        return this.httpClient.get(this.backendConfig.obtainSalaryUrl()+"/"+salaryId, new TypeReference<Salary>() {}, true);
    }
}
