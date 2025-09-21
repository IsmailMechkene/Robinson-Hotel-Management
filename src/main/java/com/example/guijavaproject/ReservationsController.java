package com.example.guijavaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReservationsController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private Label dateLabel;
    @FXML private Button reportButton;
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> startDateColumn;
    @FXML private TableColumn<Reservation, String> endDateColumn;
    @FXML private TableColumn<Reservation, String> cinColumn;
    @FXML private TableColumn<Reservation, Integer> roomIdColumn;

    ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    public static class Reservation {
        private final String reservationId;
        private final String startDate;
        private final String endDate;
        private final String clientCin;
        private final int roomId;

        public Reservation(String reservationId, String startDate, String endDate, String clientCin, int roomId) {
            this.reservationId = reservationId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.clientCin = clientCin;
            this.roomId = roomId;
        }

        public String getReservationId() { return reservationId; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getClientCin() { return clientCin; }
        public int getRoomId() { return roomId; }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeBranding();
        initializeDate();
        initializeTable();
        loadDataFromDatabase();

        reportButton.setOnMouseEntered(e -> {
            reportButton.setStyle("-fx-background-color: #6C5B7B; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 200px;");
        });

        reportButton.setOnMouseExited(e -> {
            reportButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 200px;");
        });

        RoomStatusUpdater.updateRoomStatus();
    }

    private void initializeBranding() {
        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
            brandingImageView.setImage(brandingImage);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }

    private void initializeDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy");
        dateLabel.setText(LocalDate.now().format(formatter));
    }

    private void initializeTable() {
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("clientCin"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
    }

    private void loadDataFromDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM reservation";

        try (PreparedStatement statement = connectDB.prepareStatement(query);
             ResultSet queryResult = statement.executeQuery()) {

            reservationData.clear();

            while (queryResult.next()) {
                reservationData.add(new Reservation(
                        queryResult.getString("idreservation"),
                        queryResult.getString("date_deb"),
                        queryResult.getString("date_fin"),
                        queryResult.getString("CIN"),
                        queryResult.getInt("idchambre")
                ));
            }

            reservationTable.setItems(reservationData);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            try {
                connectDB.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
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
    public void manageReservationsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "ReservationsManagement.fxml");
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
