module kth.decitong.librarydb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;


    opens kth.decitong.librarydb to javafx.fxml;
    opens kth.decitong.librarydb.model to javafx.fxml;
    exports kth.decitong.librarydb;
}