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
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanning {
    private BluetoothLeScanner bluetoothLeScanner;
    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
    private boolean scanning;
    private static final long SCAN_PERIOD = 100000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;

    // Map to store scan results with MAC address as the key
    private final Map<String, String> scanResultsMap;

    public interface ScanResultListener {
        void onDeviceFound(Map<String, String> devices);
    }
    private ScanResultListener scanResultListener;

    public Scanning(Context context) {
        this.context = context;
        this.scanResultsMap = new HashMap<>();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkScanPermission();
        } else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    // Setter for the ScanResultListener
    public void setScanResultListener(ScanResultListener listener) {
        this.scanResultListener = listener;
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
            handler.postDelayed(() -> {
                scanning = false;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkScanPermission();
                    }
                }
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(scanCallback);
                    Toast.makeText(context, "Scanning stopped due to timeout", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);

            scanning = true;
            if (bluetoothLeScanner == null) {
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
            if (bluetoothLeScanner != null) {
                ScanSettings settings = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    settings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                            .build();
                }
                List<ScanFilter> filters = new ArrayList<>();
                bluetoothLeScanner.startScan(filters, settings, scanCallback);
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

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
            StringBuilder uuidMessage = new StringBuilder();
            if (uuids != null) {
                for (ParcelUuid uuid : uuids) {
                    uuidMessage.append(uuid.toString());
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

            // Filtering the data to be re-advertised
            // Trim the device name to only the first 4 characters
            String shortDeviceName = deviceName.length() > 4 ? deviceName.substring(0, 4) : deviceName;
            String deviceInfoForAdvertising = macAddress + shortDeviceName + rssi;
            Log.d("AdvertisingData", "Advertised: " + deviceInfoForAdvertising.toString());

            // Store the result in the map
            String deviceInfo = "Name: " + deviceName +
                    " RSSI: " + rssi +
                    " Proximity: " + proximity;

            // Store the result in the map
            scanResultsMap.put(macAddress, deviceInfo);
            Log.d("ScannedDevices", "HashMap: " + scanResultsMap.toString());

            // Notify the listener with the updated devices
            if (scanResultListener != null) {
                scanResultListener.onDeviceFound(scanResultsMap);
            }
        }
    };

    public void stopScanning() {
        if (scanning) {
            scanning = false;
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkScanPermission();
                }
            }
            bluetoothLeScanner.stopScan(scanCallback);
            Toast.makeText(context, "Scanning manually stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Scanning is not active", Toast.LENGTH_SHORT).show();
        }
    }
}
