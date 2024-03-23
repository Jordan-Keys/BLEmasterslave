//package App.MasterSlave;
//
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanFilter;
//import android.bluetooth.le.ScanResult;
//import android.bluetooth.le.ScanSettings;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.core.app.ActivityCompat;
//import com.example.blemasterslave.R;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Testing3 extends AppCompatActivity {
//
//    private BluetoothAdapter bluetoothAdapter;
//    private BluetoothLeScanner bluetoothLeScanner;
//    private boolean scanning = false;
//    private Handler handler = new Handler();
//    private static final long SCAN_PERIOD = 10000; // 10 seconds
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Permissions permissions = new Permissions();
//        permissions.checkBluetoothSupport(this);
//        setContentView(R.layout.activity_main);
//        Button button = findViewById(R.id.scanButton);
//        button.setOnClickListener(v -> {
//            scanLeDevice(true);
//        });
//
//        Button button1 = findViewById(R.id.scanButton1);
//        button1.setOnClickListener(v -> {
//            stopScan();
//        });
//
//        // if all the required permissions for scanning are granted
//        if (hasPermissions()) {
//            initializeBluetoothScan();
////            scanLeDevice(true);
//        } else {
//            requestPermissions();
//        }
//    }
//    // Results returned from bluetooth decision after requesting permission
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Permissions.handleOnActivityResult(this, requestCode, resultCode);
//    }
//
//    private void initializeBluetoothScan() {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        }
//    }
//    private boolean hasPermissions() {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
//                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//    private void requestPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Permission granted, initialize Bluetooth and start scanning
//            initializeBluetoothScan();
//        } else {
//            // Permission denied, handle accordingly
//            requestPermissions();
//            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // Stops scanning after a predefined scan period
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scanning = false;
//                    if (ActivityCompat.checkSelfPermission(Testing3.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(Testing3.this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
//                    }
//                    Toast.makeText(Testing3.this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
//                }
//            }, SCAN_PERIOD);
//
//            scanning = true;
//            ScanSettings settings = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                settings = new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                        .build();
//            }
//            List<ScanFilter> filters = new ArrayList<>(); // You can add filters if needed
//            bluetoothLeScanner.startScan(filters, settings, scanCallback);
//        } else {
//            scanning = false;
//            bluetoothLeScanner.stopScan(scanCallback);
//        }
//    }
//
//    private final ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            if (ActivityCompat.checkSelfPermission(Testing3.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                if (!hasPermissions()) {
//                    // if permission is not granted
//                    Toast.makeText(Testing3.this, "Permission denied", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            String deviceName = result.getDevice().getName();
//            int rssi = result.getRssi();
//            // Check if device name is null and provide a default value
//            if (deviceName == null) {
//                deviceName = "Unknown Device";
//            }
//
//            // Display device name and RSSI using a toast message
//            String message = "Device Name: " + deviceName + "\nRSSI: " + rssi;
//            Toast.makeText(Testing3.this, message, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            Toast.makeText(Testing3.this, "Scanning batch", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Toast.makeText(Testing3.this, "failed to scan", Toast.LENGTH_SHORT).show();
//        }
//    };
//    public void stopScan() {
//        if (scanning) {
//            scanning = false;
//            if (ActivityCompat.checkSelfPermission(Testing3.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                if (!hasPermissions()) {
//                    // if no scanning permissions
//                }
//            }
//            bluetoothLeScanner.stopScan(scanCallback);
//            Toast.makeText(Testing3.this, "Scanning stopped", Toast.LENGTH_SHORT).show();
//        } else {
//            // Scanning is not currently active
//            Toast.makeText(Testing3.this, "Scanning is not active", Toast.LENGTH_SHORT).show();
//        }
//    }
//}