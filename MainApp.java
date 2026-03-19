package com.college.cms;

import com.college.cms.model.DBConnection;
import com.college.cms.view.LoginScreen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

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
