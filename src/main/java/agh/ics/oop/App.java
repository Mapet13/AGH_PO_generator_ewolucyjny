package agh.ics.oop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class App extends Application {

  @Override
  public void start(Stage primaryStage) {
    GridPane grid = new GridPane();
    grid.gridLinesVisibleProperty();

    Scene scene = new Scene(grid, 400, 400);

    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
