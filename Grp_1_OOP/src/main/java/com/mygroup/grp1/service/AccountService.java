package com.mygroup.grp1.service;

import com.mygroup.grp1.model.Branch;
import com.mygroup.grp1.model.ClientRecord;
import com.mygroup.grp1.persistence.AccountRepository;
import com.mygroup.grp1.validation.FormValidator.ApplicationFormData;
import com.mygroup.grp1.validation.FormValidator;
import com.mygroup.grp1.validation.ValidationResult;

/**
 * Coordinates validation, account-number generation, and persistence.
 */
public class AccountService {

    private final AccountRepository repository;
    private final AccountNumberGenerator numberGenerator;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
        this.numberGenerator = new AccountNumberGenerator(repository);
    }

    public ValidationResult validate(ApplicationFormData data) {
        return FormValidator.validate(data);
    }

    public ClientRecord submit(ApplicationFormData data) throws Exception {
        ValidationResult validation = validate(data);
        if (!validation.isValid()) {
            throw new IllegalStateException(validation.summaryText());
        }

        String accountNumber = numberGenerator.nextAccountNumber(data.branch());
        ClientRecord record = new ClientRecord(
                accountNumber,
                data.firstName().trim(),
                data.lastName().trim(),
                data.nin().trim().toUpperCase(),
                data.secondNin() == null || data.secondNin().trim().isEmpty()
                        ? null
                        : data.secondNin().trim().toUpperCase(),
                data.email().trim(),
                data.phone().trim(),
                data.dateOfBirth(),
                data.accountType(),
                data.branch(),
                Long.parseLong(data.openingDepositText().trim().replace(",", "")));

        repository.save(record);
        return record;
    }
}
