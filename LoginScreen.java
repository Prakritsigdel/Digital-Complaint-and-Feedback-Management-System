package com.college.cms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * LoginScreen – Two-panel layout (Figma Image 1).
 *
 * UI RULE ENFORCED:
 *   This screen never displays any hardcoded user or complaint data.
 *   The only static strings here are UI chrome (labels, placeholders, hints)
 *   — not application data.  All data retrieved from the DB flows through
 *   AppController.login() → model object → appropriate dashboard.
 */
public class LoginScreen {

    private final Stage         stage;
    private final AppController controller = new AppController();

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        HBox root = new HBox();
        root.setPrefSize(1100, 680);

        VBox     left  = buildLeftPanel();
        left.setPrefWidth(480);
        StackPane right = buildRightPanel();
        HBox.setHgrow(right, Priority.ALWAYS);

        root.getChildren().addAll(left, right);

        Scene scene = new Scene(root, 1100, 680);
        applyStyles(scene);

        stage.setTitle("University Portal – Login");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    // ── Left branding panel ─────────────────────────────────
    private VBox buildLeftPanel() {
        VBox panel = new VBox(22);
        panel.setId("left-panel");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 44, 60, 44));

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("icon-box");
        Label icon = new Label("\uD83C\uDF93");
        icon.setStyle("-fx-font-size: 36px;");
        iconBox.getChildren().add(icon);

        Label title = new Label("University Portal");
        title.getStyleClass().add("portal-title");

        Label subtitle = new Label("Digital Complaint & Feedback\nManagement System");
        subtitle.getStyleClass().add("portal-subtitle");
        subtitle.setTextAlignment(TextAlignment.CENTER);

        HBox stats = new HBox(28);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
            statBox("24/7", "Available"),
            new Label("|") {{
                setStyle("-fx-font-size:24px;-fx-text-fill:rgba(255,255,255,.28);"); }},
            statBox("Fast", "Response")
        );

        panel.getChildren().addAll(iconBox, title, subtitle, stats);
        return panel;
    }

    private VBox statBox(String val, String lbl) {
        VBox b = new VBox(3);
        b.setAlignment(Pos.CENTER);
        Label v = new Label(val);
        v.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:11px;-fx-text-fill:rgba(255,255,255,.70);");
        b.getChildren().addAll(v, l);
        return b;
    }

    // ── Right login-form panel ──────────────────────────────
    private StackPane buildRightPanel() {
        StackPane panel = new StackPane();
        panel.setId("right-panel");

        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(42, 46, 42, 46));
        card.setMaxWidth(410);

        Label heading  = new Label("Login");
        heading.getStyleClass().add("card-title");
        Label subhead  = new Label("Enter your credentials to continue");
        subhead.getStyleClass().add("card-subtitle");

        // Email field
        VBox emailGrp = fieldGroup("Email Address");
        TextField emailFld = new TextField();
        emailFld.setPromptText("your.email@university.edu");
        emailFld.getStyleClass().add("input-field");
        emailGrp.getChildren().add(emailFld);

        // Password field
        VBox passGrp = fieldGroup("Password");
        PasswordField passFld = new PasswordField();
        passFld.setPromptText("Enter your password");
        passFld.getStyleClass().add("input-field");
        passGrp.getChildren().add(passFld);

        // Error label
        Label errLbl = new Label();
        errLbl.getStyleClass().add("error-label");
        errLbl.setVisible(false);
        errLbl.setWrapText(true);

        // Login button
        Button loginBtn = new Button("Secure Login");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Runnable doLogin = () -> handleLogin(
            emailFld.getText(), passFld.getText(), errLbl
        );
        loginBtn.setOnAction(e -> doLogin.run());
        passFld.setOnAction(e  -> doLogin.run());

        Separator sep = new Separator();

        VBox regRow = new VBox(8);
        regRow.setAlignment(Pos.CENTER);
        Label newUser = new Label("New to the platform?");
        newUser.getStyleClass().add("hint-text");
        Hyperlink regLink = new Hyperlink("Create an account");
        regLink.getStyleClass().add("link");
        regLink.setOnAction(e -> new RegisterScreen(stage).show());
        regRow.getChildren().addAll(newUser, regLink);

        // Demo hint — shows the seeded admin account from schema.sql
        Label demo = new Label("Admin demo: admin@university.edu / admin123");
        demo.getStyleClass().add("demo-text");

        card.getChildren().addAll(
            heading, subhead,
            emailGrp, passGrp,
            errLbl, loginBtn,
            sep, regRow, demo
        );
        panel.getChildren().add(card);
        return panel;
    }

    // ── Login handler ───────────────────────────────────────
    // Calls controller → DAO → DB.  Data flows back as a model object.
    private void handleLogin(String email, String pass, Label errLbl) {
        if (email.isBlank()) { showErr(errLbl, "Please enter your email address."); return; }
        if (pass.isBlank())  { showErr(errLbl, "Please enter your password.");      return; }

        User user = controller.login(email, pass);
        if (user == null) {
            showErr(errLbl, "Incorrect email or password. Please try again.");
            return;
        }
        errLbl.setVisible(false);

        // Route to the correct dashboard — passing the model object (not raw strings)
        if (user instanceof Admin admin) {
            new AdminDashboard(stage, admin).show();
        } else {
            new StudentDashboard(stage, (Student) user).show();
        }
    }

    private void showErr(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
    }

    // ── Helpers ──────────────────────────────────────────────
    private VBox fieldGroup(String labelText) {
        VBox g = new VBox(7);
        Label l = new Label(labelText);
        l.getStyleClass().add("field-label");
        g.getChildren().add(l);
        return g;
    }

    // Stylesheet loader — reused by all screens via static call
    static void applyStyles(Scene scene) {
        java.net.URL css = LoginScreen.class.getResource("/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        else System.err.println("[UI] style.css not found — copy it to src/ root.");
    }
}
