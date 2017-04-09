package com.example.address;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address {
    private @Id @GeneratedValue Long id;
    private String personId;
    private String street;
    private String zipCode;

    public Address() {
    }

    public Address(String personId, String street, String zipCode) {
        this.personId = personId;
        this.street = street;
        this.zipCode = zipCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (id != null ? !id.equals(address.id) : address.id != null) return false;
        if (personId != null ? !personId.equals(address.personId) : address.personId != null) return false;
        if (street != null ? !street.equals(address.street) : address.street != null) return false;
        return zipCode != null ? zipCode.equals(address.zipCode) : address.zipCode == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (personId != null ? personId.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", personId='" + personId + '\'' +
                ", street='" + street + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
