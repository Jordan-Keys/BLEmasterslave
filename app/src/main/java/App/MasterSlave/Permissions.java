package App.MasterSlave;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.blemasterslave.R;
import org.jetbrains.annotations.NotNull;
import App.MasterSlave.Gatt_Client;

public class Permissions extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializing Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBluetoothSupport();
    }

    /*=================================>>>>>HANDLING BLUETOOTH PERMISSIONS<<<<<=========================*/
    // method for checking if Bluetooth is supported and enabled

    public void checkBluetoothSupport() {
        // if the device does not support bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device.", Toast.LENGTH_SHORT).show();
        } else {
            // if bluetooth is supported but not enabled
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // requesting user to allow bluetooth
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                }
            } else {
                // if bluetooth is supported and enabled
                // check if location permission is granted
//                checkLocationPermission();// to be uncommented
                Gatt_Client gattClient = new Gatt_Client();// for testing
                gattClient.startBleScanning(this);
            }
        }
    }

    // Handling user response for Bluetooth permission request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                // if user allows Bluetooth
                // also let him allow location permissions
//                checkLocationPermission();// to be uncommented
//                startBleScanning();//for testing
            } else {
                // if user denies Bluetooth
                Toast.makeText(this, "You denied Bluetooth.", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    //    /*========================================>>>>>LOCATION PERMISSIONS<<<<<==================================*/
//    // Method for allowing the user to grant location to the application
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // if location permission is not granted, request user to grant it
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            // if location permission granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // enable user turn on the location services
//                showLocationSettings();
//                // if location services are turned on
//                Toast.makeText(this, "Starting scanning.", Toast.LENGTH_SHORT).show();
//                // if location permission not granted
//            } else {
//                // if the user denies location permissions
//                Toast.makeText(this, "Location permission is required for the app to function well", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Method for checking and requesting for location.
//    private void checkLocationPermission() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Location permission not granted, request it
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//            // If location permission granted request user to turn on location services
//            isLocationEnabled();
//        } else {
//            // Location permission already granted, check if location services are enabled
//            if (!isLocationEnabled()) {
//                // Location services are disabled, prompt the user to enable them
//                showLocationSettings();
//            } else {
//                // Location services are enabled, start BLE scanning
////                startBleScanning();
//                Toast.makeText(this, "Starting scanning.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Method to check if location services are enabled
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//    }
//
//    // Method to request user launch location settings screen
//    private void showLocationSettings() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Devices location is off. Do you want to turn it on?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", (dialog, id) -> openLocationSettings())
//                .setNegativeButton("No", (dialog, id) -> {
//                    // Handling user's choice
//                    Toast.makeText(getApplicationContext(), "Some functionalities may not work properly.", Toast.LENGTH_SHORT).show();
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    // Method to open location settings screen
//    private void openLocationSettings() {
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(intent);
//    }
}
