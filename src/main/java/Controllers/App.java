package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.net.URL;

/**
 * loads the Main.fxml
 * CS 4743 Assignment 2 by Richard Escalona & Chris Urista**
 **/

public class App extends Application {
    private static final Logger logger = LogManager.getLogger();
    @Override
    public void start(Stage primaryStage) {
        try {

            URL url = new File("src/main/resources/Main.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);

            Scene scene = new Scene(root, 1090, 700);
//            scene.getStylesheets().add(getClass().getResource("CSS/application.css").toExternalForm());

            primaryStage.setScene(scene);
            // our title
            primaryStage.setTitle("People ListView");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}

