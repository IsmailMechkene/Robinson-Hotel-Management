package com.example.guijavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class C_ReportPageController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private ComboBox<String> issueType;
    @FXML private Button reportButton;
    @FXML private TextArea descriptionField;
    @FXML private Label dateLabel;
    @FXML private Label resultLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
            brandingImageView.setImage(brandingImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        reportButton.setOnMouseEntered(e -> {
            reportButton.setStyle("-fx-background-color: #6C5B7B; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 200px;");
        });

        reportButton.setOnMouseExited(e -> {
            reportButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 200px;");
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        dateLabel.setText(formattedDate);
    }

    @FXML
    public void sendReport(ActionEvent event) {
        String issueCategory = issueType.getValue();
        String issueDescription = descriptionField.getText();

        if (issueCategory == null || issueCategory.isEmpty()) {
            System.out.println("Please select an issue category.");
            return;
        }
        if (issueDescription == null || issueDescription.isEmpty()) {
            System.out.println("Please enter a description of the issue.");
            return;
        }

        sendEmail(issueCategory, issueDescription);
    }

    private void sendEmail(String issueCategory, String issueDescription) {
        final String senderEmail = "#";
        final String sendGridApiKey = "#";
        final String recipientEmail = "#";
        final String replyToEmail = "#";

        try {
            URL url = new URL("https://api.sendgrid.com/v3/mail/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + sendGridApiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String emailJson = "{"
                    + "\"personalizations\":[{\"to\":[{\"email\":\"" + recipientEmail + "\"}]}],"
                    + "\"from\":{\"email\":\"" + senderEmail + "\"},"
                    + "\"reply_to\":{\"email\":\"" + replyToEmail + "\"},"
                    + "\"subject\":\"New Report Submitted - " + issueCategory + "\","
                    + "\"content\":[{\"type\":\"text/plain\",\"value\":\"Issue Category: " + issueCategory + "\\n\\nDescription:\\n" + issueDescription + "\"}]"
                    + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = emailJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 202) {
                resultLabel.setText("Email sent successfully!");

            } else {
                resultLabel.setText("Failed to send email.");

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
    public void ReservationsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "C_Reservations.fxml");
    }

    @FXML
    public void ReservationManagementsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "C_ReservationsManagement.fxml");
    }

    @FXML
    public void RoomsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "C_Rooms.fxml");
    }

    @FXML
    public void ReportButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "ReportPage.fxml");
    }
}
