package com.mygroup.grp1.validation;

import com.mygroup.grp1.model.Account;
import com.mygroup.grp1.model.AccountType;
import com.mygroup.grp1.model.Branch;
import com.mygroup.grp1.model.StudentAccount;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Validates all form inputs according to the coursework specification.
 */
public final class FormValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,30}$");
    private static final Pattern NIN_PATTERN = Pattern.compile("^[A-Z0-9]{14}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+256[0-9]{9}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^[0-9]{4,6}$");

    private FormValidator() {
    }

    public static ValidationResult validate(ApplicationFormData data) {
        ValidationResult result = new ValidationResult();

        validateName(result, "firstName", "First Name", data.firstName());
        validateName(result, "lastName", "Last Name", data.lastName());
        validateNin(result, "nin", "National ID (NIN)", data.nin(), true);

        if (data.accountType() == AccountType.JOINT) {
            validateNin(result, "secondNin", "Second NIN (Joint holder)", data.secondNin(), true);
            if (data.nin() != null && data.secondNin() != null
                    && data.nin().trim().equalsIgnoreCase(data.secondNin().trim())) {
                result.addError("secondNin", "Second NIN must differ from the primary NIN.");
            }
        }

        validateEmail(result, data.email(), data.confirmEmail());
        validatePhone(result, data.phone());
        validatePin(result, data.pin(), data.confirmPin());
        validateDateOfBirth(result, data.dateOfBirth());
        validateAccountType(result, data.accountType());
        validateBranch(result, data.branch());
        validateDeposit(result, data.accountType(), data.openingDepositText(), data.dateOfBirth());

        return result;
    }

    private static void validateName(ValidationResult result, String key, String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            result.addError(key, label + " is required.");
            return;
        }
        String trimmed = value.trim();
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            result.addError(key, label + " must contain letters only and be 2-30 characters.");
        }
    }

    private static void validateNin(ValidationResult result, String key, String label, String value, boolean required) {
        if (value == null || value.trim().isEmpty()) {
            if (required) {
                result.addError(key, label + " is required.");
            }
            return;
        }
        String trimmed = value.trim().toUpperCase();
        if (!NIN_PATTERN.matcher(trimmed).matches()) {
            result.addError(key, label + " must be exactly 14 uppercase alphanumeric characters.");
        }
    }

    private static void validateEmail(ValidationResult result, String email, String confirmEmail) {
        if (email == null || email.trim().isEmpty()) {
            result.addError("email", "Email is required.");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            result.addError("email", "Email format is invalid.");
        }

        if (confirmEmail == null || confirmEmail.trim().isEmpty()) {
            result.addError("confirmEmail", "Confirm Email is required.");
        } else if (!EMAIL_PATTERN.matcher(confirmEmail.trim()).matches()) {
            result.addError("confirmEmail", "Confirm Email format is invalid.");
        }

        if (email != null && confirmEmail != null
                && !email.trim().isEmpty() && !confirmEmail.trim().isEmpty()
                && EMAIL_PATTERN.matcher(email.trim()).matches()
                && EMAIL_PATTERN.matcher(confirmEmail.trim()).matches()
                && !email.trim().equalsIgnoreCase(confirmEmail.trim())) {
            result.addError("confirmEmail", "Email addresses do not match.");
        }
    }

    private static void validatePhone(ValidationResult result, String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            result.addError("phone", "Phone Number is required.");
            return;
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            result.addError("phone", "Phone must follow Ugandan format +256XXXXXXXXX (9 digits after +256).");
        }
    }

    private static void validatePin(ValidationResult result, String pin, String confirmPin) {
        if (pin == null || pin.trim().isEmpty()) {
            result.addError("pin", "PIN is required.");
        } else if (!PIN_PATTERN.matcher(pin.trim()).matches()) {
            result.addError("pin", "PIN must be numeric with 4-6 digits.");
        } else if (hasIdenticalDigits(pin.trim())) {
            result.addError("pin", "PIN must not use all identical digits (e.g. 0000).");
        }

        if (confirmPin == null || confirmPin.trim().isEmpty()) {
            result.addError("confirmPin", "Confirm PIN is required.");
        } else if (!PIN_PATTERN.matcher(confirmPin.trim()).matches()) {
            result.addError("confirmPin", "Confirm PIN must be numeric with 4-6 digits.");
        }

        if (pin != null && confirmPin != null
                && PIN_PATTERN.matcher(pin.trim()).matches()
                && PIN_PATTERN.matcher(confirmPin.trim()).matches()
                && !pin.trim().equals(confirmPin.trim())) {
            result.addError("confirmPin", "PIN entries do not match.");
        }
    }

    private static boolean hasIdenticalDigits(String pin) {
        char first = pin.charAt(0);
        for (int i = 1; i < pin.length(); i++) {
            if (pin.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }

    private static void validateDateOfBirth(ValidationResult result, LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            result.addError("dob", "Date of Birth is required.");
        }
    }

    private static void validateAccountType(ValidationResult result, AccountType accountType) {
        if (accountType == null) {
            result.addError("accountType", "Account Type must be selected.");
        }
    }

    private static void validateBranch(ValidationResult result, Branch branch) {
        if (branch == null) {
            result.addError("branch", "Branch must be selected.");
        }
    }

    private static void validateDeposit(
            ValidationResult result,
            AccountType accountType,
            String depositText,
            LocalDate dateOfBirth) {

        if (depositText == null || depositText.trim().isEmpty()) {
            result.addError("openingDeposit", "Opening Deposit is required.");
            return;
        }

        long deposit;
        try {
            deposit = Long.parseLong(depositText.trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            result.addError("openingDeposit", "Opening Deposit must be a valid numeric amount in UGX.");
            return;
        }

        if (deposit < 0) {
            result.addError("openingDeposit", "Opening Deposit cannot be negative.");
            return;
        }

        if (accountType != null) {
            Account account = Account.forType(accountType);
            if (deposit < account.minimumDeposit()) {
                result.addError(
                        "openingDeposit",
                        String.format(
                                "Minimum opening deposit for %s is UGX %,d (%s).",
                                accountType.getDisplayName(),
                                account.minimumDeposit(),
                                account.getSpecialRule()));
            }
        }

        if (dateOfBirth != null) {
            int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
            if (age < 18 || age > 75) {
                result.addError("dob", "Applicant age must be between 18 and 75 years inclusive.");
            }
            if (accountType == AccountType.STUDENT
                    && (age < StudentAccount.MIN_AGE || age > StudentAccount.MAX_AGE)) {
                result.addError("dob", "Student accounts require applicant age between 18 and 25.");
            }
        }
    }

    public record ApplicationFormData(
            String firstName,
            String lastName,
            String nin,
            String secondNin,
            String email,
            String confirmEmail,
            String phone,
            String pin,
            String confirmPin,
            LocalDate dateOfBirth,
            AccountType accountType,
            Branch branch,
            String openingDepositText) {
    }
}
