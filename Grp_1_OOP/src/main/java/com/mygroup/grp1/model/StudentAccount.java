package com.mygroup.grp1.model;

public class StudentAccount extends Account {

    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 25;

    public StudentAccount() {
        super(AccountType.STUDENT);
    }

    @Override
    public long minimumDeposit() {
        return 10_000L;
    }

    @Override
    public String getSpecialRule() {
        return "Applicant age must be 18-25";
    }
}
