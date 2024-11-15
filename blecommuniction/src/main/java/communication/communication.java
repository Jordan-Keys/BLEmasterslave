package communication;//package communication;
//
//
//import android.util.Log;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class communication {
//    // HashMap to store scanned devices with MAC address as the key
//    private final Map<String, DeviceInfo> deviceMap;
//
//    public communication() {
//        deviceMap = new HashMap<>();
//    }
//
//    // Method to receive scanned device information and the badge/message
//    public void receiveScannedDevice(String macAddress, int rssi, String badge) {
//        DeviceInfo deviceInfo = new DeviceInfo(macAddress, rssi, badge);
//        deviceMap.put(macAddress, deviceInfo);
//        Log.d("BluetoothScanner", "Device MAC: " + macAddress);
//        Log.d("BluetoothScanner", "RSSI: " + rssi);
//        Log.d("BluetoothScanner", "Badge: " + badge);
//
//    }
//
//
//    // Inner class to hold device information
//    public static class DeviceInfo {
//        private final String macAddress;
//        private final int rssi;
//        private final String badge; // Renamed from message to badge
//
//        public DeviceInfo(String macAddress, int rssi, String badge) {
//            this.macAddress = macAddress;
//            this.rssi = rssi;
//            this.badge = badge;
//        }
//
//        @Override
//        public String toString() {
//            return "DeviceInfo{" +
//                    "macAddress='" + macAddress + '\'' +
//                    ", rssi=" + rssi +
//                    ", badge='" + badge + '\'' +
//                    '}';
//        }
//    }
//}
//


import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class communication {
    // HashMap to store scanned devices
    private final Map<String, DeviceInfo> deviceMap;
    public communication() {
        deviceMap = new HashMap<>();
    }
    // Method to receive scanned device information and the badge/message
    public void receiveScannedDevice(String macAddress, int rssi, String badge) {
        DeviceInfo deviceInfo = new DeviceInfo(rssi, badge);
        deviceMap.put(macAddress, deviceInfo);

//        for debugging
        Log.d("BluetoothScanner", "Device MAC: " + macAddress);
        Log.d("BluetoothScanner", "RSSI: " + rssi);
        Log.d("BluetoothScanner", "Badge: " + badge);
    }

    // Method to retrieve devices in the HashMap
    public Map<String, DeviceInfo> getHashMap() {
        return deviceMap;
    }

    // Inner class to hold device information without macAddress, as it's already the key
    public static class DeviceInfo {
        private final int rssi;
        private final String badge; // Renamed from message to badge

        public DeviceInfo(int rssi, String badge) {
            this.rssi = rssi;
            this.badge = badge;
        }

        public int getRssi() {
            return rssi;
        }

        public String getBadge() {
            return badge;
        }

        @Override
        public String toString() {
            return "DeviceInfo{" +
                    "rssi=" + rssi +
                    ", badge='" + badge + '\'' +
                    '}';
        }
    }

    // method to get devices by macAddresses
    public DeviceInfo getDeviceInfo(String macAddress) {
        return deviceMap.get(macAddress);
    }

    // for debugging
    public void logDeviceMap() {
        for (Map.Entry<String, DeviceInfo> entry : deviceMap.entrySet()) {
            String macAddress = entry.getKey();
            DeviceInfo deviceInfo = entry.getValue();
            Log.d("DeviceMap", "MAC Address: " + macAddress + ", RSSI: " + deviceInfo.getRssi() + ", Badge: " + deviceInfo.getBadge());
        }
    }



}
