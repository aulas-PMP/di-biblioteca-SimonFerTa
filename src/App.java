import java.io.File;
import java.text.DecimalFormat;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;

public class App {

    @FXML
    private Button backwards_10;    

        @FXML
        void back10(ActionEvent event) {
            if (video_mp != null) {
                video_mp.seek(new Duration(video_mp.getCurrentTime().toMillis()-10000));
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
            if (video_mp != null) {
                video_mp.seek(new Duration(video_mp.getCurrentTime().toMillis()+10000));
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
            if (video_mp != null) {
                if (!(video_mp.getStatus() == MediaPlayer.Status.PLAYING)) {
                    video_mp.play();
                    play_btn.setText("Pause");
                } else {
                    video_mp.pause();
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
            if (video_mp != null) {
                if (video_mp.isMute()) {
                    video_mp.setMute(false);
                } else {
                    video_mp.setMute(true);
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
            if (video_mp != null) {
                double selected = Double.parseDouble(video_speed.getSelectionModel().getSelectedItem().toString().replace("x", ""));
                video_mp.setRate(selected);
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

    private MediaPlayer video_mp;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void updateVideo(File video) {
        video_reproductor.getChildren().clear();
        video_mp = new MediaPlayer(new Media(video.toURI().toString()));
        if (!video.getName().substring(video.getName().length()-3).equals("mp3")) {
            video_mv = new MediaView(video_mp);
            video_mv.fitHeightProperty().bind(video_reproductor.heightProperty());
            video_mv.fitWidthProperty().bind(video_reproductor.widthProperty());
            video_reproductor.getChildren().add(video_mv);
        } else {
            Image audio = new Image("res/audio.png");
            ImageView audio_only = new ImageView(audio);
            video_reproductor.getChildren().add(audio_only);
        }
        video_mp.setRate(Double.parseDouble(video_speed.getSelectionModel().getSelectedItem().toString().replace("x", "")));
        bindVideo();
        play_btn.setText("Play");
    }

    public void bindVideo() {
        video_progress_bar.progressProperty().bind(
            Bindings.createDoubleBinding(
                () -> {
                    Duration currentTime = video_mp.getCurrentTime();
                    Duration duration = video_mp.getMedia().getDuration();
                    return currentTime.toMillis() / duration.toMillis();
                },
                video_mp.currentTimeProperty(),
                video_mp.getMedia().durationProperty())
        );
        EventHandler<MouseEvent> onClickAndDragHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                Duration dur = video_mp.getMedia().getDuration();
                Duration time = dur.multiply(me.getX() / video_progress_bar.getWidth());
                video_mp.seek(time);
                me.consume();
            }
        };
        video_progress_bar.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickAndDragHandler);
        video_progress_bar.addEventHandler(MouseEvent.MOUSE_DRAGGED, onClickAndDragHandler);

        video_mp.volumeProperty().bind(video_volume.valueProperty());
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
                        video_title.setText(videoBtn.getText().toUpperCase());
                    }
                });

                Text videoDesc = new Text(video.getName().substring(video.getName().lastIndexOf(".")+1).toUpperCase());
                videoDesc.getStyleClass().add("white-text");
                try {
                    MediaPlayer mpDuration = new MediaPlayer(new Media(video.toURI().toString()));
                    mpDuration.setOnReady(new Runnable() {
                        @Override
                        public void run() {
                            DecimalFormat df = new DecimalFormat("00");
                            int horas = (int)mpDuration.getMedia().getDuration().toHours();
                            int minutos = (int)(mpDuration.getMedia().getDuration().toMinutes()-horas*60);
                            int segundos = (int)(mpDuration.getMedia().getDuration().toSeconds()-minutos*60-horas*3600);

                            videoDesc.setText(videoDesc.getText() + ", " + df.format(horas) + ":" + df.format(minutos) + ":" + df.format(segundos));
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
        Alert addedToLibrary = new Alert(AlertType.INFORMATION);
        addedToLibrary.initModality(Modality.NONE);
        addedToLibrary.setHeaderText("Archivo añadido");
        addedToLibrary.setContentText("Se ha añadido un nuevo archivo a la librería.");
        FileChooser fc = new FileChooser();
        createLibrary(fc.showOpenDialog(primaryStage));
        addedToLibrary.show();
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
        this.video_mp.dispose();
        this.video_title.setText(null);
    }

    @FXML
    void showAboutUsDialog(ActionEvent event) {
        Alert aboutUs = new Alert(AlertType.INFORMATION);
        aboutUs.setTitle("Sobre nosotros:");
        aboutUs.setHeaderText(null);
        aboutUs.setContentText("Reproductor de vídeo\nSimón Fernández Tacón");
        aboutUs.show();
    }
}