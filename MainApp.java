package com.college.cms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * MainApp – JavaFX entry point.
 *
 * On startup:
 *   1. Verifies the MySQL JDBC connection can be opened.
 *   2. If the connection fails, shows a clear error dialog
 *      explaining what to fix, then exits.
 *   3. If the connection succeeds, opens the LoginScreen.
 *
 * On exit (stop()):
 *   The JDBC connection is closed cleanly.
 *
 * ─── HOW TO RUN IN ECLIPSE ───────────────────────────────────
 *
 * Prerequisites:
 *   • Java 17+
 *   • JavaFX SDK 17+  (https://openjfx.io)
 *   • mysql-connector-java-8.x.x.jar
 *   • MySQL Server running with cms_db created via schema.sql
 *
 * Step 1 – Run schema.sql in MySQL Workbench.
 *
 * Step 2 – In DBConnection.java set DB_PASSWORD to your MySQL password.
 *
 * Step 3 – Build Path → Add External JARs:
 *   • All JARs from javafx-sdk/lib/
 *   • mysql-connector-java-8.x.x.jar
 *
 * Step 4 – Run Configurations → Arguments → VM arguments:
 *   --module-path "/path/to/javafx-sdk/lib"
 *   --add-modules javafx.controls,javafx.fxml
 *
 * Step 5 – Copy style.css into the src/ root folder.
 *
 * Step 6 – Right-click MainApp.java → Run As → Java Application.
 *
 * ─── DEMO ADMIN (seeded by schema.sql) ───────────────────────
 *   Email:    admin@university.edu
 *   Password: admin123
 *
 * Register new student accounts via the "Create an account" link
 * on the Login screen.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Verify DB connection before showing any UI
        try {
            DBConnection.getInstance();
        } catch (Exception ex) {
            showDbError(ex.getMessage());
            return;
        }
        new LoginScreen(primaryStage).show();
    }

    @Override
    public void stop() {
        try {
            DBConnection.getInstance().close();
        } catch (Exception ignored) {}
    }

    private void showDbError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Connection Failed");
        alert.setHeaderText("Cannot connect to MySQL");
        alert.setContentText(
            message + "\n\n" +
            "To fix:\n" +
            "  1. Make sure MySQL Server is running.\n" +
            "  2. Open DBConnection.java and set DB_PASSWORD.\n" +
            "  3. Run schema.sql in MySQL Workbench to create cms_db.\n" +
            "  4. Confirm mysql-connector-java.jar is on the build path."
        );
        alert.showAndWait();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
