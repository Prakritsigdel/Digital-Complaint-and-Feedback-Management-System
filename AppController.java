package com.college.cms;

import java.util.List;

/**
 * AppController – Connects JavaFX UI screens to DAO classes.
 *
 * DATA FLOW:
 *   UI event (button click)
 *     → AppController method
 *       → DAO (SQL via PreparedStatement)
 *         → Model object
 *           → returned to UI for display
 *
 * The UI NEVER calls DAO classes directly.
 * The UI NEVER constructs data strings — it only reads model getters.
 */
public class AppController {

    private final UserDAO      userDAO      = new UserDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    // ════════════════════════════════════════════════════════
    //  AUTHENTICATION
    // ════════════════════════════════════════════════════════

    /**
     * Tries student login first, then admin.
     * @return Student, Admin, or null if credentials are wrong.
     */
    public User login(String email, String password) {
        if (email == null || email.isBlank())    return null;
        if (password == null || password.isBlank()) return null;
        User u = userDAO.loginStudent(email, password);
        if (u == null) u = userDAO.loginAdmin(email, password);
        return u;
    }

    /**
     * Validates input and registers a new student.
     * @return null on success, or an error message string.
     */
    public String register(String studentId, String name,
                           String course,    String email,
                           String password) {
        if (studentId.isBlank()) return "Student ID is required.";
        if (name.isBlank())      return "Full name is required.";
        if (course.isBlank())    return "Please select your course.";
        if (email.isBlank())     return "Email address is required.";
        if (password.length() < 6)
            return "Password must be at least 6 characters.";
        if (userDAO.studentIdExists(studentId))
            return "Student ID is already registered.";
        if (userDAO.emailExistsInStudents(email))
            return "Email address is already registered.";

        Student s = new Student(studentId.trim(), name.trim(),
                                email.trim().toLowerCase(),
                                course.trim(), password);
        return userDAO.registerStudent(s) ? null
               : "Registration failed — please try again.";
    }

    // ════════════════════════════════════════════════════════
    //  COMPLAINT — Student
    // ════════════════════════════════════════════════════════

    /**
     * Validates and inserts a new complaint.
     * @return null on success, or a validation error message.
     */
    public String submitComplaint(String studentId, String category, String description) {
        if (category == null || category.isBlank())
            return "Please select a category.";
        if (description == null || description.isBlank())
            return "Please enter a description.";
        if (description.trim().length() < 15)
            return "Description must be at least 15 characters.";

        Complaint c = new Complaint(studentId, category, description.trim());
        return complaintDAO.insertComplaint(c) ? null
               : "Failed to submit complaint — please try again.";
    }

    /**
     * Returns all complaints for the logged-in student.
     * Data comes from: DB → DAO → List<Complaint>
     */
    public List<Complaint> getMyComplaints(String studentId) {
        return complaintDAO.getComplaintsByStudent(studentId);
    }

    // ════════════════════════════════════════════════════════
    //  COMPLAINT — Admin
    // ════════════════════════════════════════════════════════

    /**
     * Returns all complaints in the system (admin view).
     * Data: DB JOIN → DAO → List<Complaint> (with studentName, course, response)
     */
    public List<Complaint> getAllComplaints() {
        return complaintDAO.getAllComplaints();
    }

    /**
     * Saves an admin response and updates the complaint status.
     * Both operations go to the DB via PreparedStatement.
     * @return null on success, or a validation error message.
     */
    public String respondToComplaint(int complaintId, String adminId,
                                     String responseText, String newStatus) {
        if (responseText == null || responseText.isBlank())
            return "Response text cannot be empty.";
        if (newStatus == null || newStatus.isBlank())
            return "Please select a status.";

        Response r = new Response(complaintId, adminId, responseText.trim());
        complaintDAO.saveResponse(r);
        complaintDAO.updateStatus(complaintId, newStatus);
        return null;
    }

    /** Returns the admin response for one complaint (may be null). */
    public Response getResponse(int complaintId) {
        return complaintDAO.getResponse(complaintId);
    }
}
