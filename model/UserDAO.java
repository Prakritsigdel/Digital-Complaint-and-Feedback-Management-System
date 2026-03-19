package com.college.cms.model;

import java.sql.*;
public class UserDAO {

    private Connection conn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    // ── AUTHENTICATION ────────────────────────────────────────

    /**
     * Checks credentials against the students table.
     * Returns a fully-populated Student object on success, null otherwise.
     *
     * SQL: SELECT * FROM students WHERE email = ? AND password = ?
     */
    public Student loginStudent(String email, String password) {
        final String sql =
            "SELECT student_id, name, email, course, password " +
            "FROM students WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("course"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.loginStudent] " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks credentials against the admins table.
     * Returns a fully-populated Admin object on success, null otherwise.
     *
     * SQL: SELECT * FROM admins WHERE email = ? AND password = ?
     */
    public Admin loginAdmin(String email, String password) {
        final String sql =
            "SELECT admin_id, name, email, password " +
            "FROM admins WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getString("admin_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.loginAdmin] " + e.getMessage());
        }
        return null;
    }

    // ── REGISTRATION ──────────────────────────────────────────

    /**
     * Inserts a new student row.
     * Returns true if insert succeeded, false if email already exists.
     *
     * SQL: INSERT INTO students VALUES (?, ?, ?, ?, ?)
     */
    public boolean registerStudent(Student s) {
        if (emailExistsInStudents(s.getEmail())) return false;
        final String sql =
            "INSERT INTO students (student_id, name, email, course, password) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail().trim().toLowerCase());
            ps.setString(4, s.getCourse());
            ps.setString(5, s.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.registerStudent] " + e.getMessage());
            return false;
        }
    }

    /** Checks if an email already exists in the students table. */
    public boolean emailExistsInStudents(String email) {
        final String sql = "SELECT COUNT(*) FROM students WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.emailExistsInStudents] " + e.getMessage());
        }
        return false;
    }

    /** Checks if a student_id already exists in the students table. */
    public boolean studentIdExists(String studentId) {
        final String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.studentIdExists] " + e.getMessage());
        }
        return false;
    }
}
