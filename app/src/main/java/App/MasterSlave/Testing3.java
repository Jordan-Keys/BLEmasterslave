//package App.MasterSlave;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
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
//public class Testing3 extends AppCompatActivity {
//    private BluetoothLeScanner bluetoothLeScanner;
//    private boolean scanning = false;
//    private final Handler handler = new Handler(Looper.getMainLooper());
//    private static final long SCAN_PERIOD = 10000; // 10 seconds
//    static BluetoothAdapter bluetoothAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        Permissions permissions = new Permissions();
////        permissions.checkBluetoothSupport(this);
//        setContentView(R.layout.activity_main);
//        Button button = findViewById(R.id.scanButton);
//        button.setOnClickListener(v -> {
//            scanLeDevice(true);
//        });
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        Button button1 = findViewById(R.id.scanButton1);
//        button1.setOnClickListener(v -> {
//            stopScan();
//        });
//
//        Button button2 = findViewById(R.id.closeButton1);
//        button2.setOnClickListener(v -> {
//            System.exit(0);
//        });
//
//        // Ensure that the required permissions are granted
//        if (hasPermissions()) {
//            checkBluetoothScanPermission(this);
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
//
//    }
//
//    public void checkBluetoothScanPermission(Context context) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
//            // BLUETOOTH_SCAN permission is granted
//
//        } else {
//            // BLUETOOTH_SCAN permission is not granted
//            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        }
//    }
//
//
//    private boolean hasPermissions() {
//        Permissions permissions = new Permissions();
//        permissions.checkBluetoothSupport(this);
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
//                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//        Permissions permissions = new Permissions();
//        permissions.checkBluetoothSupport(this);
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
//                checkBluetoothScanPermission(this);
////            scanLeDevice(true);
//                //Location permission denied
//            } else {
//                Permissions.deniedPermission(this);
//            }
//        }}
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