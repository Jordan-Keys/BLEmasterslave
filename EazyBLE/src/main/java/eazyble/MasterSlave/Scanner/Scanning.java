//package eazyble.MasterSlave.Scanner;
//
//import eazyble.Permissions;
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.*;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.ParcelUuid;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Scanning {
//    private BluetoothLeScanner bluetoothLeScanner;
//    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
//    private boolean scanning;
//    private static final long SCAN_PERIOD = 100000;
//    private final Handler handler = new Handler(Looper.getMainLooper());
//    private final Context context;
//    private final BluetoothAdapter bluetoothAdapter;
//
//    // Map to store scan results with MAC address as the key
//    private final Map<String, String> scanResultsMap;
//
//    public interface ScanResultListener {
//        void onDeviceFound(Map<String, String> devices);
//    }
//    private ScanResultListener scanResultListener;
//
//    public Scanning(Context context) {
//        this.context = context;
//        this.scanResultsMap = new HashMap<>();
//        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            checkScanPermission();
//        } else {
//            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        }
//    }
//
//    // Setter for the ScanResultListener
//    public void setScanResultListener(ScanResultListener listener) {
//        this.scanResultListener = listener;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    private void checkScanPermission() {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
//                    (int) REQUEST_BLUETOOTH_SCAN_PERMISSION);
//        }
//    }
//
//    public void scanLeDevices() {
//        Permissions.checkBluetoothSupport((Activity) context);
//        if (!bluetoothAdapter.isEnabled()) {
//            Toast.makeText(context, "Please Enable Bluetooth", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        scanResultsMap.clear();
//        if (!scanning) {
//            handler.postDelayed(() -> {
//                scanning = false;
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        checkScanPermission();
//                    }
//                }
//                if (bluetoothLeScanner != null) {
//                    bluetoothLeScanner.stopScan(scanCallback);
//                    Toast.makeText(context, "Scanning stopped due to timeout", Toast.LENGTH_SHORT).show();
//
//                    // Generate final compact string after scanning ends
//                    String finalAdString = getFinalCompactAdvertisingString();
//                    Log.d("FinalAdString", finalAdString);
//                }
//            }, SCAN_PERIOD);
//
//            scanning = true;
//            if (bluetoothLeScanner == null) {
//                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//            }
//            if (bluetoothLeScanner != null) {
//                ScanSettings.Builder settingsBuilder = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    settingsBuilder = new ScanSettings.Builder()
//                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//                }
//
//                // Add extended advertising support if available
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    if (bluetoothAdapter.isLeExtendedAdvertisingSupported()) {
//                        settingsBuilder.setLegacy(false) // Look for extended advertising
//                                .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED); // Scan on all PHYs
//                    }
//                }
//
//                ScanSettings settings = settingsBuilder.build();
//
//                List<ScanFilter> filters = new ArrayList<>();
//                bluetoothLeScanner.startScan(filters, settings, scanCallback);
//                Toast.makeText(context, "Started scanning for extended advertisements", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "Bluetooth is turned off", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            scanning = false;
//            if (bluetoothLeScanner != null) {
//                bluetoothLeScanner.stopScan(scanCallback);
//                Toast.makeText(context, "Scanning is already in progress", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private final ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
//            StringBuilder uuidMessage = new StringBuilder();
//            if (uuids != null) {
//                for (ParcelUuid uuid : uuids) {
//                    uuidMessage.append(uuid.toString());
//                }
//            }
//
//            String macAddress = result.getDevice().getAddress();
//            String deviceName = result.getDevice().getName();
//            if (deviceName == null || deviceName.isEmpty()) {
//                return;
//            }
//
//            int rssi = result.getRssi();
//
//            int veryClose = -50;
//            int close = -70;
//            String proximity;
//            if (rssi >= veryClose) {
//                proximity = "Near";
//            } else if (rssi >= close) {
//                proximity = "Close";
//            } else {
//                proximity = "Far";
//            }
//
//            // Store the result in the map
//            String deviceInfo = "Name: " + deviceName + " RSSI: " + rssi + " Proximity: " + proximity;
//
//            // Store the result in the map
//            scanResultsMap.put(macAddress, deviceInfo);
//            Log.d("ScannedDevices", "HashMap: " + scanResultsMap.toString());
//
//            // Notify the listener with the updated devices
//            if (scanResultListener != null) {
//                scanResultListener.onDeviceFound(scanResultsMap);
//            }
//        }
//    };
//
//    public void stopScanning() {
//        if (scanning) {
//            scanning = false;
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    checkScanPermission();
//                }
//            }
//            bluetoothLeScanner.stopScan(scanCallback);
//            Toast.makeText(context, "Scanning manually stopped", Toast.LENGTH_SHORT).show();
//            scanResultsMap.clear();
//            // Generate final compact string after scanning stops
//            String finalAdString = getFinalCompactAdvertisingString();
//            Log.d("FinalAdString", finalAdString);
//        } else {
//            Toast.makeText(context, "Scanning is not active", Toast.LENGTH_SHORT).show();
//        }
//    }
//    public String getFinalCompactAdvertisingString() {
//        // Construct final compact string: MAC-Name-RSSI, MAC-Name-RSSI
//        StringBuilder finalAdStringBuilder = new StringBuilder();
//        for (Map.Entry<String, String> entry : scanResultsMap.entrySet()) {
//            String macAddress = entry.getKey();
//            String deviceInfo = entry.getValue();
//            String[] parts = deviceInfo.split(" ");
//            String deviceName = "";
//            String rssi = "";
//            for (int i = 0; i < parts.length; i++) {
//                if (parts[i].equals("Name:")) {
//                    deviceName = parts[i + 1];
//                    if (deviceName.length() > 5) {
//                        deviceName = deviceName.substring(0, 5);
//                    }
//                }
//                if (parts[i].equals("RSSI:")) {
//                    rssi = parts[i + 1];
//                }
//            }
//            // Generate the compact string with trimmed device name
//            String compactString = macAddress.replace(":", "") + deviceName + rssi;
//            if (finalAdStringBuilder.length() > 0) {
//                finalAdStringBuilder.append(",");
//            }
//            finalAdStringBuilder.append(compactString);
//        }
//        return finalAdStringBuilder.toString();
//    }
//}