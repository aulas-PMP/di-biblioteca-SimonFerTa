import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;

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
    private RadioMenuItem hide_library_menu;

        @FXML
        void hideLibrary(ActionEvent event) {
            if (library_content.isVisible()) {
                library_content.setVisible(false);
                library_content.setManaged(false);
                hide_library_menu.setSelected(false);
                hide_library_btn.setText("<");
            } else {
                library_content.setVisible(true);
                library_content.setManaged(true);
                hide_library_menu.setSelected(true);
                hide_library_btn.setText(">");
            }
        }

    @FXML
    private Button hide_edit_btn;

    @FXML
    private RadioMenuItem hide_edit_menu;

        @FXML
        void hideEditMenu(ActionEvent event) {
            if (edit_buttons.isVisible()) {
                edit_buttons.setVisible(false);
                edit_buttons.setManaged(false);
                hide_edit_menu.setSelected(false);
                hide_edit_btn.setText(">");
            } else {
                edit_buttons.setVisible(true);
                edit_buttons.setManaged(true);
                hide_edit_menu.setSelected(true);
                hide_edit_btn.setText("<");
            }
        }

    @FXML
    private Menu edit;

    @FXML
    private ScrollPane edit_buttons;

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

    private File library_folder;
    
    private MediaView video_mv;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void updateVideo(File video) {
        video_mv = new MediaView(new MediaPlayer(new Media(video.toURI().toString())));
        video_reproductor.getChildren().clear();
        video_mv.fitHeightProperty().bind(video_reproductor.heightProperty());
        video_mv.fitWidthProperty().bind(video_reproductor.widthProperty());
        video_reproductor.getChildren().add(video_mv);
        video_mv.getMediaPlayer().setRate(Double.parseDouble(video_speed.getSelectionModel().getSelectedItem().toString().replace("x", "")));
        bindVideo(video_mv.getMediaPlayer(), video_progress_bar);
        play_btn.setText("Play");
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
        File[] videos;
        if (videoDirectory.isDirectory()) {
            videos = videoDirectory.listFiles();
        } else {
            videos = new File[1];
            videos[0] = videoDirectory;
        }

        for (File video : videos) {
            if (!video.isDirectory()) {
                Button videoBtn = new Button(video.getName().replaceFirst("[.][^.]+$", ""));
                videoBtn.setCursor(Cursor.OPEN_HAND);
                videoBtn.getStyleClass().add("video_btn");
                videoBtn.getStyleClass().add("white-text");
                videoBtn.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent arg0) {
                        updateVideo(video);
                        video_title.setText(videoBtn.getText());
                    }
                });

                Text videoDesc = new Text(video.getName().substring(video.getName().lastIndexOf(".")+1).toUpperCase());
                videoDesc.getStyleClass().add("white-text");
                try {
                    MediaPlayer mpDuration = new MediaPlayer(new Media(video.toURI().toString()));
                    mpDuration.setOnReady(new Runnable() {
                        @Override
                        public void run() {
                            int horas = (int)mpDuration.getMedia().getDuration().toHours();
                            int minutos = (int)(mpDuration.getMedia().getDuration().toMinutes()-horas*60);
                            int segundos = (int)(mpDuration.getMedia().getDuration().toSeconds()-minutos*60-horas*3600);

                            videoDesc.setText(videoDesc.getText() + ", " + horas + ":" + minutos + ":" + segundos);
                        }
                    });
                    VBox.setMargin(videoDesc, new Insets(0,0,10,0));

                    library_vbox.getChildren().add(videoBtn);
                    library_vbox.getChildren().add(videoDesc);
                    library_vbox.getChildren().add(new Separator(Orientation.HORIZONTAL));
                } catch (MediaException e) {}

            } else {
                createLibrary(video);
            }
        }
    }

    public void createLibrary() {
        library_vbox.getChildren().clear();
        createLibrary(library_folder);
    }

    @FXML
    void changeLibraryRoot(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        this.library_folder = dc.showDialog(primaryStage);
        library_vbox.getChildren().clear();
        createLibrary();
    }

    @FXML
    void clearLibrary(ActionEvent event) {
        library_vbox.getChildren().clear();
    }

    @FXML 
    void addToLibrary(ActionEvent event) {
        FileChooser fc = new FileChooser();
        createLibrary(fc.showOpenDialog(primaryStage));
    }

    @FXML
    void openLibrary(ActionEvent event) {
        try {
            Desktop.getDesktop().open(library_folder);
        } catch (Exception e) {}
    }

    @FXML
    void cleanMedia(ActionEvent event) {
        this.video_reproductor.getChildren().clear();
        this.video_mv.getMediaPlayer().dispose();
        this.video_title.setText(null);
    }

    @FXML
    void showAboutUsDialog(ActionEvent event) {
        Alert aboutUs = new Alert(AlertType.INFORMATION);
        aboutUs.setTitle("Sobre nosotros:");
        aboutUs.setHeaderText(null);
        aboutUs.setContentText("Reproductor de vídeo\nSimón Fernández Tacón");
        aboutUs.showAndWait();
    }
}