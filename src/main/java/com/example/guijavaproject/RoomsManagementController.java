package com.example.guijavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class RoomsManagementController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private Button reportButton;
    @FXML private Label dateLabel;

    @FXML private Label AddRoomLabel;
    @FXML private Label ModifyRoomLabel;

    @FXML private TextField addBedsNumber;
    @FXML private TextField addStatus;


    @FXML private TextField modifyRoomId;
    @FXML private TextField modifyBedsNumber;
    @FXML private TextField modifyStatus;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
            brandingImageView.setImage(brandingImage);
        }
        catch (Exception e) {
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

        RoomStatusUpdater.updateRoomStatus();
    }

    @FXML
    private void addRoomButtonAction(ActionEvent event) {
        AddRoomLabel.setText("");

        if (addBedsNumber.getText() == null || addStatus.getText() == null) {
            AddRoomLabel.setText("Please select dates!");
            return;
        }

        String bedsNumber = addBedsNumber.getText().trim();
        String status = addStatus.getText().trim();

        if (bedsNumber.isEmpty() || status.isEmpty()) {
            AddRoomLabel.setText("All fields are required!");
            return;
        }

        if (!bedsNumber.matches("\\d+")) {
            AddRoomLabel.setText("beds number must be numeric!");
            return;
        }

        if (!status.equals("D") && !status.equals("I") && !status.equals("R")) {
            AddRoomLabel.setText("Invalid status. Allowed values are 'A', 'O', or 'C'!");
            return;
        }

        saveRoomToDatabase(bedsNumber, status);
    }

    @FXML
    public void modifyRoomCapacityButtonAction() {
        ModifyRoomLabel.setText("");

        if (modifyRoomId.getText() == null || modifyBedsNumber.getText() == null) {
            ModifyRoomLabel.setText("Please enter the Room ID and the number of beds!");
            return;
        }

        String roomId = modifyRoomId.getText().trim();
        String bedsNumber = modifyBedsNumber.getText().trim();

        if (bedsNumber.isEmpty() || roomId.isEmpty()) {
            ModifyRoomLabel.setText("All fields are required!");
            return;
        }

        if (!bedsNumber.matches("\\d+") || !roomId.matches("\\d+")) {
            ModifyRoomLabel.setText("Room ID and beds number must be numeric!");
            return;
        }

        updateRoomCapacityInDatabase();
    }

    @FXML
    public void modifyRoomStatusButtonAction() {
        ModifyRoomLabel.setText("");

        if (modifyRoomId.getText() == null || modifyBedsNumber.getText() == null) {
            ModifyRoomLabel.setText("Please enter the Room ID and the number of beds!");
            return;
        }

        String roomId = modifyRoomId.getText().trim();
        String roomStatus = modifyStatus.getText().trim();

        if (roomStatus.isEmpty() || roomId.isEmpty()) {
            ModifyRoomLabel.setText("All fields are required!");
            return;
        }

        if (!roomId.matches("\\d+")) {
            ModifyRoomLabel.setText("Room ID must be numeric!");
            return;
        }

        if (!roomStatus.equals("D") && !roomStatus.equals("I") && !roomStatus.equals("R")) {
            ModifyRoomLabel.setText("Invalid status. Allowed values are 'A', 'O', or 'C'!");
            return;
        }

        updateRoomStatusInDatabase();
    }

    private void saveRoomToDatabase(String bedsNumber, String status) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String insertReservation = "INSERT INTO chambre (etat, capacité) VALUES ('"
                + status + "', '" + bedsNumber + "')";

        try {
            Statement statement = connectDB.createStatement();
            int rowsAffected = statement.executeUpdate(insertReservation);

            if (rowsAffected > 0) {
                ModifyRoomLabel.setText("Room added successfully!");
            } else {
                ModifyRoomLabel.setText("Failed to add room.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRoomCapacityInDatabase() {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String idchambre = modifyRoomId.getText().trim();
        String numBeds = modifyBedsNumber.getText().trim();

        try {
            Statement statement = connectDB.createStatement();
            String verifyQuery = "SELECT COUNT(1) FROM chambre WHERE idchambre = '" + idchambre + "'";

            ResultSet resultSet = statement.executeQuery(verifyQuery);

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                String updateQuery = "UPDATE chambre SET capacité = '" + numBeds + "' WHERE idchambre = '" + idchambre + "'";

                int rowsAffected =  statement.executeUpdate(updateQuery);

                if (rowsAffected > 0) {
                    ModifyRoomLabel.setText("Room capacity updated successfully!");
                }
                else {
                    ModifyRoomLabel.setText("Error: Room ID not found.");
                }
            }
            else {
                ModifyRoomLabel.setText("Room not found!");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateRoomStatusInDatabase() {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String idchambre = modifyRoomId.getText().trim();
        String status = modifyStatus.getText().trim();

        try {
            Statement statement = connectDB.createStatement();
            String verifyQuery = "SELECT COUNT(1) FROM chambre WHERE idchambre = '" + idchambre + "'";

            ResultSet resultSet = statement.executeQuery(verifyQuery);

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                String updateQuery = "UPDATE chambre SET etat = '" + status + "' WHERE idchambre = '" + idchambre + "'";

                int rowsAffected =  statement.executeUpdate(updateQuery);

                if (rowsAffected > 0) {
                    ModifyRoomLabel.setText("Room status updated successfully!");
                }
                else {
                    ModifyRoomLabel.setText("Error: Room ID not found.");
                }
            }
            else {
                ModifyRoomLabel.setText("Room not found!");
            }

        }
        catch (SQLException e) {
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

    @FXML
    public void ReportButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "ReportPage.fxml");
    }
}
