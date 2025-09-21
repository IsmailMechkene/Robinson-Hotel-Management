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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class RoomsController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private Button reportButton;
    @FXML private Label dateLabel;

    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> roomIdColumn;
    @FXML private TableColumn<Room, String> bedsNumberColumn;
    @FXML private TableColumn<Room, String> roomStatusColumn;
    @FXML private CheckBox filterRoomsCheckBox;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField nbPersonsField;
    @FXML private Label SearchMessageLabel;

    ObservableList<Room> roomsData = FXCollections.observableArrayList();

    public static class Room {

        private final String roomId;
        private final String bedsNumber;
        private final String roomStatus;

        public Room(String roomId, String bedsNumber, String roomStatus) {
            this.roomId = roomId;
            this.bedsNumber = bedsNumber;
            this.roomStatus = roomStatus;
        }

        public String getRoomId() {
            return roomId;
        }
        public String getBedsNumber() {
            return bedsNumber;
        }
        public String getRoomStatus() {
            return roomStatus;
        }
    }


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
        initializeTable();
        loadDataFromDatabase();

    }

    private void initializeTable() {
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        bedsNumberColumn.setCellValueFactory(new PropertyValueFactory<>("bedsNumber"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("roomStatus"));
    }

    @FXML
    private void loadDataFromDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM chambre";

        try {

            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            roomsData.clear();

            while (queryResult.next()) {
                roomsData.add(new RoomsController.Room(
                        queryResult.getString("idchambre"),
                        queryResult.getString("etat"),
                        queryResult.getString("capacité")
                ));
            }

            roomTable.setItems(roomsData);

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void toggleRoomsFilter() {
        if (filterRoomsCheckBox.isSelected()) {
            filterHighlyDemandedRooms();
        } else {
            roomTable.setItems(roomsData);
        }
    }

    private void filterHighlyDemandedRooms() {
        ObservableList<Room> filteredRooms = FXCollections.observableArrayList();
        DatabaseConnection connectNow = new DatabaseConnection();
        String query = "SELECT c.*, COUNT(r.idchambre)  " +
                "FROM chambre c " +
                "JOIN reservation r ON c.idchambre = r.idchambre " +
                "GROUP BY c.idchambre, c.capacité, c.etat " +
                "HAVING COUNT(r.idchambre) > 2";

        try (Connection connectDB = connectNow.getConnection();
             PreparedStatement statement = connectDB.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                filteredRooms.add(new Room(
                        resultSet.getString("idchambre"),
                        resultSet.getString("etat"),
                        resultSet.getString("capacité")
                ));
            }
            roomTable.setItems(filteredRooms);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchRoomsButton(ActionEvent event) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String nbPersonsStr = nbPersonsField.getText();

        if (startDate == null || endDate == null || nbPersonsStr.isEmpty()) {
            SearchMessageLabel.setText("Please fill in the start date, end date, and number of persons.");
            return;
        }

        int nbPersons;
        try {
            nbPersons = Integer.parseInt(nbPersonsStr);
        }
        catch (NumberFormatException e) {
            SearchMessageLabel.setText("Please enter a valid number for persons.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            SearchMessageLabel.setText("Check-in date must be before check-out!");
            return;
        }

        searchAvailableRooms(startDate, endDate, nbPersons);
    }

    private void searchAvailableRooms(LocalDate startDate, LocalDate endDate, int nbPersons) {
        DatabaseConnection connectNow = new DatabaseConnection();
        String sql = "{ CALL get_available_rooms(?, ?, ?) }";

        try (Connection connectDB = connectNow.getConnection();
             CallableStatement cs = connectDB.prepareCall(sql)) {

            cs.setDate(1, Date.valueOf(startDate));
            cs.setDate(2, Date.valueOf(endDate));
            cs.setInt(3, nbPersons);

            boolean hasResultSet = cs.execute();

            if (hasResultSet) {
                try (ResultSet rs = cs.getResultSet()) {
                    ObservableList<Room> availableRooms = FXCollections.observableArrayList();
                    while (rs.next()) {
                        availableRooms.add(new Room(
                                rs.getString("idchambre"),
                                rs.getString("etat"),
                                rs.getString("capacité")
                        ));
                    }
                    if (availableRooms.isEmpty()) {
                        SearchMessageLabel.setText("There is no available rooms responding to your requirements!");
                        roomTable.setItems(availableRooms);
                    }
                    else {
                        roomTable.setItems(availableRooms);
                        SearchMessageLabel.setText("");
                    }
                }
            }
        } catch (SQLException e) {
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
    public void manageRoomsButtonAction(ActionEvent event) throws IOException {
        switchScene(event, "RoomsManagement.fxml");
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
