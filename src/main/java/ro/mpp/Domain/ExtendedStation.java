package ro.mpp.Domain;

import de.sfuhrm.radiobrowser4j.Station;

/**
 * Mockingly extends de.sfuhrm.radiobrowser4j.Station since
 * it's a final class and adds state short and long name
 */
public class ExtendedStation {
    private Station station;
    private String longName;
    private String shortName;

    public ExtendedStation() {
    }

    public ExtendedStation(Station station) {
        this.station = station;
    }

    public ExtendedStation(Station station, String longName, String shortName) {
        this.station = station;
        this.longName = longName;
        this.shortName = shortName;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return "ExtendedStation{" +
                "station=" + station +
                ", longName='" + longName + '\'' +
                ", shortName='" + shortName + '\'' +
                '}';
    }
}
