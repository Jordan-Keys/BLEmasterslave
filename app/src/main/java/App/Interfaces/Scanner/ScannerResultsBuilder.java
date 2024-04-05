package App.Interfaces.Scanner;

public class ScannerResultsBuilder {
    private String deviceName;
    private int rssi;
    private String uuid;

    public ScannerResultsBuilder(String deviceName, int rssi, String uuid) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.uuid = uuid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getRssi() {
        return rssi;
    }

    public String getUuid() {
        return uuid;
    }
}
