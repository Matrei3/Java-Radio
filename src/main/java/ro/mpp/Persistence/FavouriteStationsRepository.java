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

public class FavouriteStationsRepository extends FileRepository<UUID, ExtendedStation> {
    public FavouriteStationsRepository(Path filePath) {
        super(filePath);
        entities = new HashMap<>();
        loadStations();
    }

    /**
     * Load stations into memory when creating repository
     */
    @Override
    protected void loadStations() {
        String data;
        entities.clear();
        try (Stream<String> lines = Files.lines(filePath)) {
            data = lines.collect(Collectors.joining("\n"));
            if (!data.isEmpty()) {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray array = new JSONArray(jsonObject.get("stations").toString());
                array.forEach(station -> {
                    ExtendedStation extendedStation = StationCreator.createStationFromArrayObject(station);
                    UUID id = extendedStation.getStation().getStationUUID();
                    entities.put(id, extendedStation);

                });
            }
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * No need to bulk add favourite stations
     * @param entities - list of ExtendedStations to be added to JSON
     */
    @Override
    public void addAll(List<ExtendedStation> entities) {

    }

    @Override
    public List<ExtendedStation> getAll() {
        return new ArrayList<>(entities.values());
    }

    public void add(ExtendedStation entity) {
        String data;
        JSONObject jsonObject;
        if (entity == null)
            return;
        try (Stream<String> lines = Files.lines(filePath)) {
            data = lines.collect(Collectors.joining("\n"));
            if (!data.isEmpty()) {
                jsonObject = new JSONObject(data);
                if (entity.getStation().getGeoLongitude() != null && entity.getStation().getGeoLatitude() != null)
                    jsonObject.append("stations", String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), entity.getStation().getGeoLatitude().toString(), entity.getStation().getGeoLongitude().toString()));
                else
                    jsonObject.append("stations", String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), " ", " "));
            } else {
                //If it's the first time adding or file is empty we need to create the actual JSON not just append to it
                jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                if (entity.getStation().getGeoLongitude() != null && entity.getStation().getGeoLatitude() != null)
                    jsonArray.put(String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), entity.getStation().getGeoLatitude().toString(), entity.getStation().getGeoLongitude().toString()));
                else
                    jsonArray.put(String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), " ", " "));
                jsonObject.put("stations", jsonArray);
            }
            //For some reason can't be used in the try with resources because lines variable will be empty
            FileWriter fileWriter = new FileWriter(String.valueOf(filePath), false);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        } finally {
            loadStations();
        }

    }

    public void delete(ExtendedStation entity) {
        JSONObject jsObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (ExtendedStation station : entities.values()) {
            if (station.getStation().getStationUUID() != entity.getStation().getStationUUID()) {
                if (station.getStation().getGeoLongitude() != null && station.getStation().getGeoLatitude() != null)
                    jsonArray.put(String.join(";", station.getStation().getStationUUID().toString(), station.getStation().getName(), station.getStation().getUrl(), station.getStation().getTags(), station.getStation().getFavicon(), station.getStation().getGeoLatitude().toString(), station.getStation().getGeoLongitude().toString()));
                else
                    jsonArray.put(String.join(";", station.getStation().getStationUUID().toString(), station.getStation().getName(), station.getStation().getUrl(), station.getStation().getTags(), station.getStation().getFavicon(), " ", " "));
            }
        }
        jsObject.put("stations", jsonArray);
        try (FileWriter fileWriter = new FileWriter(String.valueOf(filePath), false)) {
            fileWriter.write(jsObject.toString());
            entities.remove(entity.getStation().getStationUUID());
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
    public Boolean findOne(ExtendedStation extendedStation){
        return entities.containsKey(extendedStation.getStation().getStationUUID());
    }
}
