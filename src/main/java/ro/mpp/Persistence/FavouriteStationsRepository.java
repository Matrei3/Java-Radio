package ro.mpp.Persistence;

import de.sfuhrm.radiobrowser4j.Station;
import org.json.JSONArray;
import org.json.JSONObject;
import ro.mpp.Domain.ExtendedStation;

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
                    String[] parts = station.toString().split(";");
                    UUID id = UUID.fromString(parts[0]);
                    String name = parts[1];
                    String url = parts[2];
                    List<String> tags = List.of(parts[3].split(","));
                    String icon = parts[4];
                    String longName = parts[5];
                    String shortName = parts[6];
                    Station createdStation = new Station();
                    createdStation.setStationUUID(id);
                    createdStation.setName(name);
                    createdStation.setUrl(url);
                    createdStation.setTagList(tags);
                    createdStation.setFavicon(icon);
                    ExtendedStation extendedStation = new ExtendedStation(createdStation, longName, shortName);
                    entities.put(id, extendedStation);

                });
            }
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Override
    public void addAll(List<ExtendedStation> entities) {

    }

    @Override
    public List<ExtendedStation> getAll() {
        return new ArrayList<>(entities.values());
    }

    public Boolean add(ExtendedStation entity) {
        String data;
        JSONObject jsonObject;
        if (entity == null)
            return false;
        try (Stream<String> lines = Files.lines(filePath)) {
            data = lines.collect(Collectors.joining("\n"));
            if (!data.isEmpty()) {
                jsonObject = new JSONObject(data);
                if (entity.getStation().getGeoLongitude() != null && entity.getStation().getGeoLatitude() != null)
                    jsonObject.append("stations", String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), entity.getStation().getGeoLatitude().toString(), entity.getStation().getGeoLongitude().toString()));
                else
                    jsonObject.append("stations", String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), " ", " "));
            } else {
                jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                if (entity.getStation().getGeoLongitude() != null && entity.getStation().getGeoLatitude() != null)
                    jsonArray.put(String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), entity.getStation().getGeoLatitude().toString(), entity.getStation().getGeoLongitude().toString()));
                else
                    jsonArray.put(String.join(";", entity.getStation().getStationUUID().toString(), entity.getStation().getName(), entity.getStation().getUrl(), entity.getStation().getTags(), entity.getStation().getFavicon(), " ", " "));
                jsonObject.put("stations", jsonArray);
            }
            FileWriter fileWriter = new FileWriter(String.valueOf(filePath), false);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
            return true;
        } catch (IOException exception) {
            return false;
        } finally {
            loadStations();
        }

    }

    public Boolean delete(ExtendedStation entity) {
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
            return true;
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            return false;
        }
    }
    public Boolean findOne(ExtendedStation extendedStation){
        return entities.containsKey(extendedStation.getStation().getStationUUID());
    }
}
