package com.mygroup.grp1.ui;

import com.mygroup.grp1.model.Account;
import com.mygroup.grp1.model.AccountType;
import com.mygroup.grp1.model.Branch;
import com.mygroup.grp1.model.ClientRecord;
import com.mygroup.grp1.persistence.AccountRepository;
import com.mygroup.grp1.service.AccountService;
import com.mygroup.grp1.util.DateOfBirthHelper;
import com.mygroup.grp1.validation.FormValidator.ApplicationFormData;
import com.mygroup.grp1.validation.ValidationResult;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX form implementing the First Bank Uganda account opening specification
 * with a minimalist visual layout.
 */
public class BankAccountForm {

    private final VBox root = new VBox(28);
    private final AccountService accountService;

    private final TextField firstNameField = styledField("First Name");
    private final TextField lastNameField = styledField("Last Name");
    private final TextField ninField = styledField("National ID (NIN)");
    private final TextField secondNinField = styledField("Second NIN (Joint holder)");
    private final TextField emailField = styledField("Email");
    private final TextField confirmEmailField = styledField("Confirm Email");
    private final TextField phoneField = styledField("Phone (+256...)");
    private final PasswordField pinField = styledPassword("PIN");
    private final PasswordField confirmPinField = styledPassword("Confirm PIN");
    private final ComboBox<Integer> yearBox = new ComboBox<>();
    private final ComboBox<String> monthBox = new ComboBox<>();
    private final ComboBox<Integer> dayBox = new ComboBox<>();
    private final ComboBox<AccountType> accountTypeBox = new ComboBox<>();
    private final ComboBox<Branch> branchBox = new ComboBox<>();
    private final TextField depositField = styledField("Opening Deposit (UGX)");
    private final TextArea summaryArea = new TextArea();
    private final Label depositHintLabel = new Label();
    private final VBox jointPanel = new VBox(8);

    private final Map<String, Label> errorLabels = new HashMap<>();

    public BankAccountForm() {
        Path dbPath = Paths.get("data", "firstbank.accdb");
        AccountRepository repository = new AccountRepository(dbPath);
        this.accountService = new AccountService(repository);

        try {
            repository.initialize();
        } catch (Exception ex) {
            showFatalInitError(ex);
        }

        buildLayout();
        wireInteractions();
        resetForm();
    }

    public VBox getRoot() {
        return root;
    }

    private void buildLayout() {
        root.getStyleClass().add("app-shell");

        VBox header = new VBox(4);
        header.getStyleClass().add("page-header");
        Label title = new Label("First Bank Uganda");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("New account opening");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        ScrollPane scrollPane = new ScrollPane(buildFormContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, scrollPane);
    }

    private VBox buildFormContent() {
        VBox content = new VBox(32);
        content.setPadding(new Insets(8, 0, 24, 0));

        content.getChildren().addAll(
                section("Personal details", buildPersonalSection()),
                section("Contact & security", buildContactSection()),
                section("Account preferences", buildAccountSection()),
                section("Account Summary is Below:", buildSummarySection()),
                buildActionBar());

        return content;
    }

    private VBox section(String title, Region body) {
        VBox block = new VBox(14);
        block.getStyleClass().add("section-block");

        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.getStyleClass().add("section-title");

        Region divider = new Region();
        divider.getStyleClass().add("section-divider");

        block.getChildren().addAll(titleLabel, divider, body);
        return block;
    }

