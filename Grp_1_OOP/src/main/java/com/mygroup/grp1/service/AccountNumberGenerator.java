package com.mygroup.grp1.service;

import com.mygroup.grp1.model.Branch;
import com.mygroup.grp1.persistence.AccountRepository;

import java.time.Year;

/**
 * Generates sequential account numbers per branch and year.
 * Format: BRANCHCODE-YYYY-xxxxxx (example KLA-2026-000142).
 */
public class AccountNumberGenerator {

    private final AccountRepository repository;

    public AccountNumberGenerator(AccountRepository repository) {
        this.repository = repository;
    }

    public String nextAccountNumber(Branch branch) throws Exception {
        int year = Year.now().getValue();
        int sequence = repository.nextSequence(branch.getCode(), year);
        return String.format("%s-%d-%06d", branch.getCode(), year, sequence);
    }
}
