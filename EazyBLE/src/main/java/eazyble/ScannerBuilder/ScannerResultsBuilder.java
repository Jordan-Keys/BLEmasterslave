package eazyble.ScannerBuilder;


// data model to hold information about scanned devices
public class ScannerResultsBuilder {
    private String deviceName;
    private int rssi;
    private String uuid;
    private String macAddress;
    private String proximity;

    public ScannerResultsBuilder(String deviceName, int rssi, String uuid, String macAddress, String proximity) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.uuid = uuid;
        this.macAddress = macAddress;
        this.proximity = proximity;
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
    public String getMacAddress() {
        return macAddress;
    }
    public String proximity() {
        return proximity;
    }
}
