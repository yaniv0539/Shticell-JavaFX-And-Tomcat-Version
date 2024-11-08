package component.main.center.app.header;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import component.main.center.app.AppController;
import component.main.center.app.model.api.FocusCellProperty;
import dto.CellDto;
import dto.SheetDto;
import dto.deserializer.CellDtoDeserializer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static utils.Constants.GSON_INSTANCE;

public class HeaderController {


        // FXML Members

        @FXML
        private TextField textFieldUpdatedBy;

        @FXML
        private Button buttonUpdateCell;

        @FXML
        private Button buttonUploadXmlFile;

        @FXML
        private Label labelCellId;

        @FXML
        private Label labelOriginalValue;

        @FXML
        private Label labelVersionSelector;

        @FXML
        private SplitMenuButton splitMenuButtonSelectVersion;

        @FXML
        private Label lableFileName;

        @FXML
        private TextField textFieldCellId;

        @FXML
        private TextField textFieldFileName;

        @FXML
        private TextField textFieldOrignalValue;

        @FXML
        private TextField textFieldLastUpdateInVersion;

        @FXML
        private TextField textFieldVersionSelector;


        // Members

        private AppController mainController;

        private SimpleStringProperty selectedFileProperty;

        private Timeline blinkTimeline; // Store the Timeline


        // Initializers

        public void init(BooleanProperty showHeadersProperty, FocusCellProperty cellInFocusProperty) {
                buttonUpdateCell.disableProperty().bind(showHeadersProperty.not());
                splitMenuButtonSelectVersion.setDisable(false);
                textFieldOrignalValue.disableProperty().bind(showHeadersProperty.not());
                textFieldCellId.disableProperty().bind(showHeadersProperty.not());
                textFieldLastUpdateInVersion.disableProperty().bind(showHeadersProperty.not());
                textFieldUpdatedBy.disableProperty().bind(showHeadersProperty.not());
                textFieldCellId.textProperty().bind(cellInFocusProperty.getCoordinate());
                textFieldOrignalValue.textProperty().bindBidirectional(cellInFocusProperty.getOriginalValue());
                textFieldLastUpdateInVersion.textProperty().bind(cellInFocusProperty.getLastUpdateVersion());
                textFieldUpdatedBy.textProperty().bind(cellInFocusProperty.getUpdateBy());
        }

        // Getters

        public SplitMenuButton getSplitMenuButtonSelectVersion() {
                return splitMenuButtonSelectVersion;
        }


        // Setters

        public void setMainController(AppController mainController) {
                this.mainController = mainController;
        }


        // FXML Methods

        @FXML
        void buttonUpdateCellAction(ActionEvent event) {
                mainController.updateCell(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Platform.runLater(() -> mainController.showAlertPopup(new Exception(),"Update cell failed"));
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            assert response.body() != null;
                            String jsonResponse = response.body().string();

                                if (response.code() != 201 && response.code() != 200) {
                                        Platform.runLater(()-> mainController.showAlertPopup(new Exception(jsonResponse), "updating a cell") );
                                } else{
                                        Gson gson = new GsonBuilder().registerTypeAdapter(CellDto.class,new CellDtoDeserializer()).create();
                                        SheetDto sheetDto = gson.fromJson(jsonResponse, SheetDto.class);
                                        Platform.runLater(() -> mainController.updateCellRunLater(sheetDto));
                                }
                        }
                });
        }

        @FXML //maybe
        void textFieldOriginalValueAction(ActionEvent event) {
                buttonUpdateCellAction(event);
        }

        @FXML
        void textFieldVersionSelectorAction(ActionEvent event) {

        }

        public void addMenuOptionToVersionSelection(String numberOfVersion) {

                MenuItem menuItem = new MenuItem(numberOfVersion + "  (Editable)");

                // Add an action listener to the MenuItem
                menuItem.setOnAction(event -> {
                        // Update the SplitMenuButton's text to show the selected option
                        mainController.getSheet(numberOfVersion, new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Platform.runLater(() -> mainController.showAlertPopup(new Exception(),"show version"));
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    assert response.body() != null;
                                    String jsonResponse = response.body().string();

                                        if (response.code() != 200) {
                                                mainController.showAlertPopup(new Exception(GSON_INSTANCE.fromJson(jsonResponse,String.class)), "show version: " + numberOfVersion);
                                        } else {
                                                Gson gson = new GsonBuilder().registerTypeAdapter(CellDto.class,new CellDtoDeserializer()).create();
                                                SheetDto sheetDto = gson.fromJson(jsonResponse, SheetDto.class);
                                                Platform.runLater(() -> mainController.getViewSheetVersionRunLater(sheetDto));
                                        }
                                }
                        });
                });

                splitMenuButtonSelectVersion.getItems().forEach(item -> item.setText(item.getText().substring(0,2)));
                // Add the MenuItem to the SplitButton
                splitMenuButtonSelectVersion.getItems().addFirst(menuItem);
        }

        public void clearVersionButton() {
                splitMenuButtonSelectVersion.getItems().clear();
        }

        public void makeSplitMenuButtonBlink() {
                // Initialize the Timeline if not already created
                if (blinkTimeline == null) {
                        blinkTimeline = new Timeline(
                                new KeyFrame(Duration.seconds(0.5), e -> splitMenuButtonSelectVersion.setStyle("-fx-background-color: green;")),
                                new KeyFrame(Duration.seconds(1.0), e -> splitMenuButtonSelectVersion.setStyle(""))
                        );
                        blinkTimeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
                }
                blinkTimeline.play(); // Start the blinking
        }

        public void stopSplitMenuButtonBlink() {
                if (blinkTimeline != null) {
                        blinkTimeline.stop(); // Stop the blinking
                        splitMenuButtonSelectVersion.setStyle(""); // Reset color
                        blinkTimeline = null;
                }
        }
}
