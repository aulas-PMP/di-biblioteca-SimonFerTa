import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class App {

    @FXML
    private Button backwards_10;    

        @FXML
        void back10(ActionEvent event) {
            if (video_mv != null) {
                video_mv.getMediaPlayer().seek(new Duration(video_mv.getMediaPlayer().getCurrentTime().toMillis()-10000));
            }
        }

    @FXML
    private Button hide_library_btn;

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
    private Button hide_edit_btn;

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
    private Menu edit;

    @FXML
    private VBox edit_buttons;

    @FXML
    private Menu file;

    @FXML
    private Button forward_10;

        @FXML
        void forward10(ActionEvent event) {
            if (video_mv != null) {
                video_mv.getMediaPlayer().seek(new Duration(video_mv.getMediaPlayer().getCurrentTime().toMillis()+10000));
            }
        }

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
        void playVideo(ActionEvent event) {
            if (video_mv != null) {
                if (!(video_mv.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING)) {
                    video_mv.getMediaPlayer().play();
                    play_btn.setText("Pause");
                } else {
                    video_mv.getMediaPlayer().pause();
                    play_btn.setText("Play");
                }
            }
        }

    @FXML
    private Slider slider_velocidad;

    @FXML
    private VBox video_info;

    @FXML
    private HBox video_menu;

    @FXML
    private Button video_mutebtn;

        @FXML
        void muteVideo(ActionEvent event) {
            if (video_mv != null) {
                if (video_mv.getMediaPlayer().isMute()) {
                    video_mv.getMediaPlayer().setMute(false);
                } else {
                    video_mv.getMediaPlayer().setMute(true);
                }
            }
        }

    @FXML
    private ProgressBar video_progress_bar;

    @FXML
    private HBox video_reproductor;

    @FXML
    private ChoiceBox<?> video_speed;

        @FXML
        void changeVideoSpeed(ActionEvent event) {
            if (video_mv != null) {
                double selected = Double.parseDouble(video_speed.getSelectionModel().getSelectedItem().toString().replace("x", ""));
                video_mv.getMediaPlayer().setRate(selected);
            }
        }

    @FXML
    private Text video_title;

    @FXML
    private Slider video_volume;

    @FXML
    private VBox video_viewer;
    
    private MediaView video_mv;

    public void updateVideo(File video) {
        video_mv = new MediaView(new MediaPlayer(new Media(video.toURI().toString())));
        video_reproductor.getChildren().clear();
        video_mv.fitHeightProperty().bind(video_reproductor.heightProperty());
        video_mv.fitWidthProperty().bind(video_reproductor.widthProperty());
        video_reproductor.getChildren().add(video_mv);
        video_mv.getMediaPlayer().setRate(Double.parseDouble(video_speed.getSelectionModel().getSelectedItem().toString().replace("x", "")));
        bindVideo(video_mv.getMediaPlayer(), video_progress_bar);
    }

    public void bindVideo(MediaPlayer mp, ProgressBar pb) {
        pb.progressProperty().bind(
            Bindings.createDoubleBinding(
                () -> {
                    Duration currentTime = mp.getCurrentTime();
                    Duration duration = mp.getMedia().getDuration();
                    return currentTime.toMillis() / duration.toMillis();
                },
                mp.currentTimeProperty(),
                mp.getMedia().durationProperty())
        );

        EventHandler<MouseEvent> onClickAndDragHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                Duration dur = mp.getMedia().getDuration();
                Duration time = dur.multiply(me.getX() / pb.getWidth());
                mp.seek(time);
                me.consume();
            }
        };
        pb.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickAndDragHandler);
        pb.addEventHandler(MouseEvent.MOUSE_DRAGGED, onClickAndDragHandler);

        video_mv.getMediaPlayer().volumeProperty().bind(video_volume.valueProperty());
    }

    public void createLibrary(File videoDirectory) {
        File[] videos = videoDirectory.listFiles();

        for (File video : videos) {
            if (!video.isDirectory()) {
                Button videoBtn = new Button(video.getName().replaceFirst("[.][^.]+$", ""));
                videoBtn.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent arg0) {
                        updateVideo(video);
                        video_title.setText(videoBtn.getText());
                    }
                });
                
                library_vbox.getChildren().add(videoBtn);

            } else {
                createLibrary(video);
            }
        }
    }
}