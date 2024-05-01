package ro.mpp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ro.mpp.Controllers.JukeboxController;
import ro.mpp.Domain.ExtendedStation;
import ro.mpp.Persistence.StationsRepository;
import ro.mpp.Persistence.FavouriteStationsRepository;
import ro.mpp.Persistence.FileRepository;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Used to create the main window which will run the application
 */

public class Radio extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileRepository<UUID, ExtendedStation> fileRepository = new StationsRepository(Paths.get("src/main/resources/data/extended-stations.json"));
        FavouriteStationsRepository favouriteStationsRepository = new FavouriteStationsRepository(Paths.get("src/main/resources/data/favourite-stations.json"));
//
        //These lines were used for the creation of JAR

//        FileRepository<UUID, ExtendedStation> fileRepository = new StationsRepository(Paths.get("resources/data/extended-stations.json"));
//        FavouriteStationsRepository favouriteStationsRepository = new FavouriteStationsRepository(Paths.get("resources/data/favourite-stations.json"));

        //Used for various operations using reverse geolocation

//        Properties clientProperties = new Properties();
//        try {
//            clientProperties.load(Radio.class.getResourceAsStream("/app.properties"));
//        } catch (IOException exception) {
//            System.err.println("Cannot find client.properties");
//            return;
//        }
//        String apiKey = clientProperties.getProperty("apikey");
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey(apiKey)
//                .build();
//        GoogleUtils googleUtils = new GoogleUtils(context);
        FXMLLoader loader = new FXMLLoader(Radio.class.getResource("/views/jukebox.fxml"));
        Parent root = loader.load();
        JukeboxController controller = loader.getController();
        controller.setRepository(fileRepository, favouriteStationsRepository);
        primaryStage.getIcons().add(new Image("/pics/jukebox.png"));
        primaryStage.setTitle("Matrei's Jukebox");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        Alert informationAlert = new Alert(Alert.AlertType.WARNING);
        informationAlert.setContentText("Not all stations work due to API limitations");
        informationAlert.setTitle("API Errors");
        Stage stage = (Stage) informationAlert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/pics/warning.png"));
        informationAlert.setHeaderText("Sorry");
        informationAlert.showAndWait();
    }
}