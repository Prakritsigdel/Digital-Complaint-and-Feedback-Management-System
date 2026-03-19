package com.college.cms.model;
public abstract class User {

    private String email;
    private String password;

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    /** Each subclass declares its own role string (Polymorphism). */
    public abstract String getRole();

    // Getters & setters
    public String getEmail()                  { return email; }
    public void   setEmail(String email)      { this.email = email; }
    public String getPassword()               { return password; }
    public void   setPassword(String password){ this.password = password; }
}
