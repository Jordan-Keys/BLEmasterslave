package eazyble.MasterSlave.Scanner;

import eazyble.Permissions;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Scanning {
    private BluetoothLeScanner bluetoothLeScanner;
    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
    private boolean scanning;
    private static final long SCAN_PERIOD = 30000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private final List<ScannerResultsBuilder> scanResults;
    private final ScanResultAdapter adapter;
    private final BluetoothAdapter bluetoothAdapter;
    private Runnable timeoutRunnable;
    private String receivedScannedData = null;

    public Scanning(Context context, List<ScannerResultsBuilder> scanResults, ScanResultAdapter adapter) {
        this.context = context;
        this.scanResults = scanResults;
        this.adapter = adapter;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkScanPermission();
        } else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkScanPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    (int) REQUEST_BLUETOOTH_SCAN_PERMISSION);
        }
    }

    public void scanLeDevices() {
        Permissions.checkBluetoothSupport((Activity) context);

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Please Enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!scanning) {
            scanResults.clear();
            adapter.notifyDataSetChanged();
            timeoutRunnable = () -> {
                scanning = false;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkScanPermission();
                    }
                }
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(scanCallback);
                    Toast.makeText(context, "Scanning stopped due to timeout", Toast.LENGTH_SHORT).show();
                    logDeviceData();
                }
            };
            handler.postDelayed(timeoutRunnable, SCAN_PERIOD);

            scanning = true;
            if (bluetoothLeScanner == null) {
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
            if (bluetoothLeScanner != null) {
                ScanSettings settings = getScanSettings();
                List<ScanFilter> filters = new ArrayList<>();
                bluetoothLeScanner.startScan(filters, settings, scanCallback);
                Toast.makeText(context, "Scanning Started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Bluetooth is turned off", Toast.LENGTH_SHORT).show();
            }
        } else {
            scanning = false;
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(scanCallback);
                Toast.makeText(context, "Scanning is already in progress", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ScanSettings getScanSettings() {
        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (bluetoothAdapter.isLeExtendedAdvertisingSupported()) {
                settingsBuilder.setLegacy(false)
                        .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED);
            }
        }

        return settingsBuilder.build();
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkScanPermission();
                }
            }

            String macAddress = result.getDevice().getAddress();
            String deviceName = result.getDevice().getName();
            if (deviceName == null || deviceName.isEmpty()) {
                return;
            }

            int rssi = result.getRssi();
            int veryClose = -50;
            int close = -70;
            String proximity;
            if (rssi >= veryClose) {
                proximity = "Near";
            } else if (rssi >= close) {
                proximity = "Close";
            } else {
                proximity = "Far";
            }

            String advertisedString = null;
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord != null) {
                SparseArray<byte[]> manufacturerData = scanRecord.getManufacturerSpecificData();
                for (int i = 0; i < manufacturerData.size(); i++) {
                    manufacturerData.keyAt(i);
                    byte[] data = manufacturerData.valueAt(i);
                    if (data != null) {
                        advertisedString = new String(data, StandardCharsets.UTF_8);
                        break;
                    }
                }
            }

            // Check if advertised data looks like a scanned result
            if (isScannedDataFormat(advertisedString)) {
                receivedScannedData = advertisedString;
            }

            // Find the existing device by MAC address and update it
            boolean deviceUpdated = false;
            for (int i = 0; i < scanResults.size(); i++) {
                ScannerResultsBuilder scannedDevice = scanResults.get(i);
                if (scannedDevice.getMacAddress().equals(macAddress)) {
                    // Update the existing device
                    scannedDevice.setRssi(rssi);
                    scannedDevice.setProximity(proximity);
                    scannedDevice.setUuid(advertisedString != null ? advertisedString : "N/A");
                    deviceUpdated = true;
                    break;
                }
            }

            // If no device was found with the same MAC address, add a new one
            if (!deviceUpdated) {
                scanResults.add(new ScannerResultsBuilder(deviceName, rssi, advertisedString != null ? advertisedString : "N/A", macAddress, proximity));
            }

            adapter.notifyDataSetChanged();
        }
    };

    public void stopScanning() {
        if (scanning) {
            scanning = false;
            if (timeoutRunnable != null) {
                handler.removeCallbacks(timeoutRunnable);
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkScanPermission();
                }
            }
            bluetoothLeScanner.stopScan(scanCallback);
            Toast.makeText(context, "Scanning manually stopped", Toast.LENGTH_SHORT).show();
            logDeviceData();
        } else {
            Toast.makeText(context, "Scanning is not active", Toast.LENGTH_SHORT).show();
        }
    }

    private void logDeviceData() {
        String currentData = buildScannedDataString();

        String combined;
        if (isScannedDataFormat(receivedScannedData)) {
            combined = receivedScannedData + "; " + currentData;
        } else {
            combined = currentData;
        }

        // Pass the combined data to the processor for processing
        ResultsProcessor processor = ResultsProcessor.getInstance();
        processor.processScannedData(combined);
    }

    private String buildScannedDataString() {
        StringBuilder stringBuilder = new StringBuilder();
        String scanningDeviceName = bluetoothAdapter.getName();

        if (scanningDeviceName != null && !scanningDeviceName.isEmpty()) {
            if (scanningDeviceName.length() > 4) {
                scanningDeviceName = scanningDeviceName.substring(0, 4);
            }
            stringBuilder.append(scanningDeviceName).append(":");
        }

        for (ScannerResultsBuilder device : scanResults) {
            String deviceName = device.getDeviceName();
            int rssi = device.getRssi();
            String macAddress = device.getMacAddress().replace(":", "");

            if (deviceName.length() > 4) {
                deviceName = deviceName.substring(0, 4);
            }

            stringBuilder.append(macAddress).append(deviceName).append(rssi).append(", ");
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }

    private boolean isScannedDataFormat(String data) {
        return data != null && data.contains(":") && data.length() > 6;
    }
}
