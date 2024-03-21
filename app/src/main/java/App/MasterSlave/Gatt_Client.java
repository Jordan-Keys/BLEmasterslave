package App.MasterSlave;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Gatt_Client extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    // Constructor
    public Gatt_Client() {
        //this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Method for scanning functionality
    // Check if BLE scanning is enabled on the device
    public void startBleScanning(Context context) {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            // Scan for BLE devices
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                bluetoothLeScanner.startScan(scanCallback);// to be researched about
            } else {
                Toast.makeText(context, "Scanner is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            // Process scan results here
            BluetoothDevice device = result.getDevice();

            // Get the device name (if available)
            if (ActivityCompat.checkSelfPermission(Gatt_Client.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Gatt_Client.this, "permission check", Toast.LENGTH_SHORT).show();
            }
            String deviceName = device.getName();
            if (deviceName == null) {
                deviceName = "Unknown Device";
            }

            // Get the RSSI value
            int rssi = result.getRssi();
            // Displaying found devices
            Toast.makeText(Gatt_Client.this, "Found device: " + deviceName + " (RSSI: " + rssi + ")", Toast.LENGTH_SHORT).show();
        }
        };
    };


