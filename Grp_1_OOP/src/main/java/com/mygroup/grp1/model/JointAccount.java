package com.mygroup.grp1.model;

public class JointAccount extends Account {

    public JointAccount() {
        super(AccountType.JOINT);
    }

    @Override
    public long minimumDeposit() {
        return 100_000L;
    }

    @Override
    public String getSpecialRule() {
        return "Requires a second NIN";
    }
}
