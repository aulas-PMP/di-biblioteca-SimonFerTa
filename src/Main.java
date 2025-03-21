import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/fxml/biblioteca.fxml"));
        Parent root = loader.load();
        App controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Reproductor de video");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.getScene().getStylesheets().add("res/css/biblioteca.css");
        primaryStage.show();
        controller.createLibrary(new File("src/res/library"));
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
