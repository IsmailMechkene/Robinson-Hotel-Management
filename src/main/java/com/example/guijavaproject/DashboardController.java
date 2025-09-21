package com.example.guijavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DashboardController implements Initializable{

    @FXML private ImageView brandingImageView;
    @FXML private Button reportButton;
    @FXML private Label lblFreeRooms;
    @FXML private Label lblNumberOfClients;
    @FXML private Label lblReservations;
    @FXML private Label lblTotalRooms;
    @FXML private Label lblRoomsUnderConstruction;
    @FXML private Label lblOccupiedRooms;
    @FXML private Label dateLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RoomStatusUpdater.updateRoomStatus();
        loadDashboardStats();

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


    }

    public void loadDashboardStats() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            Statement statement = connectDB.createStatement();

            ResultSet queryResult = statement.executeQuery("SELECT COUNT(*) FROM chambre WHERE etat = 'D'");
            if (queryResult.next()){
                lblFreeRooms.setText(String.valueOf(queryResult.getInt(1)));
            }

            queryResult = statement.executeQuery("SELECT COUNT(*) FROM client");
            if (queryResult.next()){
                lblNumberOfClients.setText(String.valueOf(queryResult.getInt(1)));
            }

            queryResult = statement.executeQuery("SELECT COUNT(*) FROM reservation");
            if (queryResult.next()){
                lblReservations.setText(String.valueOf(queryResult.getInt(1)));
            }

            queryResult = statement.executeQuery("SELECT COUNT(*) FROM chambre");
            if (queryResult.next()){
                lblTotalRooms.setText(String.valueOf(queryResult.getInt(1)));
            }

            queryResult = statement.executeQuery("SELECT COUNT(*) FROM chambre WHERE etat = 'R'");
            if (queryResult.next()){
                lblRoomsUnderConstruction.setText(String.valueOf(queryResult.getInt(1)));
            }

            queryResult = statement.executeQuery("SELECT COUNT(*) FROM chambre WHERE etat = 'I'");
            if (queryResult.next()){
                lblOccupiedRooms.setText(String.valueOf(queryResult.getInt(1)));
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

    @FXML
    public void ReportButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "ReportPage.fxml");
    }
}
