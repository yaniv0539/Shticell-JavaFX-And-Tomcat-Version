package component.main.top;

import component.main.MainController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TopController {

    @FXML
    private GridPane gridPaneTopApp;

    @FXML
    private Label textFieldAppName;

    @FXML
    private Label textFieldHelloGuest;

    private MainController mainController;

    public TopController() {

    }

    public void init() {
        textFieldHelloGuest.textProperty().bind(Bindings.concat("Hello ", mainController.userNameProperty()));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setTextFieldHelloGuest(String text) {
        textFieldHelloGuest.setText(text);
    }


}
