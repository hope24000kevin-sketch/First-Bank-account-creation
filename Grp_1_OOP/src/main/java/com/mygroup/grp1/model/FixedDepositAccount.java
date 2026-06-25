package com.mygroup.grp1.model;

public class FixedDepositAccount extends Account {

    public FixedDepositAccount() {
        super(AccountType.FIXED_DEPOSIT);
    }

    @Override
    public long minimumDeposit() {
        return 1_000_000L;
    }

    @Override
    public String getSpecialRule() {
        return "Locked term, highest interest";
    }
}