    private GridPane buildPersonalSection() {
        GridPane grid = twoColumnGrid();
        addField(grid, 0, "First Name", "firstName", firstNameField);
        addField(grid, 1, "Last Name", "lastName", lastNameField);
        addField(grid, 2, "National ID (NIN)", "nin", ninField);

        ninField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(newVal.toUpperCase())) {
                ninField.setText(newVal.toUpperCase());
            }
        });

        jointPanel.getStyleClass().add("joint-panel");
        jointPanel.setVisible(false);
        jointPanel.setManaged(false);
        Label jointHint = new Label("Joint accounts require a second National ID.");
        jointHint.getStyleClass().add("hint-text");
        jointHint.setWrapText(true);
        jointPanel.getChildren().addAll(jointHint, labeledControl("Second NIN", "secondNin", secondNinField));
        secondNinField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(newVal.toUpperCase())) {
                secondNinField.setText(newVal.toUpperCase());
            }
        });

        grid.add(jointPanel, 0, 2, 2, 1);
        return grid;
    }

    private GridPane buildContactSection() {
        GridPane grid = twoColumnGrid();
        addField(grid, 0, "Email", "email", emailField);
        addField(grid, 1, "Confirm Email", "confirmEmail", confirmEmailField);
        addField(grid, 2, "Phone Number", "phone", phoneField);
        addField(grid, 3, "PIN", "pin", pinField);
        addField(grid, 4, "Confirm PIN", "confirmPin", confirmPinField);

        HBox dobRow = new HBox(10);
        yearBox.setPromptText("Year");
        monthBox.setPromptText("Month");
        dayBox.setPromptText("Day");
        styleCombo(yearBox);
        styleCombo(monthBox);
        styleCombo(dayBox);
        yearBox.setPrefWidth(120);
        monthBox.setPrefWidth(160);
        dayBox.setPrefWidth(100);
        dobRow.getChildren().addAll(yearBox, monthBox, dayBox);

        grid.add(labeledControl("Date of Birth", "dob", dobRow), 0, 3, 2, 1);
        return grid;
    }

    private GridPane buildAccountSection() {
        GridPane grid = twoColumnGrid();

        accountTypeBox.setItems(FXCollections.observableArrayList(AccountType.values()));
        accountTypeBox.setPromptText("Select account type");
        styleCombo(accountTypeBox);

        branchBox.setItems(FXCollections.observableArrayList(Branch.values()));
        branchBox.setPromptText("Select branch");
        styleCombo(branchBox);

        addField(grid, 0, "Account Type", "accountType", accountTypeBox);
        addField(grid, 1, "Branch", "branch", branchBox);
        addField(grid, 2, "Opening Deposit (UGX)", "openingDeposit", depositField);

        depositHintLabel.getStyleClass().add("hint-text");
        depositHintLabel.setWrapText(true);
        grid.add(depositHintLabel, 0, 2, 2, 1);

        return grid;
    }

    private VBox buildSummarySection() {
        summaryArea.setEditable(false);
        summaryArea.setWrapText(true);
        summaryArea.setPrefRowCount(4);
        summaryArea.getStyleClass().add("summary-box");
        summaryArea.setPromptText("Submitted records appear here.");

        VBox box = new VBox(8, summaryArea);
        Label footer = new Label("Saved to data/firstbank.accdb");
        footer.getStyleClass().add("footer-note");
        footer.setWrapText(true);
        box.getChildren().add(footer);
        return box;
    }

    private HBox buildActionBar() {
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("primary-button");
        submitButton.setOnAction(event -> handleSubmit());

        Button resetButton = new Button("Reset");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setOnAction(event -> resetForm());

        HBox actions = new HBox(12, submitButton, resetButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(8, 0, 0, 0));
        return actions;
    }

    private void wireInteractions() {
        yearBox.setItems(FXCollections.observableArrayList(DateOfBirthHelper.years()));
        monthBox.setItems(FXCollections.observableArrayList(DateOfBirthHelper.months()));

        Runnable refreshDays = () -> refreshDayOptions();
        yearBox.valueProperty().addListener((obs, o, n) -> refreshDays.run());
        monthBox.valueProperty().addListener((obs, o, n) -> refreshDays.run());
        refreshDayOptions();

        accountTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean joint = newVal == AccountType.JOINT;
            jointPanel.setVisible(joint);
            jointPanel.setManaged(joint);
            updateDepositHint(newVal);
        });

        branchBox.valueProperty().addListener((obs, o, n) -> { });
        updateDepositHint(null);
    }

    private void refreshDayOptions() {
        Integer selectedDay = dayBox.getValue();
        var days = DateOfBirthHelper.days(yearBox.getValue(), monthBox.getValue());
        dayBox.setItems(FXCollections.observableArrayList(days));
        if (selectedDay != null && days.contains(selectedDay)) {
            dayBox.setValue(selectedDay);
        } else if (!days.isEmpty()) {
            dayBox.setValue(days.get(days.size() - 1));
        }
    }

    private void updateDepositHint(AccountType type) {
        AccountType selected = type != null ? type : accountTypeBox.getValue();
        if (selected == null) {
            depositHintLabel.setText("Select an account type to view its minimum opening deposit.");
            return;
        }
        Account account = Account.forType(selected);
        depositHintLabel.setText(String.format(
                "Minimum deposit: UGX %,d — %s",
                account.minimumDeposit(),
                account.getSpecialRule()));
    }

    private void handleSubmit() {
        clearErrors();

        ApplicationFormData data = collectFormData();
        ValidationResult validation = accountService.validate(data);

        if (!validation.isValid()) {
            applyErrors(validation);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Failed");
            alert.setHeaderText("Please correct the highlighted fields.");
            alert.setContentText(validation.summaryText());
            alert.showAndWait();
            return;
        }

        try {
            ClientRecord record = accountService.submit(data);
            String line = record.formatSummary();
            if (summaryArea.getText().isBlank()) {
                summaryArea.setText(line);
            } else {
                summaryArea.appendText(System.lineSeparator() + line);
            }

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Account Created");
            success.setHeaderText("Application submitted successfully.");
            success.setContentText(line + System.lineSeparator() + System.lineSeparator()
                    + "The record has been saved to the MS Access database.");
            success.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Submission Error");
            alert.setHeaderText("Unable to save the account record.");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private ApplicationFormData collectFormData() {
        return new ApplicationFormData(
                firstNameField.getText(),
                lastNameField.getText(),
                ninField.getText(),
                secondNinField.getText(),
                emailField.getText(),
                confirmEmailField.getText(),
                phoneField.getText(),
                pinField.getText(),
                confirmPinField.getText(),
                DateOfBirthHelper.toLocalDate(yearBox.getValue(), monthBox.getValue(), dayBox.getValue()),
                accountTypeBox.getValue(),
                branchBox.getValue(),
                depositField.getText());
    }

    private void resetForm() {
        clearErrors();
        firstNameField.clear();
        lastNameField.clear();
        ninField.clear();
        secondNinField.clear();
        emailField.clear();
        confirmEmailField.clear();
        phoneField.clear();
        pinField.clear();
        confirmPinField.clear();
        depositField.clear();
        accountTypeBox.getSelectionModel().clearSelection();
        branchBox.getSelectionModel().clearSelection();
        yearBox.getSelectionModel().clearSelection();
        monthBox.getSelectionModel().clearSelection();
        dayBox.getSelectionModel().clearSelection();
        jointPanel.setVisible(false);
        jointPanel.setManaged(false);
        updateDepositHint(null);
        refreshDayOptions();
    }

    private void clearErrors() {
        errorLabels.values().forEach(label -> {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        });
        new java.util.ArrayList<>(root.lookupAll(".field-invalid")).forEach(node ->
                node.getStyleClass().remove("field-invalid"));
    }

    private void applyErrors(ValidationResult validation) {
        validation.getErrors().forEach((fieldKey, message) -> {
            Label label = errorLabels.get(fieldKey);
            if (label != null) {
                label.setText(message);
                label.setVisible(true);
                label.setManaged(true);
            }
            findControl(fieldKey).ifPresent(control -> control.getStyleClass().add("field-invalid"));
        });
    }

    private java.util.Optional<javafx.scene.Node> findControl(String fieldKey) {
        return switch (fieldKey) {
            case "firstName" -> java.util.Optional.of(firstNameField);
            case "lastName" -> java.util.Optional.of(lastNameField);
            case "nin" -> java.util.Optional.of(ninField);
            case "secondNin" -> java.util.Optional.of(secondNinField);
            case "email" -> java.util.Optional.of(emailField);
            case "confirmEmail" -> java.util.Optional.of(confirmEmailField);
            case "phone" -> java.util.Optional.of(phoneField);
            case "pin" -> java.util.Optional.of(pinField);
            case "confirmPin" -> java.util.Optional.of(confirmPinField);
            case "dob" -> {
                yearBox.getStyleClass().add("field-invalid");
                monthBox.getStyleClass().add("field-invalid");
                dayBox.getStyleClass().add("field-invalid");
                yield java.util.Optional.empty();
            }
            case "accountType" -> java.util.Optional.of(accountTypeBox);
            case "branch" -> java.util.Optional.of(branchBox);
            case "openingDeposit" -> java.util.Optional.of(depositField);
            default -> java.util.Optional.empty();
        };
    }

    private GridPane twoColumnGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(12);
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        grid.getColumnConstraints().addAll(left, right);
        return grid;
    }

    private void addField(
            GridPane grid,
            int row,
            String labelText,
            String errorKey,
            javafx.scene.Node control) {
        grid.add(labeledControl(labelText, errorKey, control), row % 2, row / 2);
    }

    private VBox labeledControl(String labelText, String errorKey, javafx.scene.Node control) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        Label error = createErrorLabel();
        errorLabels.put(errorKey, error);
        VBox box = new VBox(6, label, control, error);
        return box;
    }

    private Label createErrorLabel() {
        Label error = new Label();
        error.getStyleClass().add("error-label");
        error.setVisible(false);
        error.setManaged(false);
        error.setWrapText(true);
        return error;
    }

    private TextField styledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-field");
        return field;
    }

    private PasswordField styledPassword(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("password-field");
        return field;
    }

    private void styleCombo(ComboBox<?> comboBox) {
        comboBox.getStyleClass().add("combo-box");
        comboBox.setMaxWidth(Double.MAX_VALUE);
    }

    private void showFatalInitError(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Database Initialization");
        alert.setHeaderText("Could not initialize MS Access database.");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}
