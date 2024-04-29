package ro.mpp.Persistence;

import de.sfuhrm.radiobrowser4j.Station;
import org.json.JSONArray;
import org.json.JSONObject;
import ro.mpp.Domain.ExtendedStation;
import ro.mpp.Utils.StationCreator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StationsRepository extends FileRepository<UUID, ExtendedStation> {

    public StationsRepository(Path filePath) {
        super(filePath);
        entities = new HashMap<>();
        loadStations();
    }

    /**
     * When creating repository instance load objects into memory
     */
    protected void loadStations() {
        String data;
        try (Stream<String> lines = Files.lines(filePath)) {
            data = lines.collect(Collectors.joining("\n"));
            JSONObject jsonObject = new JSONObject(data);
            JSONArray array = new JSONArray(jsonObject.get("stations").toString());
            array.forEach(station -> {
                ExtendedStation extendedStation = StationCreator.createStationFromArrayObject(station);
                UUID id = extendedStation.getStation().getStationUUID();
                entities.put(id,extendedStation);
            });
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * One time use function for creating the JSON where the data will be stored
     * @param entities - list of ExtendedStations to be written into filePath file
     */
    @Override
    public void addAll(List<ExtendedStation> entities) {
        JSONObject jsObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (ExtendedStation station : entities) {
            //Making sure values are not null so the JSON structure will not be affected
            if (station.getStation().getGeoLongitude() != null && station.getStation().getGeoLatitude() != null)
                jsonArray.put(String.join(";", station.getStation().getStationUUID().toString(), station.getStation().getName(), station.getStation().getUrl(), station.getStation().getTags(), station.getStation().getFavicon(), station.getStation().getGeoLatitude().toString(), station.getStation().getGeoLongitude().toString()));
            else
                jsonArray.put(String.join(";", station.getStation().getStationUUID().toString(), station.getStation().getName(), station.getStation().getUrl(), station.getStation().getTags(), station.getStation().getFavicon()," "," "));

        }
        jsObject.put("stations", jsonArray);
        try (FileWriter fileWriter = new FileWriter(String.valueOf(filePath))) {
            fileWriter.write(jsObject.toString());
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }

    }

    /**
     * Used to get all stations loaded into memory
     * @return a list of all ExtendedStations
     */
    @Override
    public List<ExtendedStation> getAll() {
        return new ArrayList<>(entities.values());
    }
}
