package com.example.guijavaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Button cancelButton;
    @FXML private Label LoginMessageLabel;
    @FXML private ImageView brandingImageView;
    @FXML private ImageView loginImageView;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordTextField;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
            brandingImageView.setImage(brandingImage);

            Image logingImage = new Image(getClass().getResourceAsStream("/Images/LoginLogo.png"));
            loginImageView.setImage(logingImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RoomStatusUpdater.updateRoomStatus();
    }

    public void LoginButtonAction(ActionEvent event) {
        if (usernameTextField.getText().isBlank() || passwordTextField.getText().isBlank()) {
            LoginMessageLabel.setText("Username and Password can not be empty");
        }
        else {
            validateLogin(event);
        }
    }

    public void CancelButtonAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }

    public void validateLogin(ActionEvent event) {

        if (usernameTextField.getText().equals("root") && passwordTextField.getText().equals("i00mechkene00")) {
            try {
                switchToAdmin(event);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM User WHERE username = '" + usernameTextField.getText() + "' AND password = '" + passwordTextField.getText() +"'";
        String insertQuery = "INSERT INTO logs (username, date_login) VALUES ('" + usernameTextField.getText().trim() + "', CURDATE())";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    Statement insertStatement = connectDB.createStatement();
                    insertStatement.executeUpdate(insertQuery);
                    insertStatement.close();

                    switchToDashboard(event);
                }
                else {
                    LoginMessageLabel.setText("Invalid username or password :(");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void switchToSignupPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("RegisterPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();
    }

    public void switchToDashboard(ActionEvent event) {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        ResultSet queryResult = null;
        Statement statement = null;
        String role = null;

        try {
            String verifyLogin = "SELECT role FROM user WHERE username = '" + usernameTextField.getText() + "'";
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                role = queryResult.getString(1);
            } else {
                LoginMessageLabel.setText("Invalid username or password :(");
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            try {
                if (queryResult != null) queryResult.close();
                if (statement != null) statement.close();
                if (connectDB != null) connectDB.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ("R".equals(role)) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setResizable(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ("U".equals(role)) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("C_reservations.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setResizable(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void switchToAdmin(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Admin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();
    }
}
