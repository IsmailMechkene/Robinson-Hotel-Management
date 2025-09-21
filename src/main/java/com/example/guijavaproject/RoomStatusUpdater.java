package com.example.guijavaproject;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

public class RoomStatusUpdater {

    public static void updateRoomStatus() {
        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            LocalDate today = LocalDate.now();
            String todayStr = today.toString();

            Statement statement = connectDB.createStatement();

            String setOccupiedQuery = "UPDATE chambre c " +
                    "JOIN reservation r ON c.idchambre = r.idchambre " +
                    "SET c.etat = 'I' " +
                    "WHERE CURDATE() BETWEEN date_deb AND date_fin";

            String setAvailableQuery = "UPDATE chambre c " +
                    "LEFT JOIN ( " +
                    "    SELECT DISTINCT idchambre FROM reservation " +
                    "    WHERE '" + todayStr + "' BETWEEN date_deb AND date_fin " +
                    ") r ON c.idchambre = r.idchambre " +
                    "SET c.etat = 'D' " +
                    "WHERE r.idchambre IS NULL AND c.etat NOT IN ('D', 'R')";

            statement.executeUpdate(setOccupiedQuery);
            statement.executeUpdate(setAvailableQuery);
            statement.close();
            connectDB.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
