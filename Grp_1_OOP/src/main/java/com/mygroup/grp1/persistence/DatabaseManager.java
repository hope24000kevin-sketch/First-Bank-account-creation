package com.mygroup.grp1.persistence;

import com.mygroup.grp1.model.ClientRecord;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates and manages the MS Access database used for account persistence.
 */
public class DatabaseManager {

    private final Path databasePath;

    public DatabaseManager(Path databasePath) {
        this.databasePath = databasePath;
    }

    public void initialize() throws Exception {
        Files.createDirectories(databasePath.getParent());
        boolean isNew = !Files.exists(databasePath);

        try (Connection connection = openConnection(isNew)) {
            if (isNew) {
                createTables(connection);
            }
        }
    }

    public int nextSequence(String branchCode, int year) throws Exception {
        try (Connection connection = openConnection(false)) {
            connection.setAutoCommit(false);
            try {
                int current = 0;
                try (PreparedStatement select = connection.prepareStatement(
                        "SELECT last_sequence FROM branch_counters WHERE branch_code = ? AND account_year = ?")) {
                    select.setString(1, branchCode);
                    select.setInt(2, year);
                    try (ResultSet rs = select.executeQuery()) {
                        if (rs.next()) {
                            current = rs.getInt("last_sequence");
                        }
                    }
                }

                int next = current + 1;
                if (current == 0) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO branch_counters (branch_code, account_year, last_sequence) VALUES (?, ?, ?)")) {
                        insert.setString(1, branchCode);
                        insert.setInt(2, year);
                        insert.setInt(3, next);
                        insert.executeUpdate();
                    }
                } else {
                    try (PreparedStatement update = connection.prepareStatement(
                            "UPDATE branch_counters SET last_sequence = ? WHERE branch_code = ? AND account_year = ?")) {
                        update.setInt(1, next);
                        update.setString(2, branchCode);
                        update.setInt(3, year);
                        update.executeUpdate();
                    }
                }

                connection.commit();
                return next;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public void saveAccount(ClientRecord record) throws Exception {
        try (Connection connection = openConnection(false);
             PreparedStatement statement = connection.prepareStatement(
                     """
                     INSERT INTO accounts (
                         account_number, first_name, last_name, nin, second_nin,
                         email, phone, date_of_birth, account_type, branch,
                         opening_deposit, summary_line, created_at
                     ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """)) {

            statement.setString(1, record.getAccountNumber());
            statement.setString(2, record.getFirstName());
            statement.setString(3, record.getLastName());
            statement.setString(4, record.getNin());
            statement.setString(5, record.getSecondNin());
            statement.setString(6, record.getEmail());
            statement.setString(7, record.getPhone());
            statement.setDate(8, java.sql.Date.valueOf(record.getDateOfBirth()));
            statement.setString(9, record.getAccountType().getDisplayName());
            statement.setString(10, record.getBranch().getDisplayName());
            statement.setLong(11, record.getOpeningDeposit());
            statement.setString(12, record.formatSummary());
            statement.setTimestamp(13, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
        }
    }

    private Connection openConnection(boolean createIfMissing) throws Exception {
        String jdbcUrl = "jdbc:ucanaccess://" + databasePath.toAbsolutePath();
        if (createIfMissing) {
            jdbcUrl += ";newDatabaseVersion=V2010";
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    private void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE branch_counters (
                        branch_code TEXT(10) NOT NULL,
                        account_year INTEGER NOT NULL,
                        last_sequence INTEGER NOT NULL,
                        CONSTRAINT pk_branch_counters PRIMARY KEY (branch_code, account_year)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE accounts (
                        id AUTOINCREMENT PRIMARY KEY,
                        account_number TEXT(30) NOT NULL,
                        first_name TEXT(50) NOT NULL,
                        last_name TEXT(50) NOT NULL,
                        nin TEXT(14) NOT NULL,
                        second_nin TEXT(14),
                        email TEXT(120) NOT NULL,
                        phone TEXT(20) NOT NULL,
                        date_of_birth DATE NOT NULL,
                        account_type TEXT(30) NOT NULL,
                        branch TEXT(30) NOT NULL,
                        opening_deposit LONG NOT NULL,
                        summary_line MEMO NOT NULL,
                        created_at DATETIME NOT NULL
                    )
                    """);
        }
    }
}
