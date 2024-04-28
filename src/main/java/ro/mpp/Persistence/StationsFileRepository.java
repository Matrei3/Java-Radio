package ro.mpp.Persistence;

import de.sfuhrm.radiobrowser4j.Station;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StationsFileRepository extends FileRepository<UUID, Station> {

    public StationsFileRepository(Path filePath) {
        super(filePath);
        entities = new HashMap<>();
        loadStations();
    }

    protected void loadStations() {
        String data;
        try (Stream<String> lines = Files.lines(filePath)) {
            data = lines.collect(Collectors.joining("\n"));
            JSONObject jsonObject = new JSONObject(data);
            JSONArray array = new JSONArray(jsonObject.get("stations").toString());
            array.forEach(station -> {
                String[] parts = station.toString().split(";");
                UUID id = UUID.fromString(parts[0]);
                String name = parts[1];
                String url = parts[2];
                List<String> tags = List.of(parts[3].split(","));
                String icon = parts[4];
                Double latitude = null;
                Double longitude = null;
                try {
                    latitude = Double.valueOf(parts[5]);
                    longitude = Double.valueOf(parts[6]);
                }
                catch (NumberFormatException ignored){}

                Station createdStation = new Station();
                createdStation.setStationUUID(id);
                createdStation.setName(name);
                createdStation.setUrl(url);
                createdStation.setTagList(tags);
                createdStation.setFavicon(icon);
                createdStation.setGeoLatitude(latitude);
                createdStation.setGeoLongitude(longitude);
                entities.put(id,createdStation);

            });
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Override
    public void addAll(List<Station> entities) {
        JSONObject jsObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Station station : entities) {
            if (station.getGeoLongitude() != null && station.getGeoLatitude() != null)
                jsonArray.put(String.join(";", station.getStationUUID().toString(), station.getName(), station.getUrl(), station.getTags(), station.getFavicon(), station.getGeoLatitude().toString(), station.getGeoLongitude().toString()));
            else
                jsonArray.put(String.join(";", station.getStationUUID().toString(), station.getName(), station.getUrl(), station.getTags(), station.getFavicon(),"",""));

        }
        jsObject.put("stations", jsonArray);
        try (FileWriter fileWriter = new FileWriter(String.valueOf(filePath))) {
            fileWriter.write(jsObject.toString());
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }


    public Boolean addFavourite(Station entity) {
        return null;
    }

    public Boolean deleteFavourite(Station entity) {
        return null;
    }

    @Override
    public List<Station> getAll() {
        return new ArrayList<>(entities.values());
    }
}
