package eazyble.MasterSlave;//package App.MasterSlave;
//
//import Scanner.Interfaces.eazyble.ScannerResultsBuilder;
//import MasterSlave.eazyble.Permissions;
//import Scanner.MasterSlave.eazyble.ScanResultAdapter;
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
//import android.os.*;
//import android.widget.ListView;
//import android.widget.Toast;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import com.example.blemasterslave.R;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Testing5 extends AppCompatActivity {
//    // declaring variables
//    private final List<ScannerResultsBuilder> scanResults = new ArrayList<>();
//    private ScanResultAdapter adapter;
//    private BluetoothLeScanner bluetoothLeScanner;
//    static BluetoothAdapter bluetoothAdapter;
//    public static final long REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;
//    private boolean scanning;
//    //stop scanning after 10 seconds
//    private static final long SCAN_PERIOD = 10000;
//    private final Handler handler = new Handler(Looper.getMainLooper());
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();
//        Permissions permissions = new Permissions();
//        permissions.checkBluetoothSupport(this);
//        setContentView(R.layout.scannerlistview);
//        adapter = new ScanResultAdapter(this, scanResults);
//        ListView listView = findViewById(R.id.scannerListView);
//        listView.setAdapter(adapter);
//
//
////        Button scanButton = findViewById(R.id.scanButton);
////        scanButton.setOnClickListener(v -> scanLeDevices());
////        Button stopButton = findViewById(R.id.stopButton);
////        stopButton.setOnClickListener(v -> stopScanning());
////        Button closeButton = findViewById(R.id.closeButton);
////        closeButton.setOnClickListener(v -> System.exit(0));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            checkScanPermission(this);
//        }else {
//            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        }
//        scanLeDevices();
//    }
//
//    // Results returned from bluetooth decision after requesting permission
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Permissions.handleOnActivityResult(this, requestCode, resultCode);
//    }
//
//    // method to check scannerlistview permissions
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    public void checkScanPermission(Context context) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
//                    (int) REQUEST_BLUETOOTH_SCAN_PERMISSION);
//        } else {
//            // BLUETOOTH_SCAN permission is already granted
//            Toast.makeText(context, "Scanning permission already granted", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_BLUETOOTH_SCAN_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed with advertising
//                Toast.makeText(this, "Scanning permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission denied, handle accordingly
//                Toast.makeText(this, "Scanning permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            if(requestCode==Permissions.REQUEST_LOCATION_PERMISSION){
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted
//                    Permissions.grantedPermission(this);
//                    //Location permission denied
//                } else {
////                    Toast.makeText(this, "Advertising permission denied", Toast.LENGTH_SHORT).show();
//                    Permissions.deniedPermission(this);
//                }
//            }
//
//        }
//    }
//
//    // method to start scanning
//    public void scanLeDevices() {
//        if (!scanning) {
//            // Stops scanning after a predefined scannerlistview period.
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scanning = false;
//                    if (ActivityCompat.checkSelfPermission(Testing5.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                            checkScanPermission(Testing5.this);
//                        }
//                    }
//                    bluetoothLeScanner.stopScan(scanCallback);
//                    Toast.makeText(Testing5.this, "Scanning stopped due to timeout", Toast.LENGTH_SHORT).show();
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
//            List<ScanFilter> filters = new ArrayList<>();
//            bluetoothLeScanner.startScan(filters, settings, scanCallback);
//            //new Handler(Looper.getMainLooper()).postDelayed(this::stopScanning, 10000);
//        } else {
//            scanning = false;
//            bluetoothLeScanner.stopScan(scanCallback);
//            Toast.makeText(Testing5.this, "Scanning is already in Progress", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    // method to stop scanning
//    private void stopScanning() {
//        if (scanning) {
//            scanning = false;
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    checkScanPermission(Testing5.this);
//                }
//            }
//            bluetoothLeScanner.stopScan(scanCallback);
//            Toast.makeText(Testing5.this, "Scanning manually stopped", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(Testing5.this, "Scanning is not active", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // call back for scanned results
//    private final ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
//            StringBuilder uuidMessage = new StringBuilder("UUIDs: ");
//            if (uuids != null) {
//                for (ParcelUuid uuid : uuids) {
//                    uuidMessage.append(uuid.toString()).append("\n");
//                }
//            }
//            if (ActivityCompat.checkSelfPermission(Testing5.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    checkScanPermission(Testing5.this);
//                }
//            }
//            String macAddress = result.getDevice().getAddress();
//            String deviceName = result.getDevice().getName();
//            int rssi = result.getRssi();
//            // Check if device name is null and provide a default value
//            if (deviceName == null) {
//                deviceName = "Unknown Device";
//            }
//            // Displaying device name and RSSI using a toast message
//            String message = "Device Name: " + deviceName + "\nRSSI: " + rssi + "\n" + uuidMessage + "\nMAC Address: " + macAddress ;
//            Toast.makeText(Testing5.this, message, Toast.LENGTH_SHORT).show();
//
//            scanResults.add(new ScannerResultsBuilder(deviceName, rssi, uuidMessage.toString(), macAddress));
//            adapter.notifyDataSetChanged();
//        }
//        // method for multiple scannerlistview results
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            Toast.makeText(Testing5.this, "Multiple Scans", Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onScanFailed(int errorCode) {
//            Toast.makeText(Testing5.this, "failed to scannerlistview", Toast.LENGTH_SHORT).show();
//        }
//    };
//}