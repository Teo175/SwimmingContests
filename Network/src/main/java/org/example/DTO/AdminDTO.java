package org.example.DTO;

import java.io.Serializable;

public class AdminDTO implements Serializable {
    private String user;
    private String passwd;

    public AdminDTO(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return passwd;
    }

    @Override
    public String toString() {
        return "CashierDTO{" +
                "id=" + user +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
