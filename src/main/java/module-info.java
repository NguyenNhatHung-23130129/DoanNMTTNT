module vn.edu.hcmuaf.fit.doannmttnt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens vn.edu.hcmuaf.fit.doannmttnt to javafx.fxml;
    exports vn.edu.hcmuaf.fit.doannmttnt;
    opens othello to javafx.graphics;

}