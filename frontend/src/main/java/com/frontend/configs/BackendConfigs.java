package com.frontend.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Component
@ConfigurationProperties(prefix = "backend-api")
public class BackendConfigs {

    private String protocol;
    private String host;
    private int port;
    private String apiBase;
    private String personCrud;
    private String roleCrud;
    private String salaryCrud;
    private long defaultPageSize;

    private String obtainEntityUrl(String entity){
        return this.protocol+"://"+this.host+":"+this.port+this.apiBase+entity;
    }

    public String obtainPersonUrl(){
        return this.obtainEntityUrl(this.personCrud);
    }

    public String obtainRoleUrl(){
        return this.obtainEntityUrl(this.roleCrud);
    }

    public String obtainSalaryUrl(){
        return this.obtainEntityUrl(this.salaryCrud);
    }

    public String obtainEntityUrl(String entity, long page, long size){
        return this.obtainEntityUrl(entity)+"?"+"page="+page+"&"+"size="+size;
    }

    public String obtainPersonUrl(long page, long size){
        return obtainEntityUrl(this.personCrud,page,size);
    }

    public String obtainRoleUrl(long page, long size){
        return obtainEntityUrl(this.roleCrud,page,size);
    }

    public String obtainSalaryUrl(long page, long size){
        return this.obtainEntityUrl(this.salaryCrud,page,size);
    }

}
