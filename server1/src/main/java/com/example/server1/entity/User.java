package com.example.server1.entity;

import java.io.Serializable;

public class User  implements Serializable
{
    private static final long serialVersionUID = 1810538160493143519L;

    private String userName = "xxc1";
    private Integer age = 20;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public User(){}

    public User(String userName, Integer age)
    {
        this.userName = userName;
        this.age = age;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("userName='").append(userName).append('\'');
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}
