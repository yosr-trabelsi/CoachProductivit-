module com.example.productivitycoach {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.net.http;
    requires org.json;

    opens com.example.productivitycoach to javafx.fxml;
    opens com.example.productivitycoach.model to javafx.base, javafx.fxml;
    opens com.example.productivitycoach.DAO to javafx.base;
    opens com.example.productivitycoach.services to javafx.base;

    exports com.example.productivitycoach;
    exports com.example.productivitycoach.model;
    exports com.example.productivitycoach.DAO;
    exports com.example.productivitycoach.services;
}