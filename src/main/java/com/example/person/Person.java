package com.example.person;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Person {

    @Id
    private String id;

    private String firstname;

    private String lastname;

    private String ip;

    private Object ipInfo;

    private Person() {
    }

    public Person(String id, String firstname, String lastname, String ip, Object ipInfo) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ip = ip;
        this.ipInfo = ipInfo;
    }

    public Person copyWithIpInfo(Object ipInfo) {
        return new Person(id, firstname, lastname, ip, ipInfo);
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getIp() {
        return ip;
    }

    public Object getIpInfo() {
        return ipInfo;
    }

}