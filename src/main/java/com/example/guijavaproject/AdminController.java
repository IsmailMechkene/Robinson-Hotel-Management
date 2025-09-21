package com.example.guijavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AdminController implements Initializable{

    @FXML private ImageView brandingImageView;
    @FXML private Label dateLabel;

    @FXML private Label AddMessageLabel;
    @FXML private Label DeleteMessageLabel;

    @FXML private TextField usernameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField typeTextField;

    @FXML private  TextField deleteUsernameTextField;
    @FXML private CheckBox deleteConfirmationCheckbox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
            brandingImageView.setImage(brandingImage);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        dateLabel.setText(formattedDate);
    }

    @FXML
    private void addUserButtonAction() {

        String username = usernameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();
        String type = typeTextField.getText().toUpperCase();


        if (username.isEmpty()) {
            AddMessageLabel.setText("Username cannot be empty.");
            usernameTextField.requestFocus();
            return;
        }
        if (username.length() < 4 || username.length() > 20) {
            AddMessageLabel.setText("Username must be 4-20 characters.");
            usernameTextField.requestFocus();
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            AddMessageLabel.setText("Username: only letters, numbers and underscores.");
            usernameTextField.requestFocus();
            return;
        }

        if (usernameTextField.getText().isEmpty() ||
                emailTextField.getText().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                typeTextField.getText().isEmpty()) {

            AddMessageLabel.setText("Error: All fields are required!");
            return;
        }

        String emailRegex = "^[\\w+&*-]+(?:\\.[\\w+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email.isEmpty()) {
            AddMessageLabel.setText("Email cannot be empty.");
            emailTextField.requestFocus();
            return;
        }
        if (!Pattern.matches(emailRegex, email)) {
            AddMessageLabel.setText("Please enter a valid email address.");
            emailTextField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            AddMessageLabel.setText("Password cannot be empty.");
            passwordTextField.requestFocus();
            return;
        }
        if (password.length() < 8) {
            AddMessageLabel.setText("Password must be at least 8 characters.");
            passwordTextField.requestFocus();
            return;
        }

        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            AddMessageLabel.setText("Password must contain: digit, lowercase, uppercase, and special character.");
            passwordTextField.requestFocus();
            return;
        }

        if (!type.equals("U") && !type.equals("R")) {
            AddMessageLabel.setText("Error: Type must be 'U' or 'R'!");
            return;
        }

        addUserToDataBase();
    }

    private void addUserToDataBase() {
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {
            Statement checkStatement = connectDB.createStatement();


            String checkQuery = "SELECT username, email FROM user WHERE username = '" +
                    usernameTextField.getText().trim() + "' OR email = '" +
                    emailTextField.getText().trim() + "'";
            ResultSet checkResult = checkStatement.executeQuery(checkQuery);

            if (checkResult.next()) {
                if (checkResult.getString("email").equals(emailTextField.getText().trim())) {
                    AddMessageLabel.setText("Email already exists.");
                }

                if (checkResult.getString("username").equals(usernameTextField.getText().trim())) {
                    AddMessageLabel.setText("Username already exists.");
                }

                checkResult.close();
                checkStatement.close();
                return;
            }
            checkResult.close();
            checkStatement.close();

            Statement registerStatement = connectDB.createStatement();
            String registerUserQuery = "INSERT INTO user (username, password, email, role) VALUES ('" +
                    usernameTextField.getText().trim() + "', '" +
                    passwordTextField.getText() + "', '" +
                    emailTextField.getText().trim() + "', '"+
                    typeTextField.getText().trim() +"')";
            registerStatement.executeUpdate(registerUserQuery);


            registerStatement.close();
            AddMessageLabel.setText("Account created successfully");


        } catch (Exception e) {
            AddMessageLabel.setText("An error occurred. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteUserButtonAction() {
        if (deleteUsernameTextField.getText().isEmpty()) {
            DeleteMessageLabel.setText("Error: Username is required!");
            return;
        }

        if (!deleteConfirmationCheckbox.isSelected()) {
            DeleteMessageLabel.setText("Error: Please confirm deletion!");
            return;
        }

        deleteUserFromDataBase();
    }

    private void deleteUserFromDataBase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String deleteQuery = "DELETE FROM user WHERE username = '" + deleteUsernameTextField.getText().trim() + "'";

        try {
            Statement statement = connectDB.createStatement();
            int rowsAffected = statement.executeUpdate(deleteQuery);

            if (rowsAffected > 0) {
                DeleteMessageLabel.setText("User deleted successfully!");
            } else {
                DeleteMessageLabel.setText("No matching user found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void LogoutButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "LoginPage.fxml");
    }

    @FXML
    public void DashboardButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "Dashboard.fxml");
    }

    @FXML
    public void ReservationsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "Reservations.fxml");
    }

    @FXML
    public void ClientsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "Clients.fxml");
    }

    @FXML
    public void RoomsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "Rooms.fxml");
    }
}
