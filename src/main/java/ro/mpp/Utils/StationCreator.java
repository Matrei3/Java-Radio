package ro.mpp.Utils;

import de.sfuhrm.radiobrowser4j.Station;
import org.json.JSONArray;
import ro.mpp.Domain.ExtendedStation;

import java.util.List;
import java.util.UUID;

public class StationCreator {
    public static ExtendedStation createStationFromArrayObject(Object station){
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
        return new ExtendedStation(createdStation,longName,shortName);

    }
}
