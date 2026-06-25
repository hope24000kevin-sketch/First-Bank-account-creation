package com.mygroup.grp1.model;

public class CurrentAccount extends Account {

    public CurrentAccount() {
        super(AccountType.CURRENT);
    }

    @Override
    public long minimumDeposit() {
        return 200_000L;
    }

    @Override
    public String getSpecialRule() {
        return "Overdraft allowed, no interest";
    }
}
