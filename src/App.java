import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App {

    @FXML
    private Button backwards_10;

    @FXML
    private Button hide_library_btn;

    @FXML
    private Button hide_edit_btn;

    @FXML
    private Menu edit;

    @FXML
    private VBox edit_buttons;

    @FXML
    private Menu file;

    @FXML
    private Button forward_10;

    @FXML
    private Menu help;

    @FXML
    private ScrollPane library_content;

    @FXML
    private VBox library_vbox;

    @FXML
    private MenuBar menu_bar;

    @FXML
    private Button play_btn;

    @FXML
    private Slider slider_velocidad;

    @FXML
    private VBox video_info;

    @FXML
    private HBox video_menu;

    @FXML
    private ProgressBar video_progress_bar;

    @FXML
    private Pane video_reproductor;

    @FXML
    private Text video_title;

    @FXML
    private VBox video_viewer;

    private Stage mainWindow;

    private File video;
    
    private MediaView video_mv;

    public void setVideo(String ruta) {
        this.video = new File(ruta);
    }

    public void updateVideo() {
        video_mv = new MediaView(new MediaPlayer(new Media(video.toURI().toString())));
        video_reproductor.getChildren().add(video_mv);
        //video_mv.getMediaPlayer().play();
    }

    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
    }

    @FXML
    void play(ActionEvent event) {
        mainWindow.setTitle("Funciona");

        video_progress_bar.setProgress(0.5);
    }

    @FXML
    void back10(ActionEvent event) {
        if (video_progress_bar.getProgress() >= 0.1) {
            video_progress_bar.setProgress(video_progress_bar.getProgress()-0.1);
        } else {
            video_progress_bar.setProgress(0);
        }
    }

    @FXML
    void forward10(ActionEvent event) {
        if (video_progress_bar.getProgress() <= 0.9) {
            video_progress_bar.setProgress(video_progress_bar.getProgress()+0.1);
        } else {
            video_progress_bar.setProgress(1);
        }
    }

    @FXML
    void hideLibrary(ActionEvent event) {
        if (library_content.isVisible()) {
            library_content.setVisible(false);
            library_content.setManaged(false);
            hide_library_btn.setText("<");
        } else {
            library_content.setVisible(true);
            library_content.setManaged(true);
            hide_library_btn.setText(">");
        }

    }

    @FXML
    void hideEditMenu(ActionEvent event) {
        if (edit_buttons.isVisible()) {
            edit_buttons.setVisible(false);
            edit_buttons.setManaged(false);
            hide_edit_btn.setText(">");
        } else {
            edit_buttons.setVisible(true);
            edit_buttons.setManaged(true);
            hide_edit_btn.setText("<");
        }
    }

    @FXML
    void playVideo(ActionEvent event) {
        if (play_btn.getText().equals("Play")) {
            video_mv.getMediaPlayer().play();
            play_btn.setText("Pause");
        } else {
            video_mv.getMediaPlayer().pause();
            play_btn.setText("Play");
        }
    }

    void createLibrary() {
        File[] videos = new File("src/res/video").listFiles();

        for (File video : videos) {
            Button videoBtn = new Button(video.getName());

            videoBtn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent arg0) {
                    setVideo("src/res/video/" + videoBtn.getText());
                    updateVideo();
                }
                
            });

            library_vbox.getChildren().add(videoBtn);

        }
    }

}