package com.example.guijavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class C_ReservationsManagementController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private Button reportButton;
    @FXML private Label dateLabel;

    @FXML private Label AddMessageLabel;
    @FXML private Label ModifyMessageLabel;
    @FXML private Label DeleteMessageLabel;

    @FXML private DatePicker addCheckIn;
    @FXML private DatePicker addCheckOut;
    @FXML private TextField addClientCin;
    @FXML private TextField addRoomId;

    @FXML private TextField modifyReservationId;
    @FXML private DatePicker modifyCheckIn;
    @FXML private DatePicker modifyCheckOut;
    @FXML private TextField modifyClientCin;
    @FXML private TextField modifyRoomId;

    @FXML private TextField deleteReservationId;
    @FXML private TextField deleteClientCin;
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

        reportButton.setOnMouseEntered(e -> {
            reportButton.setStyle("-fx-background-color: #6C5B7B; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 200px;");
        });

        reportButton.setOnMouseExited(e -> {
            reportButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 200px;");
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        dateLabel.setText(formattedDate);

        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            java.time.LocalDate today = java.time.LocalDate.now();
            String todayStr = today.toString();

            Statement statement = connectDB.createStatement();

            String setOccupiedQuery = "UPDATE chambre c " +
                    "JOIN reservation r ON c.idchambre = r.idchambre " +
                    "SET c.etat = 'I' " +
                    "WHERE CURDATE() BETWEEN date_deb AND date_fin ";

            String setAvailableQuery = "UPDATE chambre c " +
                    "LEFT JOIN ( " +
                    "    SELECT DISTINCT idchambre FROM reservation " +
                    "    WHERE '" + todayStr + "' BETWEEN date_deb AND date_fin " +
                    ") r ON c.idchambre = r.idchambre " +
                    "SET c.etat = 'D' " +
                    "WHERE r.idchambre IS NULL AND c.etat NOT IN ('D', 'R')";

            statement.executeUpdate(setOccupiedQuery);
            statement.executeUpdate(setAvailableQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void addReservationButtonAction(ActionEvent event) {
        AddMessageLabel.setText("");

        if (addCheckIn.getValue() == null || addCheckOut.getValue() == null) {
            AddMessageLabel.setText("Please select dates!");
            return;
        }

        LocalDate checkInDate = addCheckIn.getValue();
        LocalDate checkOutDate = addCheckOut.getValue();
        String cin = addClientCin.getText().trim();
        String roomId = addRoomId.getText().trim();

        if (cin.isEmpty() || roomId.isEmpty()) {
            AddMessageLabel.setText("All fields are required!");
            return;
        }

        if (!cin.matches("\\d+") || !roomId.matches("\\d+")) {
            AddMessageLabel.setText("CIN and Room ID must be numeric!");
            return;
        }

        if (checkInDate.isAfter(checkOutDate)) {
            AddMessageLabel.setText("Check-in date must be before check-out!");
            return;
        }

        if (!clientExists(cin)) {
            AddMessageLabel.setText("Client does not exist!");
            return;
        }

        if (!roomExists(roomId)) {
            AddMessageLabel.setText("Room does not exist!");
            return;
        }

        saveReservationToDatabase(checkInDate.toString(), checkOutDate.toString(), cin, roomId);
    }

    @FXML
    private void modifyReservationButtonAction(ActionEvent event) {
        ModifyMessageLabel.setText("");

        if (modifyCheckIn.getValue() == null || modifyCheckOut.getValue() == null) {
            ModifyMessageLabel.setText("Please select dates!");
            return;
        }

        String reservationId = modifyReservationId.getText().trim();
        LocalDate checkInDate = modifyCheckIn.getValue();
        LocalDate checkOutDate = modifyCheckOut.getValue();
        String cin = modifyClientCin.getText().trim();
        String roomId = modifyRoomId.getText().trim();

        if (cin.isEmpty() || roomId.isEmpty() || reservationId.isEmpty() ) {
            ModifyMessageLabel.setText("All fields are required!");
            return;
        }

        if (!cin.matches("\\d+") || !roomId.matches("\\d+") || !reservationId.matches("\\d+")) {
            ModifyMessageLabel.setText("Reservation Id CIN and Room ID must be numeric!");
            return;
        }

        if (checkInDate.isAfter(checkOutDate)) {
            ModifyMessageLabel.setText("Check-in date must be before check-out!");
            return;
        }

        if (!reservationExists(reservationId)) {
            ModifyMessageLabel.setText("Reservation not found!");
            return;
        }

        modifyReservationInDatabase(reservationId, checkInDate.toString(), checkOutDate.toString(), cin, roomId);
    }

    @FXML
    private void deleteReservationButtonAction(ActionEvent event) {
        DeleteMessageLabel.setText("");

        String reservationId = deleteReservationId.getText().trim();
        boolean confirmed = deleteConfirmationCheckbox.isSelected();

        if (reservationId.isEmpty()) {
            DeleteMessageLabel.setText("All fields are required!");
            return;
        }

        if (!reservationId.matches("\\d+")) {
            DeleteMessageLabel.setText("Reservation ID must be numeric!");
            return;
        }

        if (!confirmed) {
            DeleteMessageLabel.setText("Please confirm deletion!");
            return;
        }

        if (!reservationExists(reservationId)) {
            DeleteMessageLabel.setText("Reservation not found!");
            return;
        }

        deleteReservationFromDatabase(reservationId);
    }

    private void saveReservationToDatabase(String checkIn, String checkOut, String cin, String roomId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String checkConflictQuery = "SELECT * FROM reservation WHERE idchambre = '" + roomId + "' " +
                "AND (('" + checkIn + "' BETWEEN date_deb AND date_fin) " +
                "OR ('" + checkOut + "' BETWEEN date_deb AND date_fin) " +
                "OR (date_deb BETWEEN '" + checkIn + "' AND '" + checkOut + "') " +
                "OR (date_fin BETWEEN '" + checkIn + "' AND '" + checkOut + "'))";

        String insertReservation = "INSERT INTO reservation (date_deb, date_fin, CIN, idchambre) VALUES ('"
                + checkIn + "', '" + checkOut + "', '" + cin + "', '" + roomId + "')";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(checkConflictQuery);

            if (resultSet.next()) {
                // Conflict found
                AddMessageLabel.setText("Error: Room is already booked during this period.");
            } else {
                int rowsAffected = statement.executeUpdate(insertReservation);

                if (rowsAffected > 0) {
                    AddMessageLabel.setText("Reservation added successfully!");
                } else {
                    AddMessageLabel.setText("Failed to add reservation.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AddMessageLabel.setText("An error occurred while saving the reservation.");
        }
    }

    private void modifyReservationInDatabase(String reservationId, String checkIn, String checkOut, String cin, String roomId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String checkRoomAvailability = "SELECT etat FROM chambre WHERE idchambre = '" + roomId + "'";
        String checkConflictQuery = "SELECT * FROM reservation WHERE idchambre = '" + roomId + "' " +
                "AND idreservation != '" + reservationId + "' " +
                "AND (('" + checkIn + "' BETWEEN date_deb AND date_fin) " +
                "OR ('" + checkOut + "' BETWEEN date_deb AND date_fin) " +
                "OR (date_deb BETWEEN '" + checkIn + "' AND '" + checkOut + "') " +
                "OR (date_fin BETWEEN '" + checkIn + "' AND '" + checkOut + "'))";

        String updateQuery = "UPDATE reservation SET date_fin = '" + checkOut + "', date_deb = '" + checkIn + "', idchambre = '" + roomId +
                "' WHERE idreservation = '" + reservationId + "' AND CIN = '" + cin + "'";

        String updateRoomStatus = "UPDATE chambre SET etat = 'I' WHERE idchambre = '" + roomId + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(checkRoomAvailability);

            if (resultSet.next()) {
                String etat = resultSet.getString("etat");

                if ("I".equalsIgnoreCase(etat)) {
                    ModifyMessageLabel.setText("The room is already taken!");
                } else {
                    ResultSet conflictResult = statement.executeQuery(checkConflictQuery);

                    if (conflictResult.next()) {
                        ModifyMessageLabel.setText("Conflict: Room already reserved during that time.");
                    } else {
                        int rowsAffected = statement.executeUpdate(updateQuery);

                        if (rowsAffected > 0) {
                            statement.executeUpdate(updateRoomStatus);
                            ModifyMessageLabel.setText("Reservation updated successfully!");
                        } else {
                            ModifyMessageLabel.setText("No matching reservation found!");
                        }
                    }
                }
            } else {
                ModifyMessageLabel.setText("Room does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean clientExists(String cin) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT COUNT(1) FROM client WHERE CIN = '" + cin + "'";

        try (Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean roomExists(String roomId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT COUNT(1) FROM chambre WHERE idchambre = '" + roomId + "'";

        try (Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean reservationExists(String reservationId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT COUNT(1) FROM reservation WHERE idreservation = '" + reservationId + "'";

        try (Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void deleteReservationFromDatabase(String reservationId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String deleteQuery = "DELETE FROM reservation WHERE idreservation = '" + reservationId + "' AND CIN = '" + deleteClientCin + "'";

        try {
            Statement statement = connectDB.createStatement();
            int rowsAffected = statement.executeUpdate(deleteQuery);

            if (rowsAffected > 0) {
                DeleteMessageLabel.setText("Reservation deleted successfully!");
            } else {
                DeleteMessageLabel.setText("No matching reservation found!");
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
        switchScene(event, "C_ReportPage.fxml");
    }
}
