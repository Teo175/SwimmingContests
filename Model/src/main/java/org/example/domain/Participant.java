package org.example.domain;


import java.io.Serializable;

public class Participant extends Entity<Long> implements Serializable {

    private String Name;
    private Long Age;

    public Participant(String name, Long age) {
        Name = name;
        Age = age;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Long getAge() {
        return Age;
    }

    public void setAge(Long age) {
        Age = age;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "Name='" + Name + '\'' +
                ", Age=" + Age +
                '}';
    }
}