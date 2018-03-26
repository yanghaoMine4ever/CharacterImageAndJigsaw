package core.code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/main.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setWidth(250);
        primaryStage.setHeight(120);
        primaryStage.setTitle("Hello");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public void one() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/one.fxml"));
        Stage stage = new Stage();
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

    public void two() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/two.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
