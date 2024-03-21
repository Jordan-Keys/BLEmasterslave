package App.MasterSlave;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.example.blemasterslave.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Testing1 extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning = false;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 10000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure that the required permissions are granted
        if (hasPermissions()) {
            initializeBluetooth();
            scanLeDevice(true);
        } else {
            requestPermissions();
        }
    }

    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not available or not enabled
            Toast.makeText(this, "Bluetooth is not available or not enabled.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        } else {
            // Bluetooth is available and enabled
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    private boolean hasPermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, initialize Bluetooth and start scanning
            initializeBluetooth();
            scanLeDevice(true);
        } else {
            // Permission denied, handle accordingly
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a predefined scan period
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    if (ActivityCompat.checkSelfPermission(Testing1.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(Testing1.this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(Testing1.this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);

            scanning = true;
            ScanSettings settings = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build();
            }
            List<ScanFilter> filters = new ArrayList<>(); // You can add filters if needed
            bluetoothLeScanner.startScan(filters, settings, scanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (ActivityCompat.checkSelfPermission(Testing1.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (!hasPermissions()) {
                    // Handle the case where the permission is not granted
                    Toast.makeText(Testing1.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            String deviceName = result.getDevice().getName();
            int rssi = result.getRssi();
            // Check if device name is null and provide a default value
            if (deviceName == null) {
                deviceName = "Unknown Device";
            }

            // Display device name and RSSI using a toast message
            String message = "Device Name: " + deviceName + "\nRSSI: " + rssi;
            Toast.makeText(Testing1.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Toast.makeText(Testing1.this, "Multiple testing", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(Testing1.this, "failed to scan", Toast.LENGTH_SHORT).show();
        }
    };
}

