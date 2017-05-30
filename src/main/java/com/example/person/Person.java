package com.example.person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String firstname;

    private String lastname;

    private String ip;

    @Column(columnDefinition="varchar(1024)")
    private String ipInfo;

    private LocalDateTime updatedAt;

    private Person() {
    }

    public Person(Long id, String firstname, String lastname, String ip, String ipInfo) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ip = ip;
        this.ipInfo = ipInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public Person copyWithIpInfo(String ipInfo) {
        return new Person(id, firstname, lastname, ip, ipInfo);
    }

    public Long getId() {
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

    public String getIpInfo() {
        return ipInfo;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}