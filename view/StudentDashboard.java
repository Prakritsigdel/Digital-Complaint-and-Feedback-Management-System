package com.college.cms.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

import com.college.cms.controller.AppController;
import com.college.cms.model.Complaint;
import com.college.cms.model.Student;
import com.college.cms.util.UiHelper;

public class StudentDashboard {

    private final Stage         stage;
    private final Student       student;    // populated from DB at login
    private final AppController controller = new AppController();

    private BorderPane root;
    private Button     submitNavBtn;
    private Button     historyNavBtn;

    public StudentDashboard(Stage stage, Student student) {
        this.stage   = stage;
        this.student = student; // object whose fields all came from a DB SELECT
    }

    public void show() {
        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(buildSubmitView());   // default view

        Scene scene = new Scene(root, 1140, 700);
        LoginScreen.applyStyles(scene);
        // Window title uses student name from DB
        stage.setTitle("Student Portal – " + student.getName());
        stage.setScene(scene);
        stage.show();
    }

    // ════════════════════════════════════════════════════════
    //  SIDEBAR  –  all text comes from the student model object
    // ════════════════════════════════════════════════════════
    private VBox buildSidebar() {
        VBox bar = new VBox();
        bar.getStyleClass().add("sidebar");
        bar.setPrefWidth(250);

        // Avatar initials derived from DB field student.getName()
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar");
        avatar.setPrefSize(48, 48);
        avatar.setMinSize(48, 48);
        Label initials = new Label(student.getInitials()); // computed from DB name
        initials.getStyleClass().add("avatar-label");
        avatar.getChildren().add(initials);

        VBox nameBox = new VBox(2);
        // Greeting uses student.getFirstName() – value from DB
        Label nameLbl = new Label("Welcome, " + student.getFirstName());
        nameLbl.getStyleClass().add("user-name");
        Label roleLbl = new Label("Student Portal");
        roleLbl.getStyleClass().add("user-role");
        nameBox.getChildren().addAll(nameLbl, roleLbl);

        HBox userRow = new HBox(12);
        userRow.setAlignment(Pos.CENTER_LEFT);
        userRow.setPadding(new Insets(14, 0, 14, 0));
        userRow.getChildren().addAll(avatar, nameBox);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:rgba(255,255,255,.2);");

        VBox nav = new VBox(4);
        nav.setPadding(new Insets(10, 0, 0, 0));

        submitNavBtn  = navBtn("\u27A4   Submit New Complaint", true);
        historyNavBtn = navBtn("\u29BF   My History",           false);

        submitNavBtn.setOnAction(e -> {
            setActive(submitNavBtn, historyNavBtn);
            root.setCenter(buildSubmitView());
        });
        historyNavBtn.setOnAction(e -> {
            setActive(historyNavBtn, submitNavBtn);
            root.setCenter(buildHistoryView());
        });

        nav.getChildren().addAll(submitNavBtn, historyNavBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("\u2192   Logout");
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> new LoginScreen(stage).show());

        bar.getChildren().addAll(userRow, sep, nav, spacer, logoutBtn);
        return bar;
    }

    private Button navBtn(String text, boolean active) {
        Button b = new Button(text);
        b.getStyleClass().addAll("nav-btn", active ? "nav-btn-active" : "");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private void setActive(Button on, Button off) {
        on.getStyleClass().add("nav-btn-active");
        off.getStyleClass().remove("nav-btn-active");
    }

    // ════════════════════════════════════════════════════════
    //  VIEW 1: SUBMIT COMPLAINT
    // ════════════════════════════════════════════════════════
    private Node buildSubmitView() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f0f4f8;-fx-background-color:#f0f4f8;");

        HBox content = new HBox(24);
        content.setPadding(new Insets(36));
        content.setStyle("-fx-background-color:#f0f4f8;");

        // ── Left column: form ──────────────────────────────
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label pageTitle = new Label("File a Complaint");
        pageTitle.getStyleClass().add("page-title");
        Label pageSub   = new Label("Submit your academic or administrative concerns");
        pageSub.getStyleClass().add("page-subtitle");

        // Form card
        VBox form = new VBox(18);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(26));

        // Category selector — options are static UI chrome, not data
        VBox catGrp = new VBox(8);
        Label catLbl = new Label("Category");
        catLbl.getStyleClass().add("field-label");
        ComboBox<String> catCmb = new ComboBox<>();
        catCmb.setPromptText("Select a category");
        catCmb.getStyleClass().add("input-field");
        catCmb.setMaxWidth(Double.MAX_VALUE);
        catCmb.setPrefHeight(42);
        catCmb.getItems().addAll(
            "Academic", "Infrastructure", "IT Support",
            "Financial", "Health & Safety", "Other"
        );
        catGrp.getChildren().addAll(catLbl, catCmb);

