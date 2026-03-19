package com.college.cms.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

import com.college.cms.controller.AppController;
import com.college.cms.model.Admin;
import com.college.cms.model.Complaint;
import com.college.cms.model.Response;
import com.college.cms.util.UiHelper;

public class AdminDashboard {

    private final Stage         stage;
    private final Admin         admin;       // populated from DB at login
    private final AppController controller = new AppController();

    private List<Complaint> complaints;  // loaded from DB
    private ScrollPane      rightScroll; // swapped when a ticket is selected

    public AdminDashboard(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin; // object fields all came from DB SELECT
    }

    public void show() {
        // ── DB query: SELECT all complaints JOIN students LEFT JOIN responses ──
        complaints = controller.getAllComplaints();

        BorderPane root = new BorderPane();
        root.setTop(buildTopBar());

        HBox body = new HBox();

        // Left panel — one card per DB row
        ScrollPane leftScroll = new ScrollPane(buildTicketList());
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setPrefWidth(430);
        leftScroll.setStyle("-fx-background:white;-fx-background-color:white;");

        // Right panel — detail view (placeholder until first selection)
        rightScroll = new ScrollPane(buildPlaceholder());
        rightScroll.setFitToWidth(true);
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setStyle("-fx-background:#f0f4f8;-fx-background-color:#f0f4f8;");
        HBox.setHgrow(rightScroll, Priority.ALWAYS);

        body.getChildren().addAll(leftScroll, rightScroll);
        root.setCenter(body);

        // Auto-select first ticket so the detail panel is not empty
        if (!complaints.isEmpty()) {
            rightScroll.setContent(buildDetailView(complaints.get(0)));
        }

        Scene scene = new Scene(root, 1260, 720);
        LoginScreen.applyStyles(scene);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // ════════════════════════════════════════════════════════
    //  TOP BAR
    //  admin.getName() — DB value; never hardcoded as "Administrator"
    // ════════════════════════════════════════════════════════
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("topbar");
        bar.setPadding(new Insets(0, 24, 0, 16));
        bar.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setStyle("-fx-background-color:#1d4ed8;-fx-background-radius:8;" +
                      "-fx-min-width:38;-fx-min-height:38;-fx-max-width:38;-fx-max-height:38;");
        Label iLbl = new Label("\u22EE\u22EE");
        iLbl.setStyle("-fx-font-size:13px;-fx-text-fill:white;-fx-font-weight:bold;");
        icon.getChildren().add(iLbl);

        VBox titleBox = new VBox(1);
        titleBox.setPadding(new Insets(0,0,0,10));
        Label t1 = new Label("Admin Dashboard");
        t1.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");
        Label t2 = new Label("All Tickets Dashboard");
        t2.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");
        titleBox.getChildren().addAll(t1, t2);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // admin.getName() — read from the DB admins row, never hardcoded
        Label adminLbl = new Label(admin.getName());
        adminLbl.setStyle("-fx-font-size:13px;-fx-text-fill:#475569;");

        Button logoutBtn = new Button("Logout  \u2192");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> new LoginScreen(stage).show());

