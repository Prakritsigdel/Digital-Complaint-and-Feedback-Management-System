package com.college.cms;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * UiHelper – Factory for shared UI components used across multiple screens.
 *
 * CRITICAL: Every method here accepts data values from model objects
 * and never contains hardcoded display strings.
 */
public final class UiHelper {

    private UiHelper() {}

    // ── Status badge pill ────────────────────────────────────
    // Colour is derived from the status string read from the DB.
    // status parameter always comes from Complaint.getStatus().
    public static Label statusBadge(String status) {
        Label badge = new Label(status);
        badge.setPadding(new Insets(3, 12, 3, 12));
        badge.setStyle(
            "-fx-background-radius: 20; -fx-text-fill: white; " +
            "-fx-font-size: 11px; -fx-font-weight: bold; " +
            "-fx-background-color: " + statusColor(status) + ";"
        );
        return badge;
    }

    // Maps a DB status value to a hex colour
    public static String statusColor(String status) {
        if (status == null) return "#94a3b8";
        return switch (status) {
            case "Resolved"     -> "#10b981"; // green
            case "Under Review" -> "#3b82f6"; // blue
            default             -> "#f59e0b"; // orange (Pending)
        };
    }

    // ── Category colour ──────────────────────────────────────
    // Colour is derived from the category value from the DB.
    public static String categoryColor(String category) {
        if (category == null) return "#475569";
        return switch (category) {
            case "Infrastructure" -> "#1d4ed8";
            case "Academic"       -> "#7c3aed";
            case "IT Support"     -> "#0891b2";
            case "Financial"      -> "#b45309";
            case "Health & Safety"-> "#dc2626";
            default               -> "#475569";
        };
    }

    // ── Info column (label + value) ──────────────────────────
    // Used in the Admin detail panel.
    // Both labelText and valueText come from model object getters.
    public static javafx.scene.layout.VBox infoColumn(String labelText, String valueText) {
        javafx.scene.layout.VBox col = new javafx.scene.layout.VBox(4);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
        Label val = new Label(valueText != null ? valueText : "—");
        val.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        col.getChildren().addAll(lbl, val);
        return col;
    }

    // ── Numbered circle ──────────────────────────────────────
    public static StackPane numberCircle(String num) {
        StackPane sp = new StackPane();
        sp.setStyle(
            "-fx-background-color: #1d4ed8; -fx-background-radius: 50%; " +
            "-fx-min-width: 26; -fx-min-height: 26; " +
            "-fx-max-width: 26; -fx-max-height: 26;");
        Label l = new Label(num);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
        sp.getChildren().add(l);
        return sp;
    }
}