        // Description text area — value entered by user at runtime
        VBox descGrp = new VBox(8);
        Label descLbl = new Label("Description");
        descLbl.getStyleClass().add("field-label");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Please describe your issue in detail...");
        descArea.getStyleClass().add("text-area");
        descArea.setPrefRowCount(10);
        descArea.setWrapText(true);
        descGrp.getChildren().addAll(descLbl, descArea);

        Label msgLbl = new Label();
        msgLbl.setWrapText(true);
        msgLbl.setVisible(false);

        Button submitBtn = new Button("Submit Ticket");
        submitBtn.getStyleClass().add("btn-primary");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setOnAction(e -> {
            // student.getStudentId() — value from DB
            // catCmb.getValue() and descArea.getText() — user-entered at runtime
            String cat = catCmb.getValue() != null ? catCmb.getValue() : "";
            String err = controller.submitComplaint(
                student.getStudentId(), cat, descArea.getText()
            );
            if (err == null) {
                msgLbl.setText("\u2713  Submitted! Go to \"My History\" to track it.");
                msgLbl.setStyle("-fx-text-fill:#16a34a;-fx-font-weight:bold;");
                catCmb.setValue(null);
                descArea.clear();
            } else {
                msgLbl.setText(err);
                msgLbl.setStyle("-fx-text-fill:#dc2626;");
            }
            msgLbl.setVisible(true);
        });

        form.getChildren().addAll(catGrp, descGrp, msgLbl, submitBtn);
        leftCol.getChildren().addAll(pageTitle, pageSub, form);

        // ── Right column: guidelines ───────────────────────
        VBox guide = buildGuideCard();

