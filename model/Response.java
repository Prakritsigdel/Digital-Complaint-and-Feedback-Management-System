package com.college.cms.model;

import java.time.LocalDateTime;

public class Response {

    private int           responseId;
    private int           complaintId;
    private String        adminId;
    private String        responseText;
    private LocalDateTime respondedAt;

    public Response() {}

    // Constructor used when an admin submits a new response
    public Response(int complaintId, String adminId, String responseText) {
        this.complaintId  = complaintId;
        this.adminId      = adminId;
        this.responseText = responseText;
    }

    // Full constructor (from DB ResultSet)
    public Response(int responseId, int complaintId, String adminId,
                    String responseText, LocalDateTime respondedAt) {
        this.responseId   = responseId;
        this.complaintId  = complaintId;
        this.adminId      = adminId;
        this.responseText = responseText;
        this.respondedAt  = respondedAt;
    }

    public int           getResponseId()               { return responseId; }
    public void          setResponseId(int id)         { this.responseId = id; }
    public int           getComplaintId()              { return complaintId; }
    public void          setComplaintId(int id)        { this.complaintId = id; }
    public String        getAdminId()                  { return adminId; }
    public void          setAdminId(String id)         { this.adminId = id; }
    public String        getResponseText()             { return responseText; }
    public void          setResponseText(String t)     { this.responseText = t; }
    public LocalDateTime getRespondedAt()              { return respondedAt; }
    public void          setRespondedAt(LocalDateTime t){ this.respondedAt = t; }
}
