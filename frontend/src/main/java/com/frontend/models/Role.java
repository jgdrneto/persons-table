package com.frontend.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Role{

    private long id;

    private String name;

    private List<Long> salaryIds;

    @JsonIgnore
    private List<Salary> objSalaries;

    public Role(){
        this.salaryIds = new ArrayList<>();
        this.objSalaries = new ArrayList<>();
    }

    @JsonIgnore
    public double obtainTotalSalary(){
        double salary = 0;
        
        for(Salary s : this.objSalaries){
            switch (s.getType()) {
                case CREDITO:
                    salary+=s.getValue();
                break;
                case DEBITO:
                    salary-=s.getValue();
                break;
            }
        }
        return salary;
    }

}
