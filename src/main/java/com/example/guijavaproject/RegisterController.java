package com.example.guijavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private ImageView registerImageView;
    @FXML private Label RegisterMessageLabel;
    @FXML private TextField cinTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField phoneTextField;
    @FXML private TextField addressTextField;
    @FXML private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
            brandingImageView.setImage(brandingImage);

            Image registerImage = new Image(getClass().getResourceAsStream("/Images/RegisterLogo.png"));
            registerImageView.setImage(registerImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RoomStatusUpdater.updateRoomStatus();
    }

    public void RegisterButtonAction(ActionEvent event) {
        RegisterMessageLabel.setText("");

        String cin = cinTextField.getText().trim();
        if (cin.isEmpty()) {
            RegisterMessageLabel.setText("CIN cannot be empty.");
            cinTextField.requestFocus();
            return;
        }
        if (!cin.matches("\\d+")) {
            RegisterMessageLabel.setText("CIN must be numeric.");
            cinTextField.requestFocus();
            return;
        }
        try {
            Integer.parseInt(cin);
        } catch (NumberFormatException e) {
            RegisterMessageLabel.setText("CIN value is too large. Please enter a smaller number.");
            cinTextField.requestFocus();
            return;
        }

        String username = usernameTextField.getText().trim();
        String firstName = firstNameTextField.getText().trim();
        String lastName = lastNameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String address = addressTextField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            RegisterMessageLabel.setText("Username cannot be empty.");
            usernameTextField.requestFocus();
            return;
        }
        if (username.length() < 4 || username.length() > 20) {
            RegisterMessageLabel.setText("Username must be 4-20 characters.");
            usernameTextField.requestFocus();
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            RegisterMessageLabel.setText("Username: only letters, numbers and underscores.");
            usernameTextField.requestFocus();
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty()) {
            RegisterMessageLabel.setText("First and last name cannot be empty.");
            firstNameTextField.requestFocus();
            return;
        }
        if (!firstName.matches("^[a-zA-ZÀ-ÿ\\-\\s']+$") || !lastName.matches("^[a-zA-ZÀ-ÿ\\-\\s']+$")) {
            RegisterMessageLabel.setText("Names can only contain letters, hyphens, spaces and apostrophes.");
            firstNameTextField.requestFocus();
            return;
        }

        String emailRegex = "^[\\w+&*-]+(?:\\.[\\w+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email.isEmpty()) {
            RegisterMessageLabel.setText("Email cannot be empty.");
            emailTextField.requestFocus();
            return;
        }
        if (!Pattern.matches(emailRegex, email)) {
            RegisterMessageLabel.setText("Please enter a valid email address.");
            emailTextField.requestFocus();
            return;
        }

        String phoneRegex = "\\d+";
        if (phone.isEmpty()) {
            RegisterMessageLabel.setText("Phone number cannot be empty.");
            phoneTextField.requestFocus();
            return;
        }
        if (!phone.matches(phoneRegex)) {
            RegisterMessageLabel.setText("Please enter a valid numeric phone number.");
            phoneTextField.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            RegisterMessageLabel.setText("Address cannot be empty.");
            addressTextField.requestFocus();
            return;
        }
        if (address.length() < 10) {
            RegisterMessageLabel.setText("Address must be at least 10 characters.");
            addressTextField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            RegisterMessageLabel.setText("Password cannot be empty.");
            passwordField.requestFocus();
            return;
        }
        if (password.length() < 8) {
            RegisterMessageLabel.setText("Password must be at least 8 characters.");
            passwordField.requestFocus();
            return;
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            RegisterMessageLabel.setText("Password must contain: digit, lowercase, uppercase, and special character.");
            passwordField.requestFocus();
            return;
        }

        validateRegister();
    }

    public void validateRegister() {
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {
            Statement checkStatement = connectDB.createStatement();
            // Vérification CIN unique
            String checkCinQuery = "SELECT cin FROM client WHERE cin = '" + cinTextField.getText().trim() + "'";
            ResultSet cinResult = checkStatement.executeQuery(checkCinQuery);
            if (cinResult.next()) {
                RegisterMessageLabel.setText("CIN already exists.");
                cinResult.close();
                checkStatement.close();
                return;
            }
            cinResult.close();

            String checkQuery = "SELECT username, email FROM user WHERE username = '" +
                    usernameTextField.getText().trim() + "' OR email = '" +
                    emailTextField.getText().trim() + "'";
            ResultSet checkResult = checkStatement.executeQuery(checkQuery);

            if (checkResult.next()) {
                if (checkResult.getString("username").equals(usernameTextField.getText().trim())) {
                    RegisterMessageLabel.setText("Username already exists.");
                } else {
                    RegisterMessageLabel.setText("Email already exists.");
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
                    passwordField.getText() + "', '" +
                    emailTextField.getText().trim() + "', 'U')";
            registerStatement.executeUpdate(registerUserQuery);

            String registerClientQuery = "INSERT INTO client (cin, nom, prenom, adresse, tel) VALUES ('" +
                    cinTextField.getText().trim() + "', '" +
                    lastNameTextField.getText().trim() + "', '" +
                    firstNameTextField.getText().trim() + "', '" +
                    addressTextField.getText().trim() + "', '" +
                    phoneTextField.getText().trim() + "')";
            registerStatement.executeUpdate(registerClientQuery);

            registerStatement.close();
            RegisterMessageLabel.setText("Registration successful! Please login.");
            clearForm();

        } catch (Exception e) {
            RegisterMessageLabel.setText("An error occurred. Please try again.");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        cinTextField.setText("");
        usernameTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        emailTextField.setText("");
        phoneTextField.setText("");
        addressTextField.setText("");
        passwordField.setText("");
    }

    public void switchToLoginPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}