package org.example.domain;

import java.io.Serializable;

public class ParticipationDTO implements Serializable {
    private String name;
    private Long age;
    private String contests;

    public ParticipationDTO(String name, Long age, String contests) {
        this.name = name;
        this.age = age;
        this.contests = contests;
    }


    public String getName() {
        return name;
    }

    public Long getAge() {
        return age;
    }

    public String getContests() {
        return contests;
    }
}
