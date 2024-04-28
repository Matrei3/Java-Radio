package ro.mpp;

import com.google.maps.GeoApiContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ro.mpp.Controllers.JukeboxController;
import ro.mpp.Domain.ExtendedStation;
import ro.mpp.Persistence.ExtendedStationsFileRepository;
import ro.mpp.Persistence.FavouriteStationsRepository;
import ro.mpp.Persistence.FileRepository;

import java.nio.file.Paths;
import java.util.UUID;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FileRepository<UUID, ExtendedStation> fileRepository = new ExtendedStationsFileRepository(Paths.get("src/main/resources/data/extended-stations.json"));
        FavouriteStationsRepository favouriteStationsRepository = new FavouriteStationsRepository(Paths.get("src/main/resources/data/favourite-stations.json"));
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCo4SI59L77dBbRUFlRvepWy93AhkWh8BQ")
                .build();
        GoogleUtils googleUtils = new GoogleUtils(context);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/jukebox.fxml"));
        Parent root = loader.load();
        JukeboxController controller = loader.getController();
        controller.setRepository(fileRepository,favouriteStationsRepository);
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