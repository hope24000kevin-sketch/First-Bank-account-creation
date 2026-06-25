package com.mygroup.grp1;

import com.mygroup.grp1.ui.BankAccountForm;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the First Bank Uganda account opening desktop application.
 */
public class FirstBankApp extends Application {

    @Override
    public void start(Stage stage) {
        BankAccountForm form = new BankAccountForm();
        Scene scene = new Scene(form.getRoot(), 980, 920);
        scene.getStylesheets().add(
                getClass().getResource("/com/mygroup/grp1/ui/minimalist.css").toExternalForm());

        stage.setTitle("First Bank Uganda — New Account Opening");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(680);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
