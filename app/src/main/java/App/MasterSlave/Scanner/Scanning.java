package App.MasterSlave.Scanner;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.*;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class Scanning {
    // declaring variables
    private BluetoothLeScanner bluetoothLeScanner;
    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
    private boolean scanning;
    //stop scanning after 10 seconds
    private static final long SCAN_PERIOD = 10000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;

    public Scanning(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkScanPermission();
        } else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }
    // method to check bluetooth permissions
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkScanPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    (int) REQUEST_BLUETOOTH_SCAN_PERMISSION);
        } else {
            // BLUETOOTH_SCAN permission is already granted
            Toast.makeText(context, "Scanning permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // method to start scanning
    public void scanLeDevices() {
        if (!scanning) {
            // Stops scanning after a predefined scanner period.
            handler.postDelayed(() -> {
                scanning = false;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkScanPermission();
                    }
                }
                bluetoothLeScanner.stopScan(scanCallback);
                Toast.makeText(context, "Scanning stopped due to timeout", Toast.LENGTH_SHORT).show();
            }, SCAN_PERIOD);

            scanning = true;
            ScanSettings settings = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build();
            }
            List<ScanFilter> filters = new ArrayList<>();
            bluetoothLeScanner.startScan(filters, settings, scanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
            Toast.makeText(context, "Scanning is already in Progress", Toast.LENGTH_SHORT).show();
        }

    }

    // call back for scanned results
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
            StringBuilder uuidMessage = new StringBuilder("UUIDs: ");
            if (uuids != null) {
                for (ParcelUuid uuid : uuids) {
                    uuidMessage.append(uuid.toString()).append("\n");
                }
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkScanPermission();
                }
            }
            String deviceName = result.getDevice().getName();
            int rssi = result.getRssi();
            // Checking if device name is null
            if (deviceName == null) {
                deviceName = "Unknown Device";
            }
            // Displaying device name and RSSI using a toast message
            String message = "Device Name: " + deviceName + "\nRSSI: " + rssi + "\n" + uuidMessage;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
}