        bar.getChildren().addAll(icon, titleBox, spacer, adminLbl, logoutBtn);
        return bar;
    }

    // ════════════════════════════════════════════════════════
    //  LEFT PANEL — ticket list
    // ════════════════════════════════════════════════════════
    private VBox buildTicketList() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color:white;");

        // Header shows the real count from the DB query
        VBox header = new VBox(4);
        header.setPadding(new Insets(18, 18, 14, 18));
        header.setStyle("-fx-background-color:white;" +
                        "-fx-border-color:#e2e8f0;-fx-border-width:0 0 1 0;");
        Label ht = new Label("INCOMING TICKETS");
        ht.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");
        // Count comes from the DB result list size — not a hardcoded number
        Label hc = new Label(complaints.size() + " total ticket" +
                             (complaints.size() != 1 ? "s" : ""));
        hc.setStyle("-fx-font-size:12px;-fx-text-fill:#64748b;");
        header.getChildren().addAll(ht, hc);
        panel.getChildren().add(header);

        for (int i = 0; i < complaints.size(); i++) {
            panel.getChildren().add(buildTicketCard(complaints.get(i), i == 0));
        }
        return panel;
    }

    /**
     * Builds one clickable ticket card.
     *
     * EVERY string displayed is read from a Complaint getter.
     * The Complaint object was built by ComplaintDAO from a DB ResultSet.
     */
    private VBox buildTicketCard(Complaint c, boolean selected) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle(selected ? SELECTED_CARD : NORMAL_CARD);

        // Ticket ID — c.getTicketId() derives "TCK-XXXX" from DB complaint_id
        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER_LEFT);
        Label idLbl = new Label(c.getTicketId());
        idLbl.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        // Status badge colour derived from c.getStatus() — DB ENUM value
        row1.getChildren().addAll(idLbl, sp, UiHelper.statusBadge(c.getStatus()));
        card.getChildren().add(row1);

        // Student name — c.getStudentName() populated by the JOIN query
        Label nameLbl = new Label(c.getStudentName() != null ? c.getStudentName() : "—");
        nameLbl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");
        card.getChildren().add(nameLbl);

        // Category — c.getCategory() from DB
        Label catLbl = new Label(c.getCategory());
        catLbl.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" +
                        UiHelper.categoryColor(c.getCategory()) + ";");
        card.getChildren().add(catLbl);

        // Description preview — c.getPreview(80) truncates the DB text field
        Label preview = new Label(c.getPreview(80));
        preview.setStyle("-fx-font-size:12px;-fx-text-fill:#64748b;");
        preview.setWrapText(true);
        card.getChildren().add(preview);

        // Click: load the detail view with this complaint's DB data
        card.setOnMouseClicked(e -> {
            rightScroll.setContent(buildDetailView(c));
            show(); // refresh card highlights
        });
        return card;
    }

    private VBox buildPlaceholder() {
        VBox b = new VBox();
        b.setAlignment(Pos.CENTER);
        b.setPadding(new Insets(80));
        Label l = new Label("Select a ticket from the left to view details.");
        l.setStyle("-fx-font-size:14px;-fx-text-fill:#94a3b8;");
        b.getChildren().add(l);
        return b;
    }

    // ════════════════════════════════════════════════════════
    //  RIGHT PANEL — ticket detail view
    //
    //  Every field displayed uses a Complaint getter.
    //  Values: complaint_id, category, status, created_at,
    //          student name, student_id, course, description
    //  — all came from the DB JOIN query.
    // ════════════════════════════════════════════════════════
    private VBox buildDetailView(Complaint c) {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color:#f0f4f8;");

        // ── Header section ─────────────────────────────────────
        VBox headerSec = new VBox(10);
        headerSec.setStyle(SECTION_STYLE);

        // Ticket ID badge + status badge — both from DB fields
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label ticketLbl = new Label(c.getTicketId());
        ticketLbl.setStyle("-fx-font-size:12px;-fx-text-fill:#64748b;-fx-font-weight:bold;");
        topRow.getChildren().addAll(ticketLbl, UiHelper.statusBadge(c.getStatus()));

        // Date aligned right — c.getFormattedDate() from DB timestamp
        HBox dateRow = new HBox();
        dateRow.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        VBox dateBox = new VBox(2);
        dateBox.setAlignment(Pos.CENTER_RIGHT);
        Label dLabel = new Label("DATE SUBMITTED");
        dLabel.setStyle("-fx-font-size:10px;-fx-text-fill:#94a3b8;-fx-font-weight:bold;");
        // c.getFormattedDate() — derived from DB created_at column
        Label dValue = new Label(c.getFormattedDate());
        dValue.setStyle("-fx-font-size:17px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");
        // c.getFormattedTime() — derived from DB created_at column
        Label dTime  = new Label(c.getFormattedTime());
        dTime.setStyle("-fx-font-size:12px;-fx-text-fill:#64748b;");
        dateBox.getChildren().addAll(dLabel, dValue, dTime);
        dateRow.getChildren().addAll(topRow, sp, dateBox);

        // Category as large heading — c.getCategory() from DB
        Label catTitle = new Label(c.getCategory());
        catTitle.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");
        Label detailLbl = new Label("Ticket Details");
        detailLbl.setStyle("-fx-font-size:13px;-fx-text-fill:#64748b;");

        headerSec.getChildren().addAll(dateRow, catTitle, detailLbl);

        // ── Student info section ───────────────────────────────
        // student name = c.getStudentName() (JOIN with students table)
        // student id   = c.getStudentId()   (FK from complaints table)
        // course       = c.getStudentCourse() (JOIN with students table)
        HBox infoSec = new HBox(50);
        infoSec.setStyle(SECTION_STYLE);
        infoSec.setAlignment(Pos.CENTER_LEFT);
        infoSec.getChildren().addAll(
            UiHelper.infoColumn("STUDENT NAME",
                c.getStudentName() != null ? c.getStudentName() : "—"),
            UiHelper.infoColumn("STUDENT ID",  c.getStudentId()),
            UiHelper.infoColumn("COURSE",
                c.getStudentCourse() != null ? c.getStudentCourse() : "—")
        );

        // ── Description section ────────────────────────────────
        VBox descSec = new VBox(10);
        descSec.setStyle(SECTION_STYLE);
        Label descHead = new Label("STUDENT DESCRIPTION");
        descHead.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:#94a3b8;");
        VBox descBox = new VBox();
        descBox.setStyle("-fx-background-color:white;-fx-background-radius:8;" +
                         "-fx-border-color:#e2e8f0;-fx-border-radius:8;-fx-padding:14;");
        // c.getDescription() — DB text column
        Label descTxt = new Label(c.getDescription());
        descTxt.setWrapText(true);
        descTxt.setStyle("-fx-font-size:13px;-fx-text-fill:#334155;");
        descBox.getChildren().add(descTxt);
        descSec.getChildren().addAll(descHead, descBox);

        // ── Response form section ──────────────────────────────
        VBox responseSec = buildResponseSection(c);

        panel.getChildren().addAll(headerSec, infoSec, descSec, responseSec);
        return panel;
    }

    /**
     * Builds the admin response form for a complaint.
     *
     * If a response already exists in the DB, its text is pre-filled
     * via response.getResponseText() — never hardcoded.
     */
    private VBox buildResponseSection(Complaint c) {
        VBox sec = new VBox(14);
        sec.setStyle(SECTION_STYLE);

        Label heading = new Label("ADMIN RESPONSE");
        heading.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:#94a3b8;");
        sec.getChildren().add(heading);

        // Check DB for an existing response
        Response existing = controller.getResponse(c.getComplaintId());
        if (existing != null) {
            VBox prevBox = new VBox(4);
            prevBox.setStyle("-fx-background-color:#eff6ff;-fx-background-radius:8;" +
                             "-fx-border-color:#bfdbfe;-fx-border-radius:8;" +
                             "-fx-border-width:1;-fx-padding:12;");
            Label prevLbl = new Label("Previous Response:");
            prevLbl.setStyle("-fx-font-size:11px;-fx-text-fill:#2563eb;-fx-font-weight:bold;");
            // existing.getResponseText() — DB response_text column
            Label prevTxt = new Label(existing.getResponseText());
            prevTxt.setWrapText(true);
            prevTxt.setStyle("-fx-font-size:13px;-fx-text-fill:#1e40af;");
            prevBox.getChildren().addAll(prevLbl, prevTxt);
            sec.getChildren().add(prevBox);
        }

        Label writeLbl = new Label(existing != null ? "Update Response:" : "Write a Response:");
        writeLbl.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#475569;");

        TextArea responseArea = new TextArea();
        responseArea.setPromptText("Type your response to the student here...");
        responseArea.getStyleClass().add("text-area");
        responseArea.setPrefRowCount(4);
        responseArea.setWrapText(true);
        // Pre-fill with DB value if one exists
        if (existing != null) responseArea.setText(existing.getResponseText());

        // Status ComboBox — current value loaded from c.getStatus() (DB)
        HBox statusRow = new HBox(14);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Label statusLbl = new Label("Update Status:");
        statusLbl.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#475569;");
        ComboBox<String> statusCmb = new ComboBox<>();
        statusCmb.getItems().addAll("Pending", "Under Review", "Resolved");
        statusCmb.setValue(c.getStatus()); // pre-selected from DB status value
        statusCmb.getStyleClass().add("input-field");
        statusCmb.setPrefWidth(180);
        statusCmb.setPrefHeight(38);
        statusRow.getChildren().addAll(statusLbl, statusCmb);

        Label msgLbl = new Label();
        msgLbl.setWrapText(true);
        msgLbl.setVisible(false);

        Button saveBtn = new Button("Submit Response & Update Status");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            String status = statusCmb.getValue() != null ? statusCmb.getValue() : "";
            // Sends user-entered text + DB-sourced IDs to controller → DAO → DB
            String err = controller.respondToComplaint(
                c.getComplaintId(),  // from DB
                admin.getAdminId(),  // from DB
                responseArea.getText(),
                status
            );
            if (err == null) {
                msgLbl.setText("\u2713  Response saved and status updated!");
                msgLbl.setStyle("-fx-text-fill:#16a34a;-fx-font-weight:bold;");
                show(); // re-query DB and refresh the whole dashboard
            } else {
                msgLbl.setText(err);
                msgLbl.setStyle("-fx-text-fill:#dc2626;");
                msgLbl.setVisible(true);
            }
        });

        sec.getChildren().addAll(writeLbl, responseArea, statusRow, msgLbl, saveBtn);
        return sec;
    }

    // ── Style constants ──────────────────────────────────────
    private static final String SECTION_STYLE =
        "-fx-background-color:white;" +
        "-fx-border-color:#e2e8f0;-fx-border-width:0 0 1 0;" +
        "-fx-padding:22 28 22 28;";

    private static final String NORMAL_CARD =
        "-fx-background-color:white;" +
        "-fx-border-color:#e2e8f0;-fx-border-width:0 0 1 0;-fx-cursor:hand;";

    private static final String SELECTED_CARD =
        "-fx-background-color:#eff6ff;" +
        "-fx-border-color:#2563eb;-fx-border-width:0 0 1 4;-fx-cursor:hand;";
}
