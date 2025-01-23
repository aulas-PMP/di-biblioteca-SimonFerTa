import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/fxml/prueba.fxml"));
        Parent root = loader.load();
        App controller = loader.getController();
        controller.setMainWindow(primaryStage);
        primaryStage.setTitle("Reproductor de video");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
        controller.createLibrary();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
