module com.example.bankingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.prefs;
    requires jbcrypt;
    requires kernel;
    requires layout;

    opens com.example.bankingapplication to javafx.fxml;
    exports com.example.bankingapplication;
    exports com.example.bankingapplication.controllers;
    opens com.example.bankingapplication.controllers to javafx.fxml;
    exports com.example.bankingapplication.config;
    opens com.example.bankingapplication.config to javafx.fxml;
}