package eazyble.MasterSlave;//package App.MasterSlave;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanFilter;
//import android.bluetooth.le.ScanResult;
//import android.bluetooth.le.ScanSettings;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import com.example.blemasterslave.R;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Gatt_Client extends AppCompatActivity {
//    private BluetoothLeScanner bluetoothLeScanner;
//    static BluetoothAdapter bluetoothAdapter;
//    private boolean scanning = false;
//    private final Handler handler = new Handler(Looper.getMainLooper());
//    private static final long SCAN_PERIOD = 10000; // 10 seconds
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();
////        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        Permissions permissions = new Permissions();
//        permissions.checkBluetoothSupport(this);
//        setContentView(R.layout.advertising);
//        Button scanButton = findViewById(R.id.scanButton);
//        scanButton.setOnClickListener(v -> scanLeDevice(true));
//        Button stopButton = findViewById(R.id.stopButton);
//        stopButton.setOnClickListener(v -> stopScan());
//        Button closeButton = findViewById(R.id.closeButton);
//        closeButton.setOnClickListener(v -> System.exit(0));
//
//        // Ensure that the required permissions are granted
//        if (hasPermissions()) {
//            checkScanPermission(this);
//        } else {
//            requestPermissions();
//        }
//    }
//    // Results returned from bluetooth decision after requesting permission
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Permissions.handleOnActivityResult(this, requestCode, resultCode);
////        Permissions.handleOnActivityResult(this, requestCode, resultCode);
//    }
//    public void checkScanPermission(Context context) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "Scanning already granted", Toast.LENGTH_SHORT).show();
//        } else {
//            // BLUETOOTH_SCAN permission is not granted
//            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        }
//    }
//
//    private boolean hasPermissions() {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
//                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // Check if the request is for location permission
//        if(requestCode==Permissions.REQUEST_LOCATION_PERMISSION){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                Permissions.grantedPermission(this);
//                // initialize Bluetooth and start scanning
//                checkScanPermission(this);
////            scanLeDevice(true);
//                //Location permission denied
//            } else {
//                Permissions.deniedPermission(this);
//            }
//        }}
//    void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // Stops scanning after a predefined scannerlistview period
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scanning = false;
//                    if (ActivityCompat.checkSelfPermission(Gatt_Client.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(Gatt_Client.this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
//                    }
//                    Toast.makeText(Gatt_Client.this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
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
//            if (ActivityCompat.checkSelfPermission(Gatt_Client.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                if (!hasPermissions()) {
//                    // if permission is not granted
//                    Toast.makeText(Gatt_Client.this, "Permission denied", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(Gatt_Client.this, message, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            Toast.makeText(Gatt_Client.this, "Scanning batch", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Toast.makeText(Gatt_Client.this, "failed to scannerlistview", Toast.LENGTH_SHORT).show();
//        }
//    };
//    public void stopScan() {
//        if (scanning) {
//            scanning = false;
//            if (ActivityCompat.checkSelfPermission(Gatt_Client.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                if (!hasPermissions()) {
//                    // if no scanning permissions
//                }
//            }
//            bluetoothLeScanner.stopScan(scanCallback);
//            Toast.makeText(Gatt_Client.this, "Scanning stopped", Toast.LENGTH_SHORT).show();
//        } else {
//            // Scanning is not currently active
//            Toast.makeText(Gatt_Client.this, "Scanning is not active", Toast.LENGTH_SHORT).show();
//        }
//    }
//}