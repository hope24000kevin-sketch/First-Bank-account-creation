package com.mygroup.grp1.model;

/**
 * Abstract base class for all bank account products.
 * Subclasses define minimum opening deposits and special rules via polymorphism.
 */
public abstract class Account {

    private final AccountType type;

    protected Account(AccountType type) {
        this.type = type;
    }

    public AccountType getType() {
        return type;
    }

    public String getDisplayName() {
        return type.getDisplayName();
    }

    public abstract long minimumDeposit();

    public abstract String getSpecialRule();

    public static Account forType(AccountType type) {
        return switch (type) {
            case SAVINGS -> new SavingsAccount();
            case CURRENT -> new CurrentAccount();
            case FIXED_DEPOSIT -> new FixedDepositAccount();
            case STUDENT -> new StudentAccount();
            case JOINT -> new JointAccount();
        };
    }
}
