package com.mygroup.grp1.model;

import java.time.LocalDate;

/**
 * Immutable snapshot of a successfully validated account application.
 */
public class ClientRecord {

    private final String accountNumber;
    private final String firstName;
    private final String lastName;
    private final String nin;
    private final String secondNin;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final AccountType accountType;
    private final Branch branch;
    private final long openingDeposit;

    public ClientRecord(
            String accountNumber,
            String firstName,
            String lastName,
            String nin,
            String secondNin,
            String email,
            String phone,
            LocalDate dateOfBirth,
            AccountType accountType,
            Branch branch,
            long openingDeposit) {
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nin = nin;
        this.secondNin = secondNin;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.accountType = accountType;
        this.branch = branch;
        this.openingDeposit = openingDeposit;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNin() {
        return nin;
    }

    public String getSecondNin() {
        return secondNin;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Branch getBranch() {
        return branch;
    }

    public long getOpeningDeposit() {
        return openingDeposit;
    }

    public String formatSummary() {
        return String.format(
                "ACC: %s | %s %s | %s | %s | DOB %s | %s | Deposit %,d | %s",
                accountNumber,
                lastName,
                firstName,
                accountType.getDisplayName(),
                branch.getDisplayName(),
                dateOfBirth,
                phone,
                openingDeposit,
                email);
    }
}
