package com.mygroup.grp1.persistence;

import com.mygroup.grp1.model.ClientRecord;

import java.nio.file.Path;

/**
 * Persists account records to Microsoft Access via JDBC (UCanAccess).
 */
public class AccountRepository {

    private final DatabaseManager databaseManager;

    public AccountRepository(Path databasePath) {
        this.databaseManager = new DatabaseManager(databasePath);
    }

    public void initialize() throws Exception {
        databaseManager.initialize();
    }

    public int nextSequence(String branchCode, int year) throws Exception {
        return databaseManager.nextSequence(branchCode, year);
    }

    public void save(ClientRecord record) throws Exception {
        databaseManager.saveAccount(record);
    }
}
