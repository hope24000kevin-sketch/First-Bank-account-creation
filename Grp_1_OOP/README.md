# First Bank Uganda — Account Opening Application

Object-oriented JavaFX desktop coursework project for **First Bank Uganda** new account registration. The UI applies **Material Design 3 Expressive** principles (vibrant tonal colors, rounded containment, expressive typography hierarchy, and accessible contrast) adapted for JavaFX.

## Features

- Full account opening form with validation for all required fields
- Leap-year aware Date of Birth selectors (Year / Month / Day)
- Polymorphic account products via abstract `Account` and concrete subclasses
- Minimum deposit validation using `minimumDeposit()` overrides
- Sequential account numbers: `BRANCHCODE-YYYY-xxxxxx`
- MS Access persistence through JDBC (UCanAccess)
- Inline field errors plus summary dialog on failed submission

## Requirements

- **JDK 17+** (JavaFX 21 is pulled in by Maven)
- **Apache Maven 3.8+**
- **Windows** recommended for MS Access `.accdb` compatibility

## Project Structure

```
src/main/java/com/mygroup/grp1/
├── FirstBankApp.java              # Application entry point
├── model/                         # OOP account hierarchy
├── validation/                    # Form validation rules
├── service/                       # Business logic
├── persistence/                   # MS Access JDBC layer
├── ui/                            # JavaFX form
└── util/                          # Date-of-birth helpers

src/main/resources/com/mygroup/grp1/ui/
└── material-expressive.css        # Material 3 Expressive theme

data/
└── firstbank.accdb                # Created automatically on first run
```

## Setup

1. Clone or download this repository.
2. Open a terminal in the project folder.
3. Ensure Java and Maven are installed and on your `PATH`.

Verify tools:

```bash
java -version
mvn -version
```

## Compile

```bash
mvn clean compile
```

## Run

```bash
mvn javafx:run
```

## Operating the Application

1. Launch the app using `mvn javafx:run`.
2. Complete all fields in the form.
3. Choose **Account Type** and **Branch** from the combo boxes.
4. For **Joint** accounts, a second NIN field appears and becomes mandatory.
5. Select Date of Birth; February automatically shows 28 or 29 days for leap years.
6. Click **Submit Application**.
   - Invalid input: inline red messages appear and an error dialog lists all issues.
   - Valid input: a formatted summary line is shown and saved to `data/firstbank.accdb`.
7. Click **Reset Form** to clear all inputs.

### Example Valid Submission Output

```
ACC: KLA-2026-000142 | Okello Allan | Savings | Kampala | DOB 2004-02-29 | +256772123456 | Deposit 50,000 | okello.allan@email.com
```

## Validation Summary

| Field | Rule |
|-------|------|
| First / Last Name | Letters only, 2–30 characters |
| NIN | Exactly 14 uppercase alphanumeric characters |
| Email / Confirm Email | Valid format and must match |
| Phone | Ugandan format `+256` followed by 9 digits |
| PIN / Confirm PIN | 4–6 digits, must match, not all identical |
| Age | 18–75 inclusive; Student accounts require 18–25 |
| Opening Deposit | Must meet account-type minimum |
| Joint | Requires distinct second NIN |

## Object-Oriented Design

- `Account` (abstract) defines shared state and `minimumDeposit()`
- `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount`, `StudentAccount`, `JointAccount` override deposit rules
- `Account.forType(...)` uses polymorphism during deposit validation

## Database

The file `data/firstbank.accdb` is created automatically. Tables:

- `accounts` — submitted account records
- `branch_counters` — per-branch, per-year sequential counters

You can open the database in Microsoft Access to verify saved records.

## License

This project is developed for academic purposes as coursework for the Faculty of Science and Technology_OOP.

## Contact

For issues or questions about this application, please refer to Group_1 OOP Victoria University Kampala,
Email: hope24000kevin@gmail.com


## Material 3 Expressive Design Notes

This desktop theme follows Google's M3 Expressive guidance:

- **Color**: HCT-inspired primary / secondary / tertiary roles with tonal surfaces
- **Shape**: 24–28px rounded cards and pill chips
- **Typography**: Display, title, body, and label hierarchy
- **Containment**: Grouped sections in elevated cards
- **Contrast**: Accessible error colors and focused field states

Reference: [Material Design 3](https://m3.material.io/) and [Expressive Design research](https://design.google/library/expressive-material-design-google-research)