        content.getChildren().addAll(leftCol, guide);
        scroll.setContent(content);
        return scroll;
    }

    private VBox buildGuideCard() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setMinWidth(270);
        card.setMaxWidth(300);

        Label title = new Label("Submission Guidelines");
        title.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");

        card.getChildren().addAll(title,
            guideRow("1","Choose the Right Category",
                "Select the category that best matches your concern."),
            guideRow("2","Be Specific and Clear",
                "Include dates, locations, and any relevant details."),
            guideRow("3","Track Your Ticket",
                "Visit \"My History\" to monitor status and see responses.")
        );

        VBox tip = new VBox();
        tip.setStyle("-fx-background-color:#eff6ff;-fx-background-radius:8;" +
                     "-fx-border-color:#bfdbfe;-fx-border-radius:8;" +
                     "-fx-border-width:1;-fx-padding:12;");
        Label tipTxt = new Label(
            "\uD83D\uDCA1 Tip: Most complaints are resolved within 3-5 business days.");
        tipTxt.setStyle("-fx-font-size:12px;-fx-text-fill:#1d4ed8;-fx-font-weight:bold;");
        tipTxt.setWrapText(true);
        tip.getChildren().add(tipTxt);
        card.getChildren().add(tip);
        return card;
    }

    private HBox guideRow(String num, String head, String body) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.TOP_LEFT);
        VBox txt = new VBox(3);
        Label h = new Label(head);
        h.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#1e293b;");
        Label b = new Label(body);
        b.setStyle("-fx-font-size:12px;-fx-text-fill:#64748b;");
        b.setWrapText(true);
        b.setMaxWidth(200);
        txt.getChildren().addAll(h, b);
        row.getChildren().addAll(UiHelper.numberCircle(num), txt);
        return row;
    }

    // ════════════════════════════════════════════════════════
    //  VIEW 2: COMPLAINT HISTORY  (Figma Image 5)
    //
    //  DATA FLOW:
    //    controller.getMyComplaints(student.getStudentId())
    //      → ComplaintDAO.getComplaintsByStudent(studentId)
    //        → SELECT … FROM complaints LEFT JOIN responses WHERE student_id = ?
    //          → List<Complaint>
    //            → each row rendered using complaint.getXxx()
    // ════════════════════════════════════════════════════════
    private Node buildHistoryView() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f0f4f8;-fx-background-color:#f0f4f8;");

        VBox content = new VBox(22);
        content.setPadding(new Insets(36));
        content.setStyle("-fx-background-color:#f0f4f8;");

        Label title = new Label("My Complaint History");
        title.getStyleClass().add("page-title");
        Label sub   = new Label("Track the status of your submitted complaints");
        sub.getStyleClass().add("page-subtitle");

        // Refresh button — re-queries the DB
        Button refreshBtn = new Button("\u21BB  Refresh");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> root.setCenter(buildHistoryView()));

        // Container that holds all complaint rows
        VBox tableBox = new VBox(0);
        tableBox.getStyleClass().add("history-container");

        // Column header row (chrome only — no data)
        HBox colHeader = new HBox();
        colHeader.getStyleClass().add("history-header-row");
        colHeader.setPadding(new Insets(12, 24, 12, 24));
        String[] cols  = {"TICKET ID","DATE SUBMITTED","CATEGORY","STATUS","ACTION"};
        double[] widths = {110, 160, 150, 140, 120};
        for (int i = 0; i < cols.length; i++) {
            Label l = new Label(cols[i]);
            l.getStyleClass().add("col-header-lbl");
            l.setMinWidth(widths[i]);
            colHeader.getChildren().add(l);
        }
        tableBox.getChildren().add(colHeader);

        // Query DB — all rows come from the database
        List<Complaint> complaints =
            controller.getMyComplaints(student.getStudentId());

        if (complaints.isEmpty()) {
            VBox empty = new VBox(8);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(50));
            Label e1 = new Label("No complaints submitted yet.");
            e1.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#475569;");
            Label e2 = new Label("Use \"Submit New Complaint\" to file your first ticket.");
            e2.setStyle("-fx-font-size:12px;-fx-text-fill:#94a3b8;");
            empty.getChildren().addAll(e1, e2);
            tableBox.getChildren().add(empty);
        } else {
            for (Complaint c : complaints) {
                addHistoryRow(tableBox, c, widths);
            }
        }

        content.getChildren().addAll(title, sub, refreshBtn, tableBox);
        scroll.setContent(content);
        return scroll;
    }

    /**
     * Renders one complaint row plus an expandable admin-remarks panel.
     *
     * EVERY value displayed here is read from the Complaint model object,
     * which was populated by a DB query — never hardcoded.
     */
    private void addHistoryRow(VBox container, Complaint c, double[] widths) {
        HBox row = new HBox();
        row.getStyleClass().add("history-row");
        row.setPadding(new Insets(16, 24, 16, 24));
        row.setAlignment(Pos.CENTER_LEFT);

        // Ticket ID — from c.getTicketId()  (derived from DB complaint_id)
        Label idLbl = new Label(c.getTicketId());
        idLbl.getStyleClass().add("ticket-id-lbl");
        idLbl.setMinWidth(widths[0]);

        // Date + time — from c.getFormattedDate() / getFormattedTime()
        //   both derived from the DB created_at timestamp
        VBox dateBox = new VBox(2);
        dateBox.setMinWidth(widths[1]);
        Label dateLbl = new Label(c.getFormattedDate());
        dateLbl.setStyle("-fx-font-size:13px;-fx-text-fill:#475569;");
        Label timeLbl = new Label(c.getFormattedTime());
        timeLbl.setStyle("-fx-font-size:11px;-fx-text-fill:#94a3b8;");
        dateBox.getChildren().addAll(dateLbl, timeLbl);

        // Category — from c.getCategory()  (DB value)
        Label catLbl = new Label(c.getCategory());
        catLbl.setStyle("-fx-font-size:13px;-fx-text-fill:#1e293b;");
        catLbl.setMinWidth(widths[2]);

        // Status badge — colour derived from c.getStatus()  (DB value)
        StackPane statusCell = new StackPane(UiHelper.statusBadge(c.getStatus()));
        statusCell.setAlignment(Pos.CENTER_LEFT);
        statusCell.setMinWidth(widths[3]);

        // Admin response panel (expandable) — from c.getResponseText() (DB value)
        boolean hasResponse = c.getResponseText() != null && !c.getResponseText().isBlank();

        VBox remarksBox = new VBox(8);
        remarksBox.getStyleClass().add("admin-remarks-box");
        remarksBox.setVisible(false);
        remarksBox.setManaged(false);

        if (hasResponse) {
            Label rmkTitle = new Label("ADMIN REMARKS");
            rmkTitle.getStyleClass().add("admin-remarks-title");
            // Response text — from c.getResponseText(), which was set by the DB JOIN
            Label rmkText  = new Label(c.getResponseText());
            rmkText.getStyleClass().add("admin-remarks-text");
            rmkText.setWrapText(true);
            remarksBox.getChildren().addAll(rmkTitle, rmkText);
        }

        // Action button
        Button actionBtn = new Button(hasResponse ? "Show Response" : "No Response Yet");
        actionBtn.getStyleClass().add("action-link");
        actionBtn.setMinWidth(widths[4]);
        if (!hasResponse) {
            actionBtn.setDisable(true);
            actionBtn.setStyle(
                "-fx-text-fill:#94a3b8;-fx-background-color:transparent;-fx-cursor:default;");
        } else {
            actionBtn.setOnAction(e -> {
                boolean show = !remarksBox.isVisible();
                remarksBox.setVisible(show);
                remarksBox.setManaged(show);
                actionBtn.setText(show ? "Hide Response" : "Show Response");
            });
        }

        row.getChildren().addAll(idLbl, dateBox, catLbl, statusCell, actionBtn);
        container.getChildren().addAll(row, remarksBox);
    }
}
