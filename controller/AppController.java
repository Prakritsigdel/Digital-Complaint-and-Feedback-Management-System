package com.college.cms.controller;

import java.util.List;

import com.college.cms.model.Admin;
import com.college.cms.model.Complaint;
import com.college.cms.model.ComplaintDAO;
import com.college.cms.model.Response;
import com.college.cms.model.Student;
import com.college.cms.model.User;
import com.college.cms.model.UserDAO;

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
        if (email == null || email.isBlank())       return null;
        if (password == null || password.isBlank()) return null;

        User u = userDAO.loginStudent(email.trim(), password);
        if (u == null) u = userDAO.loginAdmin(email.trim(), password);

        // ── SUCCESS ALERT: only fires if a matching user was found in DB ──
        if (u != null) {
            showSuccess("Login successful. Welcome, " + getDisplayName(u) + "!");
        }

        return u;
    }

    // ── Helper to get the display name from either user type ─────────────
    private String getDisplayName(User u) {
        if (u instanceof Student s) return s.getName();
        if (u instanceof Admin   a) return a.getName();
        return u.getEmail();
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
        boolean saved = userDAO.registerStudent(s);
        if (saved) {
            showSuccess("Account created successfully!\nYou can now log in.");
        }
        return saved ? null : "Registration failed — please try again.";
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

     // ── SUCCESS ALERT: only fires if INSERT into complaints table succeeded ──
     boolean inserted = complaintDAO.insertComplaint(c);
     if (inserted) {
         showSuccess("Complaint submitted successfully!\n" +
                     "You can track its status in My History.");
     }
     return inserted ? null : "Failed to submit complaint — please try again.";
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

        boolean responseSaved = complaintDAO.saveResponse(r);
        boolean statusUpdated = complaintDAO.updateStatus(complaintId, newStatus);

        // ── SUCCESS ALERT: only fires if BOTH response saved AND status updated ──
        if (responseSaved && statusUpdated) {
            showSuccess("Response submitted successfully!\n" +
                        "Complaint status updated to: " + newStatus);
        }
        return null;
    }

    /** Returns the admin response for one complaint (may be null). */
    public Response getResponse(int complaintId) {
        return complaintDAO.getResponse(complaintId);
    }
    private void showSuccess(String message) {
        javafx.scene.control.Alert alert =
            new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } 
}
