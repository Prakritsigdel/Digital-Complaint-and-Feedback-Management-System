package com.college.cms.model;

public class Student extends User {

    private String studentId; // PRIMARY KEY in DB  e.g. "STU2024001" entered by user
    private String name;
    private String course;

    // Default constructor
    public Student() { super(); }

    // Full constructor (used when loading a row from the DB)
    public Student(String studentId, String name, String email,
                   String course,   String password) {
        super(email, password);
        this.studentId = studentId;
        this.name      = name;
        this.course    = course;
    }

    @Override
    public String getRole() { return "STUDENT"; }

    /** Returns up to two initials for the avatar circle. "Alex Johnson" → "AJ" */
    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0)));
            if (sb.length() == 2) break;
        }
        return sb.toString();
    }

    /** Returns only the first word of the name for greeting labels. */
    public String getFirstName() {
        if (name == null || name.isBlank()) return "";
        return name.trim().split("\\s+")[0];
    }

    // Getters & setters
    public String getStudentId()              { return studentId; }
    public void   setStudentId(String id)     { this.studentId = id; }
    public String getName()                   { return name; }
    public void   setName(String name)        { this.name = name; }
    public String getCourse()                 { return course; }
    public void   setCourse(String course)    { this.course = course; }
}
