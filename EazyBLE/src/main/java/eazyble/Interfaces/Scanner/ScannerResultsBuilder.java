package eazyble.Interfaces.Scanner;


// data model to hold information about scanned devices
public class ScannerResultsBuilder {
    private String deviceName;
    private int rssi;
    private String uuid;
    private String macAddress;

    public ScannerResultsBuilder(String deviceName, int rssi, String uuid, String macAddress) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.uuid = uuid;
        this.macAddress = macAddress;
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
}
