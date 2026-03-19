package com.college.cms.model;

public class Admin extends User {

    private String adminId;
    private String name;

    public Admin() { super(); }

    public Admin(String adminId, String name, String email, String password) {
        super(email, password);
        this.adminId = adminId;
        this.name    = name;
    }

    @Override
    public String getRole() { return "ADMIN"; }

    public String getAdminId()               { return adminId; }
    public void   setAdminId(String adminId) { this.adminId = adminId; }
    public String getName()                  { return name; }
    public void   setName(String name)       { this.name = name; }
}
