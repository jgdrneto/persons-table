package com.frontend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frontend.configs.BackendConfigs;
import com.frontend.models.Role;
import com.frontend.models.Salary;
import com.frontend.utils.MyHttpClient;
import com.frontend.utils.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService{

    @Autowired
    private BackendConfigs backendConfig;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private MyHttpClient httpClient;

    public double calculeSalary(Role role){
        return role.obtainTotalSalary();
    }

    public List<Role> findAll() throws IOException, InterruptedException {
        
        Map<Long,Salary> salariesMap = salaryService.findAllWithMap();

        TypeReference<Page<Role>> rolePageType = new TypeReference<Page<Role>>() {};
        
        List<Role> roles = new ArrayList<>();
    
        Page<Role> rolesFirstPage = this.httpClient.get(this.backendConfig.obtainRoleUrl(0,backendConfig.getDefaultPageSize()), rolePageType, true);
        
        for(Role r : rolesFirstPage.getContent()){
            List<Salary> salaries = new ArrayList<>();
            for(Long salaryId : r.getSalaryIds()){
                salaries.add(salariesMap.get(salaryId));
            }
            r.setObjSalaries(salaries);
        }

        roles.addAll(rolesFirstPage.getContent());
        
        for(int page=1; page<rolesFirstPage.getTotalPages();page++){

            Page<Role> rolesPage = this.httpClient.get(this.backendConfig.obtainRoleUrl(page,backendConfig.getDefaultPageSize()), rolePageType, true);
        
            for(Role r : rolesPage.getContent()){
                List<Salary> salaries = new ArrayList<>();
                for(Long salaryId : r.getSalaryIds()){
                    salaries.add(salariesMap.get(salaryId));
                }
                r.setObjSalaries(salaries);
            }

            roles.addAll(rolesPage.getContent());
        }
        
        return roles;
    }

    public Map<Long,Role> findAllWithMap() throws IOException, InterruptedException {
        
        Map<Long,Salary> salariesMap = salaryService.findAllWithMap();

        TypeReference<Page<Role>> rolePageType = new TypeReference<Page<Role>>() {};
        
        Map<Long,Role> rolesMap = new HashMap<>();
    
        Page<Role> rolesFirstPage = this.httpClient.get(this.backendConfig.obtainRoleUrl(0,backendConfig.getDefaultPageSize()), rolePageType, true);
        
        for(Role r : rolesFirstPage.getContent()){
            List<Salary> salaries = new ArrayList<>();
            for(Long salaryId : r.getSalaryIds()){
                salaries.add(salariesMap.get(salaryId));
            }
            r.setObjSalaries(salaries);
            rolesMap.put(r.getId(), r);
        }

        for(int page=1; page<rolesFirstPage.getTotalPages();page++){

            Page<Role> rolesPage = this.httpClient.get(this.backendConfig.obtainRoleUrl(page,backendConfig.getDefaultPageSize()), rolePageType, true);
        
            for(Role r : rolesPage.getContent()){
                List<Salary> salaries = new ArrayList<>();
                for(Long salaryId : r.getSalaryIds()){
                    salaries.add(salariesMap.get(salaryId));
                }
                r.setObjSalaries(salaries);
                rolesMap.put(r.getId(), r);
            }
        }
        
        return rolesMap;
    }

    public Role findById(Long roleId) throws IOException, InterruptedException {

        Role role = this.httpClient.get(this.backendConfig.obtainRoleUrl()+"/"+roleId, new TypeReference<Role>() {}, true);

        List<Salary> salaries = new ArrayList<>();

        for(Long salaryId : role.getSalaryIds()){
            salaries.add(salaryService.findById(salaryId));
        }

        role.setObjSalaries(salaries);

        return role;
    }

}
