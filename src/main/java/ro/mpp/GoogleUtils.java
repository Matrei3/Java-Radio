package ro.mpp;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import de.sfuhrm.radiobrowser4j.Station;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import ro.mpp.Domain.ExtendedStation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleUtils {
    private final GeoApiContext context;

    public GoogleUtils(GeoApiContext context) {
        this.context = context;
    }


    private Pair<String, String> getStateNamesFromCoordinates(Double latitude, Double longitude) {
        GeocodingApiRequest req = GeocodingApi.newRequest(context).latlng(new LatLng(latitude, longitude));
        try {
            GeocodingResult[] results = req.await();
            for (GeocodingResult result : results) {
                for (AddressComponent component : result.addressComponents) {
                    for (AddressComponentType type : component.types) {
                        if (type == AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1) {
                            return new Pair<>(component.longName, component.shortName);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public List<ExtendedStation> createExtendedStations(List<Station> stations) {
        List<ExtendedStation> extendedStations = new ArrayList<>();
        stations.forEach(station -> {
            if (station.getGeoLatitude() != null & station.getGeoLongitude() != null) {
                Pair<String, String> stateNames = getStateNamesFromCoordinates(station.getGeoLatitude(), station.getGeoLongitude());
                ExtendedStation extendedStation;
                if (stateNames != null)
                    extendedStation = new ExtendedStation(station, stateNames.getKey(), stateNames.getValue());
                else
                    extendedStation = new ExtendedStation(station);

                extendedStations.add(extendedStation);
            } else {
                ExtendedStation extendedStation = new ExtendedStation(station);
                extendedStations.add(extendedStation);
            }

        });
        return extendedStations;
    }

    public void createExtendedStationsJson(List<Station> stations) {
        List<ExtendedStation> extendedStations = createExtendedStations(stations);
        JSONObject jsObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (ExtendedStation station : extendedStations) {
            if (station.getShortName() != null && station.getLongName() != null)
                jsonArray.put(String.join(";", station.getStation().getStationUUID().toString(), station.getStation().getName(), station.getStation().getUrl(), station.getStation().getTags(), station.getStation().getFavicon(), station.getLongName(), station.getShortName()));
            else
                jsonArray.put(String.join(";", station.getStation().getStationUUID().toString(), station.getStation().getName(), station.getStation().getUrl(), station.getStation().getTags(), station.getStation().getFavicon(), "", ""));

        }
        jsObject.put("stations", jsonArray);
        try (FileWriter fileWriter = new FileWriter("src/main/resources/extended-stations.json")) {
            fileWriter.write(jsObject.toString());
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
