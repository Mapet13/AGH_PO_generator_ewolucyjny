package agh.ics.oop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new Label("Zwierzak");
        Scene scene = new Scene(label, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
