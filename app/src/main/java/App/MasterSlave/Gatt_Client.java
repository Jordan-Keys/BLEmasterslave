package App.MasterSlave;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
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
                // bluetoothLeScanner.startScan(scanCallback);// to be researched about
                Toast.makeText(context, "Starting scanning.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Scanner is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
