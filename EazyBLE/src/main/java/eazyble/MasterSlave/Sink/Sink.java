package eazyble.MasterSlave.Sink;

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
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sink {
    // Declaring variables
    private BluetoothLeScanner bluetoothLeScanner;
    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
    private boolean listening;
    private static final long SCAN_PERIOD = 10000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "SinkLog";
    private final HashMap<String, String> scannedDevices = new HashMap<>();
    private Runnable timeoutRunnable;
    private final Set<String> advertisedStrings = new HashSet<>();

    public Sink(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkScanPermission();
        } else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    // Method to check Bluetooth permissions
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkScanPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    (int) REQUEST_BLUETOOTH_SCAN_PERMISSION);
        }
    }

    // Method to start listening
    public void startSink() {
        Permissions.checkBluetoothSupport((Activity) context);
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Please Enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!listening) {
            timeoutRunnable = () -> {
                listening = false;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkScanPermission();
                    }
                }
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(scanCallback);
                    Toast.makeText(context, "Sink stopped due to timeout", Toast.LENGTH_SHORT).show();
                    // Log final HashMap
                    Log.d(TAG, "Final Scanned Devices:");
                    for (String mac : scannedDevices.keySet()) {
                        Log.d(TAG, mac + " -> " + scannedDevices.get(mac));
                    }
                    DataProcessor.handleScannedData(scannedDevices, advertisedStrings);
                }
            };
            handler.postDelayed(timeoutRunnable, SCAN_PERIOD);

            listening = true;
            if (bluetoothLeScanner == null) {
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
            if (bluetoothLeScanner != null) {
                ScanSettings settings = getScanSettings();
                List<ScanFilter> filters = new ArrayList<>();
                bluetoothLeScanner.startScan(filters, settings, scanCallback);
                Toast.makeText(context, "Sink started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Bluetooth is turned off", Toast.LENGTH_SHORT).show();
            }
        }else {
            listening = false;
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(scanCallback);
                Toast.makeText(context, "Scanning is already in progress", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ScanSettings getScanSettings() {
        ScanSettings.Builder settingsBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsBuilder = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (bluetoothAdapter.isLeExtendedAdvertisingSupported()) {
                settingsBuilder.setLegacy(false)
                        .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED);
            }
        }

        assert settingsBuilder != null;
        return settingsBuilder.build();
    }

    // Callback for listened results
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkScanPermission();
                }
            }

            // Get device information
            String macAddress = result.getDevice().getAddress();
            String deviceName = result.getDevice().getName();
            if (deviceName == null || deviceName.isEmpty()) {
                return;
            }
            int rssi = result.getRssi();

            scannedDevices.put(macAddress, deviceName + " RSSI: " + rssi);
            Log.d(TAG, "Added: " + macAddress + " -> " + scannedDevices.get(macAddress));


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

            //  Check if advertised data looks like a scanned result
            if (isScannedDataFormat(advertisedString)) {
                Log.d(TAG, "Final Scanned String:"+ advertisedString);
                advertisedStrings.add(advertisedString);

            }
            // Send data to the server immediately as its being scanned...
            //DataProcessor.handleScannedData(scannedDevices, advertisedStrings);
        }
    };


    private boolean isScannedDataFormat(String data) {
        return data != null && data.contains(":") && data.length() > 6;
    }

    public void stopSink() {
        if (listening) {
            listening = false;
            if (timeoutRunnable != null) {
                handler.removeCallbacks(timeoutRunnable);
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkScanPermission();
                }
            }
            bluetoothLeScanner.stopScan(scanCallback);
            Toast.makeText(context, "Sink manually stopped", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Final Scanned Devices:");
            for (String mac : scannedDevices.keySet()) {
                Log.d(TAG, mac + " -> " + scannedDevices.get(mac));
            }
            DataProcessor.handleScannedData(scannedDevices, advertisedStrings);
            // Clear for next scan so it doesn't reuse old ones
            scannedDevices.clear();
            advertisedStrings.clear();
        } else {
            Toast.makeText(context, "Sink is not active", Toast.LENGTH_SHORT).show();
        }
    }

}