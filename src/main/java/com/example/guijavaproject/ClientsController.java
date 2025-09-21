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

public class ClientsController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private Label dateLabel;
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> cinColumn;
    @FXML private TableColumn<Client, String> firstNameColumn;
    @FXML private TableColumn<Client, String> lastNameColumn;
    @FXML private TableColumn<Client, String> addressColumn;
    @FXML private TableColumn<Client, String> phoneColumn;
    @FXML private CheckBox filterReservationsCheckBox;
    @FXML private Button reportButton;


    ObservableList<Client> clientData = FXCollections.observableArrayList();

    public static class Client {
        private final String cin;
        private final String firstName;
        private final String lastName;
        private final String address;
        private final String phoneNumber;


        public Client(String cin, String firstName, String lastName, String address, String phoneNumber) {
            this.cin = cin;
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.phoneNumber = phoneNumber;
        }

        public String getCin() {
            return cin;
        }
        public String getFirstName() {
            return firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public String getAddress() {
            return address;
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }
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
        Image brandingImage = new Image(getClass().getResourceAsStream("/Images/RobinsonLogo.png"));
        brandingImageView.setImage(brandingImage);
    }

    private void initializeDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy");
        dateLabel.setText(LocalDate.now().format(formatter));
    }

    private void initializeTable() {
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadDataFromDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM client";

        try {

            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            clientData.clear();

            while (queryResult.next()) {
                clientData.add(new Client(
                        queryResult.getString("CIN"),
                        queryResult.getString("nom"),
                        queryResult.getString("prenom"),
                        queryResult.getString("adresse"),
                        queryResult.getString("tel")
                ));
            }

            clientTable.setItems(clientData);

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void toggleReservationFilter() {
        if (filterReservationsCheckBox.isSelected()) {
            filterClientsByReservations();
        } else {
            clientTable.setItems(clientData);
        }
    }

    private void filterClientsByReservations() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT c.CIN, c.nom, c.prenom, c.adresse, c.tel, COUNT(r.CIN) AS reservation_count\n" +
                "FROM client c\n" +
                "JOIN reservation r ON c.CIN = r.CIN\n" +
                "GROUP BY c.CIN, c.nom, c.prenom, c.adresse, c.tel\n" +
                "HAVING COUNT(r.CIN) > 2\n" +
                "ORDER BY reservation_count DESC;";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            ObservableList<Client> filteredClients = FXCollections.observableArrayList();

            while (queryResult.next()) {
                filteredClients.add(new Client(
                        queryResult.getString("CIN"),
                        queryResult.getString("nom"),
                        queryResult.getString("prenom"),
                        queryResult.getString("adresse"),
                        queryResult.getString("tel")
                ));
            }

            clientTable.setItems(filteredClients);

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
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
    public void manageClientsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "ClientsManagement.fxml");
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