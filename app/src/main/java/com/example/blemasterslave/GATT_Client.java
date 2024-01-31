package com.example.blemasterslave;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class GATT_Client extends AppCompatActivity {
    // Declaring variables
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private List<BluetoothDevice> scannedDevicesList;
    private ArrayAdapter<String> devicesAdapter;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initializing Bluetooth adapter and scanner
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_client);
        // Initialize scanned devices list and adapter
        scannedDevicesList = new ArrayList<>();
        devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Set up ListView
        listView = findViewById(R.id.listView);
        listView.setAdapter(devicesAdapter);


        checkBluetoothSupport();
        startBleScanning();
    }

    /*========================================>>>>>BLUETOOTH PERMISSIONS<<<<<==================================*/
    // Checking if Bluetooth is supported and enabled
    private void checkBluetoothSupport() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not supported or not enabled. Prompting the user to enable Bluetooth.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            // If Bluetooth is supported and enabled. Check and request location permission.
            checkLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                // If bluetooth is already enabled. Proceed to check and request location permission.
                checkLocationPermission();
            } else {
                // if bluetooth is not already enabled.
                Toast.makeText(GATT_Client.this, "Bluetooth is required for scanning.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*========================================>>>>>LOCATION PERMISSIONS<<<<<==================================*/
    // Method for allowing the user to grant location to the application
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // if location permission granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLocationSettings();
                // Location permission granted, start BLE scanning
                startBleScanning();
                // if location permission not granted
            } else {
                // Location permission not granted, show a message or handle accordingly
                Toast.makeText(this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method for checking and requesting for location.
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            // If location permission granted request user to turn on location services
            isLocationEnabled();
        } else {
            // Location permission already granted, check if location services are enabled
            if (!isLocationEnabled()) {
                // Location services are disabled, prompt the user to enable them
                showLocationSettings();
            } else {
                // Location services are enabled, start BLE scanning
                startBleScanning();
            }
        }
    }

    // Method to check if location services are enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Method to request user launch location settings screen
    private void showLocationSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Devices location is off. Do you want to turn it on?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> openLocationSettings())
                .setNegativeButton("No", (dialog, id) -> {
                    // Handling user's choice
                    Toast.makeText(getApplicationContext(), "Some functionalities may not work properly.", Toast.LENGTH_SHORT).show();
                    // additional actions if the user chooses not to enable location services
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Method to open location settings screen
    private void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    /*========================================>>>>>STARTING BLE SCANNING SERVICES<<<<<==================================*/
// Method to start BLE scanning
    private void startBleScanning() {
        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                // Process scan result
                BluetoothDevice device = result.getDevice();
                if (!scannedDevicesList.contains(device)) {
                    scannedDevicesList.add(device);
                    updateDeviceList();
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                // Process batch scan results
                for (ScanResult result : results) {
                    BluetoothDevice device = result.getDevice();
                    if (!scannedDevicesList.contains(device)) {
                        scannedDevicesList.add(device);
                    }
                }
                updateDeviceList();
            }

            @Override
            public void onScanFailed(int errorCode) {
                // Handle scan failure
                Toast.makeText(getApplicationContext(), "Scan failed with error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothLeScanner.startScan(scanCallback);
    }

    // Method to update the list of scanned devices
    private void updateDeviceList() {
        runOnUiThread(() -> {
            devicesAdapter.clear();
            for (BluetoothDevice device : scannedDevicesList) {
                if (ActivityCompat.checkSelfPermission(GATT_Client.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                devicesAdapter.add(device.getName() + " - " + device.getAddress());
            }
            devicesAdapter.notifyDataSetChanged();
        });
    }

    // Method to stop BLE scanning
    private void stopBleScanning() {
        if (bluetoothLeScanner != null && scanCallback != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBleScanning();
    }
}
