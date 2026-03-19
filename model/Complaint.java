package com.college.cms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Complaint {

    private int           complaintId;
    private String        studentId;
    private String        category;
    private String        description;
    private String        status;
    private LocalDateTime createdAt;

    // Populated by JOIN queries – never hardcoded
    private String studentName;
    private String studentCourse;
    private String responseText; // admin response (may be null)

    // Default constructor
    public Complaint() {}

    // Constructor used when inserting a new complaint (DB assigns the ID)
    public Complaint(String studentId, String category, String description) {
        this.studentId   = studentId;
        this.category    = category;
        this.description = description;
        this.status      = "Pending";
    }

    // Full constructor (used when reading a row from the DB)
    public Complaint(int complaintId, String studentId, String category,
                     String description, String status, LocalDateTime createdAt) {
        this.complaintId = complaintId;
        this.studentId   = studentId;
        this.category    = category;
        this.description = description;
        this.status      = status;
        this.createdAt   = createdAt;
    }

    // ── Display helpers ──────────────────────────────────────

    /** Returns "TCK-XXXX" format built from the DB complaint_id. */
    public String getTicketId() {
        return "TCK-" + (1000 + complaintId);
    }

    /** Formatted date string from the DB timestamp, e.g. "14 Feb 2026". */
    public String getFormattedDate() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /** Formatted time string from the DB timestamp, e.g. "4:15 PM". */
    public String getFormattedTime() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    /** Truncated description for list card previews. */
    public String getPreview(int maxChars) {
        if (description == null) return "";
        return description.length() > maxChars
               ? description.substring(0, maxChars) + "..."
               : description;
    }

    // ── Getters & setters ────────────────────────────────────
    public int           getComplaintId()              { return complaintId; }
    public void          setComplaintId(int id)        { this.complaintId = id; }
    public String        getStudentId()                { return studentId; }
    public void          setStudentId(String id)       { this.studentId = id; }
    public String        getCategory()                 { return category; }
    public void          setCategory(String c)         { this.category = c; }
    public String        getDescription()              { return description; }
    public void          setDescription(String d)      { this.description = d; }
    public String        getStatus()                   { return status; }
    public void          setStatus(String s)           { this.status = s; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void          setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public String        getStudentName()              { return studentName; }
    public void          setStudentName(String n)      { this.studentName = n; }
    public String        getStudentCourse()            { return studentCourse; }
    public void          setStudentCourse(String c)    { this.studentCourse = c; }
    public String        getResponseText()             { return responseText; }
    public void          setResponseText(String r)     { this.responseText = r; }
}
