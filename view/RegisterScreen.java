package com.college.cms.view;

import com.college.cms.controller.AppController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class RegisterScreen {

    private final Stage         stage;
    private final AppController controller = new AppController();

    public RegisterScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        HBox root = new HBox();
        root.setPrefSize(1100, 680);

        VBox      left  = buildLeftPanel();
        left.setPrefWidth(480);
        ScrollPane right = buildRightPanel();
        HBox.setHgrow(right, Priority.ALWAYS);

        root.getChildren().addAll(left, right);

        Scene scene = new Scene(root, 1100, 680);
        LoginScreen.applyStyles(scene);
        stage.setTitle("University Portal – Student Registration");
        stage.setScene(scene);
        stage.show();
    }

    // ── Left panel ──────────────────────────────────────────
    private VBox buildLeftPanel() {
        VBox panel = new VBox(22);
        panel.setId("left-panel");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 44, 60, 44));

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("icon-box");
        Label icon = new Label("\uD83D\uDEE1");
        icon.setStyle("-fx-font-size: 34px;");
        iconBox.getChildren().add(icon);

        Label title = new Label("Join Our Community");
        title.getStyleClass().add("portal-title");

        Label sub = new Label(
            "Create your account and start managing\nyour academic concerns efficiently");
        sub.getStyleClass().add("portal-subtitle");
        sub.setTextAlignment(TextAlignment.CENTER);

        VBox benefits = new VBox(12);
        benefits.getStyleClass().add("benefits-box");
        benefits.setMaxWidth(360);
        Label bt = new Label("What you'll get:");
        bt.setStyle("-fx-font-weight:bold;-fx-text-fill:white;-fx-font-size:14px;");
        benefits.getChildren().addAll(bt,
            bItem("Submit and track complaints 24/7"),
            bItem("Real-time status updates"),
            bItem("Direct communication with administrators")
        );

        panel.getChildren().addAll(iconBox, title, sub, benefits);
        return panel;
    }

    private Label bItem(String text) {
        Label l = new Label("\u2713   " + text);
        l.setStyle("-fx-text-fill:rgba(255,255,255,.85);-fx-font-size:13px;");
        return l;
    }

    // ── Right panel ─────────────────────────────────────────
    private ScrollPane buildRightPanel() {
        ScrollPane scroll = new ScrollPane();
        scroll.setId("right-panel");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f1f5f9;-fx-background-color:#f1f5f9;");

        StackPane wrapper = new StackPane();
        wrapper.setStyle("-fx-background-color:#f1f5f9;");
        wrapper.setPadding(new Insets(30, 40, 50, 40));

        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(32, 42, 42, 42));
        card.setMaxWidth(430);

        Hyperlink back = new Hyperlink("\u2190  Back to Login");
        back.getStyleClass().add("link");
        back.setOnAction(e -> new LoginScreen(stage).show());

        Label heading = new Label("Student Registration");
        heading.getStyleClass().add("card-title");
        Label subhead = new Label("Fill in your details to create an account");
        subhead.getStyleClass().add("card-subtitle");

        // Form fields — all values entered by the user at runtime, never hardcoded
        TextField      idFld     = inputFld("Your Student ID  e.g. STU2024001");
        TextField      nameFld   = inputFld("Full name");
        ComboBox<String> courseCmb = coursesCombo();
        TextField      emailFld  = inputFld("email@university.edu");
        PasswordField  passFld   = new PasswordField();
        passFld.setPromptText("Minimum 6 characters");
        passFld.getStyleClass().add("input-field");

        Label msgLbl = new Label();
        msgLbl.setWrapText(true);
        msgLbl.setVisible(false);

        Button regBtn = new Button("Register");
        regBtn.getStyleClass().add("btn-primary");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setOnAction(e -> {
            String course = courseCmb.getValue() != null ? courseCmb.getValue() : "";
            // Values come from user input → validated in controller → inserted into DB
            String err = controller.register(
                idFld.getText().trim(),
                nameFld.getText().trim(),
                course,
                emailFld.getText().trim(),
                passFld.getText()
            );
            if (err == null) {
                msgLbl.setText("\u2713  Account created! You can now log in.");
                msgLbl.setStyle("-fx-text-fill:#16a34a;-fx-font-weight:bold;");
                idFld.clear(); nameFld.clear(); emailFld.clear(); passFld.clear();
                courseCmb.setValue(null);
            } else {
                msgLbl.setText(err);
                msgLbl.setStyle("-fx-text-fill:#dc2626;");
            }
            msgLbl.setVisible(true);
        });

        card.getChildren().addAll(
            back, heading, subhead,
            lRow("Student ID / Number", idFld),
            lRow("Full Name",           nameFld),
            lRow("Course / Department", courseCmb),
            lRow("Email Address",       emailFld),
            lRow("Create Password",     passFld),
            msgLbl, regBtn
        );
        wrapper.getChildren().add(card);
        scroll.setContent(wrapper);
        return scroll;
    }

    // ── Helpers ──────────────────────────────────────────────
    private TextField inputFld(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("input-field");
        return tf;
    }

    private VBox lRow(String label, Control ctrl) {
        VBox g = new VBox(7);
        Label l = new Label(label);
        l.getStyleClass().add("field-label");
        g.getChildren().addAll(l, ctrl);
        return g;
    }

    private ComboBox<String> coursesCombo() {
        ComboBox<String> c = new ComboBox<>();
        c.setPromptText("Select your course");
        c.getStyleClass().add("input-field");
        c.setMaxWidth(Double.MAX_VALUE);
        c.setPrefHeight(40);
        c.getItems().addAll(
            "Computer Science", "Engineering", "Business Administration",
            "Medicine", "Law", "Arts & Humanities",
            "Natural Sciences", "Education", "Architecture", "Other"
        );
        return c;
    }
}
