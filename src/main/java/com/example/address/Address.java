package com.example.address;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address {

    @Id @GeneratedValue
    private Long id;
    private String personId;
    private String street;
    private String zipCode;

    private Address() {
    }

    public Address(String personId, String street, String zipCode) {
        this.personId = personId;
        this.street = street;
        this.zipCode = zipCode;
    }

    public Long getId() {
        return id;
    }

    public String getPersonId() {
        return personId;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

}
