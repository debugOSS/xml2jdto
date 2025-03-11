package io.github.debug.xml2jdto.core.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person", propOrder = { "name", "age", "address" })
public class Person {
    private String name;
    private int age;
    private Object address;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Object getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAddress(Object address) {
        this.address = address;
    }
}
