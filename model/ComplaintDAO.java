package com.college.cms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    private Connection conn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    // ════════════════════════════════════════════════════════
    //  INSERT
    // ════════════════════════════════════════════════════════

    /**
     * Inserts a new complaint row.
     * Status defaults to 'Pending' (enforced by DB default).
     *
     * SQL:
     *   INSERT INTO complaints (student_id, category, description)
     *   VALUES (?, ?, ?)
     */
    public boolean insertComplaint(Complaint c) {
        final String sql =
            "INSERT INTO complaints (student_id, category, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getStudentId());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO.insertComplaint] " + e.getMessage());
            return false;
        }
    }

    // ════════════════════════════════════════════════════════
    //  SELECT — Student view
    // ════════════════════════════════════════════════════════

    /**
     * Returns all complaints belonging to one student,
     * plus any admin response text (LEFT JOIN).
     *
     * SQL:
     *   SELECT c.*, r.response_text
     *   FROM complaints c
     *   LEFT JOIN responses r ON c.complaint_id = r.complaint_id
     *   WHERE c.student_id = ?
     *   ORDER BY c.created_at DESC
     */
    public List<Complaint> getComplaintsByStudent(String studentId) {
        final String sql =
            "SELECT c.complaint_id, c.student_id, c.category, c.description, " +
            "       c.status, c.created_at, r.response_text " +
            "FROM complaints c " +
            "LEFT JOIN responses r ON c.complaint_id = r.complaint_id " +
            "WHERE c.student_id = ? " +
            "ORDER BY c.created_at DESC";
        List<Complaint> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Complaint c = mapComplaint(rs);
                    c.setResponseText(rs.getString("response_text")); // may be null
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO.getComplaintsByStudent] " + e.getMessage());
        }
        return list;
    }

    // ════════════════════════════════════════════════════════
    //  SELECT — Admin view  (all complaints with student info)
    // ════════════════════════════════════════════════════════

    /**
     * Returns every complaint joined with student name, course,
     * and any admin response — used to populate the Admin Dashboard.
     *
     * SQL:
     *   SELECT c.*, s.name AS student_name, s.course,
     *          r.response_text
     *   FROM complaints c
     *   JOIN  students  s ON c.student_id = s.student_id
     *   LEFT JOIN responses r ON c.complaint_id = r.complaint_id
     *   ORDER BY c.created_at DESC
     */
    public List<Complaint> getAllComplaints() {
        final String sql =
            "SELECT c.complaint_id, c.student_id, c.category, c.description, " +
            "       c.status, c.created_at, " +
            "       s.name AS student_name, s.course, " +
            "       r.response_text " +
            "FROM complaints c " +
            "JOIN  students  s ON c.student_id  = s.student_id " +
            "LEFT JOIN responses r ON c.complaint_id = r.complaint_id " +
            "ORDER BY c.created_at DESC";
        List<Complaint> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Complaint c = mapComplaint(rs);
                c.setStudentName(rs.getString("student_name"));   // from DB
                c.setStudentCourse(rs.getString("course"));        // from DB
                c.setResponseText(rs.getString("response_text")); // from DB
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO.getAllComplaints] " + e.getMessage());
        }
        return list;
    }

    // ════════════════════════════════════════════════════════
    //  UPDATE — status
    // ════════════════════════════════════════════════════════

    /**
     * Updates the status column of one complaint.
     *
     * SQL:
     *   UPDATE complaints SET status = ? WHERE complaint_id = ?
     */
    public boolean updateStatus(int complaintId, String status) {
        final String sql = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, complaintId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO.updateStatus] " + e.getMessage());
            return false;
        }
    }

    // ════════════════════════════════════════════════════════
    //  RESPONSE  (INSERT or UPDATE)
    // ════════════════════════════════════════════════════════

    /**
     * Saves an admin response.
     * If a response already exists for the complaint it is overwritten
     * (ON DUPLICATE KEY UPDATE).
     *
     * SQL:
     *   INSERT INTO responses (complaint_id, admin_id, response_text)
     *   VALUES (?, ?, ?)
     *   ON DUPLICATE KEY UPDATE response_text = VALUES(response_text),
     *                           responded_at  = NOW()
     */
    public boolean saveResponse(Response r) {
        final String sql =
            "INSERT INTO responses (complaint_id, admin_id, response_text) " +
            "VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "  response_text = VALUES(response_text), " +
            "  responded_at  = NOW()";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, r.getComplaintId());
            ps.setString(2, r.getAdminId());
            ps.setString(3, r.getResponseText());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO.saveResponse] " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches the response for one complaint, or null if none exists yet.
     *
     * SQL:
     *   SELECT * FROM responses WHERE complaint_id = ?
     */
    public Response getResponse(int complaintId) {
        final String sql = "SELECT * FROM responses WHERE complaint_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Response resp = new Response();
                    resp.setResponseId(rs.getInt("response_id"));
                    resp.setComplaintId(rs.getInt("complaint_id"));
                    resp.setAdminId(rs.getString("admin_id"));
                    resp.setResponseText(rs.getString("response_text"));
                    Timestamp ts = rs.getTimestamp("responded_at");
                    if (ts != null) resp.setRespondedAt(ts.toLocalDateTime());
                    return resp;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO.getResponse] " + e.getMessage());
        }
        return null;
    }

    // ── Private mapping helper ───────────────────────────────
    // Converts one ResultSet row into a Complaint object.
    // Values come from the DB — never hardcoded.
    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getInt("complaint_id"));
        c.setStudentId(rs.getString("student_id"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        return c;
    }
}
