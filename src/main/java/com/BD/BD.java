package com.BD;

import java.sql.*;
import java.util.Scanner;

public class BD {

    private static final String URL = "jdbc:mysql://localhost:3306/attendance_db";
    private static final String USER = "root";
    private static final String PASSWORD = "0909";

    private static void table () {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "entry_time DATETIME, " +
                    "exit_time DATETIME);";
            statement.executeUpdate(createTableSQL);
            System.out.println("Database and table initialized.");
        } catch (SQLException e) {
            System.err.println("Error creating database or table: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        table(); 
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Attendance Tracker ---");
            System.out.println("1. Register Entry");
            System.out.println("2. Register Exit");
            System.out.println("3. View Attendance Records");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> registerEntry(scanner);
                case 2 -> registerExit(scanner);
                case 3 -> records();
                case 4 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);

        scanner.close();
    }

    private static void registerEntry(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        String entryTime = new Timestamp(System.currentTimeMillis()).toString();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO attendance (name, entry_time) VALUES (?, ?)")
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, entryTime);
            preparedStatement.executeUpdate();
            System.out.println("Entry registered successfully.");
        } catch (SQLException e) {
            System.err.println("Error registering entry: " + e.getMessage());
        }
    }

    private static void registerExit(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        String exitTime = new Timestamp(System.currentTimeMillis()).toString();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE attendance SET exit_time = ? WHERE name = ? AND exit_time IS NULL")
        ) {
            preparedStatement.setString(1, exitTime);
            preparedStatement.setString(2, name);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Exit registered successfully.");
            } else {
                System.out.println("No open entry found for this name.");
            }
        } catch (SQLException e) {
            System.err.println("Error registering exit: " + e.getMessage());
        }
    }

    private static void records() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM attendance")) {

            System.out.println("\n--- Attendance Records ---");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") +
                                   ", Name: " + resultSet.getString("name") +
                                   ", Entry: " + resultSet.getString("entry_time") +
                                   ", Exit: " + resultSet.getString("exit_time"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving records: " + e.getMessage());
        }
    }
}