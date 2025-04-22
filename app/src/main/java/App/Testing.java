//package App;
//
//import android.os.Bundle;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.blemasterslave.R;
//import eazyble.MasterSlave.Advertiser.Advertising1;
//import eazyble.MasterSlave.Advertiser.Testing2;
//
//public class Testing extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.hello);
//        Advertising1 sink = new Advertising1(this);
//        Button defaultAdvertise = findViewById(R.id.testId);
//        defaultAdvertise.setOnClickListener(v -> {
//            sink.startAdvertising(false, null, true);
//        });
//        Testing2 testing = new Testing2(this);
//        Button testsupport = findViewById(R.id.testFeatures);
//        testsupport.setOnClickListener(v -> {
//            testing.testSupportedFeatures();
//        });
//    }
//}
//

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
//import android.widget.Toast;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class Sink {
//    // Declaring variables
//    private BluetoothLeScanner bluetoothLeScanner;
//    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
//    private boolean scanning;
//    // Stop scanning after 10 seconds
//    private static final long SCAN_PERIOD = 100000;
//    private final Handler handler = new Handler(Looper.getMainLooper());
//    private final Context context;
//    private final BluetoothAdapter bluetoothAdapter;
//
//    public Sink(Context context) {
//        this.context = context;
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
//    // Method to check Bluetooth permissions
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    private void checkScanPermission() {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
//                    (int) REQUEST_BLUETOOTH_SCAN_PERMISSION);
//        } //else {
//        // BLUETOOTH_SCAN permission is already granted
//        // Toast.makeText(context, "Scanning permission already granted", Toast.LENGTH_SHORT).show();
//        // }
//    }
//
//    // Method to start scanning
//    public void startSink() {
//        Permissions.checkBluetoothSupport((Activity) context);
//        if (!bluetoothAdapter.isEnabled()) {
//            Toast.makeText(context, "Please Enable Bluetooth", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!scanning) {
//            // Stops scanning after a predefined scan period.
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
//                }
//            }, SCAN_PERIOD);
//
//            scanning = true;
//            if (bluetoothLeScanner == null) {
//                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//            }
//            if (bluetoothLeScanner != null) {
//                ScanSettings settings = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    settings = new ScanSettings.Builder()
//                            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                            .build();
//                }
//                List<ScanFilter> filters = new ArrayList<>();
//                bluetoothLeScanner.startScan(filters, settings, scanCallback);
//            } else {
//                // Handle null bluetoothLeScanner object
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
//    // Callback for scanned results
//    private final ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    checkScanPermission();
//                }
//            }
//            String macAddress = result.getDevice().getAddress();
//            String deviceName = result.getDevice().getName();
//            // if the scanned device is not ble device
//            if (deviceName == null || deviceName.isEmpty()) {
//                deviceName = "N/A";
//            }
//            int rssi = result.getRssi();  // Get RSSI value
//            int txPower = result.getScanRecord().getTxPowerLevel();
//        }
//    };
//
//}
