package com.mygroup.grp1.model;

public class SavingsAccount extends Account {

    public SavingsAccount() {
        super(AccountType.SAVINGS);
    }

    @Override
    public long minimumDeposit() {
        return 50_000L;
    }

    @Override
    public String getSpecialRule() {
        return "Earns interest, no overdraft";
    }
}
