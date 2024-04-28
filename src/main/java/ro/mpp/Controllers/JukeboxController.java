package ro.mpp.Controllers;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javazoom.jl.player.Player;
import ro.mpp.Domain.ExtendedStation;
import ro.mpp.Persistence.FavouriteStationsRepository;
import ro.mpp.Persistence.FileRepository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class JukeboxController {

    @FXML
    private TableView<ExtendedStation> stationTableView;
    @FXML
    private TableColumn<ExtendedStation, String> tableColumnName;
    @FXML
    private TableColumn<ExtendedStation, String> tableColumnState;
    @FXML
    private Label nameLabel;
    @FXML
    private Label placeholderLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Button favButton;
    @FXML
    private ListView<ExtendedStation> favouritesList;
    private final ObservableList<ExtendedStation> model = FXCollections.observableArrayList();
    private final ObservableList<ExtendedStation> modelFavourites = FXCollections.observableArrayList();
    private FileRepository<UUID, ExtendedStation> repository;
    private FavouriteStationsRepository favRepository;
    private Player player;
    private HttpURLConnection connection;
    private InputStream inputStream;


    public void setRepository(FileRepository<UUID, ExtendedStation> repository, FavouriteStationsRepository favRepository) {
        this.repository = repository;
        this.favRepository = favRepository;
        loadStations();
    }

    private void loadStations() {
        model.setAll(repository.getAll());
        modelFavourites.setAll(favRepository.getAll());
    }

    private void loadFavStations() {
        modelFavourites.setAll(favRepository.getAll());
    }

    @FXML
    void initialize() {
        stationTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ImageView img = new ImageView(new Image("/pics/default.png"));
        ImageView fav = new ImageView(new Image("/pics/fav.png"));
        img.setFitWidth(30);
        img.setFitHeight(30);
        fav.setFitWidth(30);
        fav.setFitHeight(30);
        favButton.setGraphic(img);
        stationTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameLabel.setVisible(true);
                placeholderLabel.setVisible(true);
                nameLabel.setText(stationTableView.getSelectionModel().getSelectedItem().getStation().getName());
                if (favRepository.findOne(stationTableView.getSelectionModel().getSelectedItem()))
                    favButton.setGraphic(fav);
                else
                    favButton.setGraphic(img);
            } else {
                nameLabel.setVisible(false);
                placeholderLabel.setVisible(false);
            }
        });
        for (TableColumn<?, ?> column : stationTableView.getColumns()) {
            column.setResizable(false);
        }
        tableColumnName.setCellValueFactory(station -> new SimpleStringProperty(station.getValue().getStation().getName()));
        tableColumnState.setCellValueFactory(cellData -> {
            String longName = cellData.getValue().getLongName();
            return new SimpleStringProperty(!Objects.equals(longName, " ") ? longName : "?");
        });
        favouritesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ExtendedStation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getStation().getName());
                }
            }
        });
        stationTableView.setItems(model);
        favouritesList.setItems(modelFavourites);

    }
    @FXML
    public void pressPlay() {
        Stage stage = (Stage) stationTableView.getScene().getWindow();

        stage.setOnCloseRequest(e -> pressStop());
        String streamUrl = stationTableView.getSelectionModel().getSelectedItem().getStation().getUrl();
        if (inputStream != null && connection != null) {
            try {
                inputStream.close();
                connection.disconnect();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        Thread playerThread = new Thread(() -> {
            try {
                URL url = new URL(streamUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                inputStream = new BufferedInputStream(connection.getInputStream());
                player = new Player(inputStream);
                player.play();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        playerThread.start();

    }

    @FXML
    public void pressStop() {
        Thread playerStopperThread = new Thread(() -> {
            try {
                inputStream.close();
                connection.disconnect();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
        playerStopperThread.start();
    }

    public void manageFav(ActionEvent event) {
        Image image = new Image("/pics/fav.png");
        Image def = new Image("/pics/default.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        ImageView imageViewDef = new ImageView(def);
        imageViewDef.setFitHeight(30);
        imageViewDef.setFitWidth(30);

        ExtendedStation station = stationTableView.getSelectionModel().getSelectedItem();
        if (station == null)
            return;
        if (!favRepository.findOne(station)) {
            favRepository.add(station);
            favButton.setGraphic(imageView);
        } else {
            favRepository.delete(station);
            favButton.setGraphic(imageViewDef);
        }
        loadFavStations();
    }

    public void deleteFav(ActionEvent event) {
        ExtendedStation favStation = favouritesList.getSelectionModel().getSelectedItem();
        if (favStation == null) {
            errorLabel.setVisible(true);
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
            pauseTransition.setOnFinished(e -> errorLabel.setVisible(false));
            pauseTransition.play();
            return;
        }
        favRepository.delete(favStation);
        loadFavStations();
    }
}
