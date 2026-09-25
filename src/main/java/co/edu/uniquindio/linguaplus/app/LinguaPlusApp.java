package co.edu.uniquindio.linguaplus.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LinguaPlusApp extends Application {
    @Override public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(LinguaPlusApp.class.getResource("/co/edu/uniquindio/linguaplus/vista/linguaplus-view.fxml"));
        Scene scene = new Scene(loader.load(), 1050, 720);
        stage.setTitle("LinguaPlus - Parcial I");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
