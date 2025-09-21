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

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

public class ClientsManagementController implements Initializable{

    @FXML private ImageView brandingImageView;
    @FXML private Button reportButton;
    @FXML private Label dateLabel;

    @FXML private Label AddMessageLabel;
    @FXML private Label ModifyMessageLabel;
    @FXML private Label DeleteMessageLabel;

    @FXML private TextField addClientCin;
    @FXML private TextField addFirstName;
    @FXML private TextField addLastName;
    @FXML private TextField addAddress;
    @FXML private TextField addPhone;

    @FXML private TextField modifyClientCin;
    @FXML private TextField modifyFirstName;
    @FXML private TextField modifyLastName;
    @FXML private TextField modifyAddress;
    @FXML private TextField modifyPhone;

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

        RoomStatusUpdater.updateRoomStatus();
    }

    @FXML
    private void addClientButtonAction(ActionEvent event) {
        AddMessageLabel.setText("");

        String cin = addClientCin.getText().trim();
        String firstName = addFirstName.getText().trim();
        String lastName = addLastName.getText().trim();
        String address = addAddress.getText().trim();
        String phone = addPhone.getText().trim();

        if (cin.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            AddMessageLabel.setText("All fields are required!");
            return;
        }

        if (!cin.matches("\\d{8,}")) {
            AddMessageLabel.setText("CIN must contain at least 8 digits!");
            return;
        }

        if (!firstName.matches("[A-Za-zÀ-ÿ]+")) {
            AddMessageLabel.setText("First name must be alphabetic!");
            return;
        }

        if (!lastName.matches("[A-Za-zÀ-ÿ]+")) {
            AddMessageLabel.setText("Last name must be alphabetic!");
            return;
        }

        if (!phone.matches("\\d+")) {
            AddMessageLabel.setText("Phone number must be numeric!");
            return;
        }

        saveClientToDatabase();
    }

    @FXML
    private void modifyClientButtonAction(ActionEvent event) {
        ModifyMessageLabel.setText("");

        String cin = modifyClientCin.getText().trim();
        String firstName = modifyFirstName.getText().trim();
        String lastName = modifyLastName.getText().trim();
        String address = modifyAddress.getText().trim();
        String phone = modifyPhone.getText().trim();

        if (cin.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            ModifyMessageLabel.setText("All fields are required!");
            return;
        }

        if (!cin.matches("\\d{8,}")) {
            ModifyMessageLabel.setText("CIN must be 8+ digits!");
            return;
        }

        if (!firstName.matches("[A-Za-zÀ-ÿ\\s]+")) {
            ModifyMessageLabel.setText("Invalid first name!");
            return;
        }

        if (!lastName.matches("[A-Za-zÀ-ÿ\\s]+")) {
            ModifyMessageLabel.setText("Invalid last name!");
            return;
        }

        if (!phone.matches("\\d{8,15}")) {
            ModifyMessageLabel.setText("Phone must be 8-15 digits!");
            return;
        }

        modifyClientInDatabase();
    }

    @FXML
    private void deleteClientButtonAction(ActionEvent event) {
        DeleteMessageLabel.setText("");

        String cin = deleteClientCin.getText().trim();
        boolean confirmed = deleteConfirmationCheckbox.isSelected();

        // CIN validation
        if (cin.isEmpty()) {
            DeleteMessageLabel.setText("CIN is required!");
            return;
        }

        if (!cin.matches("\\d{8,}")) {
            DeleteMessageLabel.setText("Invalid CIN format!");
            return;
        }

        if (!confirmed) {
            DeleteMessageLabel.setText("You must confirm deletion!");
            return;
        }

        deleteClientFromDatabase();
    }

    public void saveClientToDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyClient = "SELECT count(1) FROM client WHERE CIN = '" + addClientCin.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyClient);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 0) {
                    AddMessageLabel.setText("Client added successfully!");

                    String insertClient = "INSERT INTO client VALUES ('"
                            + addClientCin.getText() + "', '"
                            + addFirstName.getText() + "', '"
                            + addLastName.getText() + "', '"
                            + addAddress.getText() + "', '"
                            + addPhone.getText() + "')";

                    int rowsAffected = statement.executeUpdate(insertClient);
                    if (rowsAffected == 0) {
                        AddMessageLabel.setText("Failed to add client. Please try again.");
                    }
                } else {
                    AddMessageLabel.setText("Client with this CIN already exists!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modifyClientInDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String cin = modifyClientCin.getText().trim();
        String firstName = modifyFirstName.getText().trim();
        String lastName = modifyLastName.getText().trim();
        String address = modifyAddress.getText().trim();
        String phone = modifyPhone.getText().trim();

        try {
            Statement statement = connectDB.createStatement();

            String verifyClient = "SELECT count(1) FROM client WHERE CIN = '" + cin + "'";
            ResultSet resultSet = statement.executeQuery(verifyClient);

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                String updateQuery = "UPDATE client SET "
                        + "nom = '" + firstName + "', "
                        + "prenom = '" + lastName + "', "
                        + "adresse = '" + address + "', "
                        + "tel = '" + phone + "' "
                        + "WHERE CIN = '" + cin + "'";

                int rowsAffected = statement.executeUpdate(updateQuery);

                if (rowsAffected > 0) {
                    ModifyMessageLabel.setText("Client updated successfully!");
                } else {
                    ModifyMessageLabel.setText("Update failed!");
                }
            } else {
                ModifyMessageLabel.setText("Client not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteClientFromDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String clientCIN = deleteClientCin.getText();

        try {
            Statement statement = connectDB.createStatement();

            String deleteReservations = "DELETE FROM reservation WHERE CIN = '" + clientCIN + "'";
            int reservationsDeleted = statement.executeUpdate(deleteReservations);

            String deleteClient = "DELETE FROM client WHERE CIN = '" + clientCIN + "'";
            int clientsDeleted = statement.executeUpdate(deleteClient);

            if (clientsDeleted > 0) {
                DeleteMessageLabel.setText("Client and " + reservationsDeleted + " reservations deleted!");
            } else {
                DeleteMessageLabel.setText("Client not found!");
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
