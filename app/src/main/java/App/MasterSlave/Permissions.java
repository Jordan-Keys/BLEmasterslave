package App.MasterSlave;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    // Declaring variables
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final int REQUEST_LOCATION_PERMISSION = 2 ;
    static BluetoothAdapter bluetoothAdapter;

    /*=================================>>>>>HANDLING BLUETOOTH PERMISSIONS<<<<<=========================*/
    // method for checking if Bluetooth is supported and enabled
    public void checkBluetoothSupport(Activity activity) {
        // initializing the bluetoothAdapter
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // if the device does not support bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();//testing
        } else {
            // if bluetooth is supported but not enabled
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // requesting user to allow bluetooth
                    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                } // if bluetooth is enabled but already enabled by the user
            } else {
                // if bluetooth is supported and enabled then check if location permission is granted
                checkLocationPermission(activity);
            }
        }
    }
    // Handling user response for Bluetooth permission request
    public static void  handleOnActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                // if user allows Bluetooth let them also allow location permissions if not yet allowed
                checkLocationPermission(activity);
//                return true; // Indicate successful handling
            } else {
                // if user denies Bluetooth
                Toast.makeText(activity, "You denied Bluetooth.", Toast.LENGTH_SHORT).show();//testing
            }
        }
//        return false; // Indicate no handling for other request codes
    }
    //    /*========================================>>>>>LOCATION PERMISSIONS<<<<<==================================*/
    // Method for allowing the user to grant location to the application
    static void checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            // If location permission granted request user to turn on location services
            isLocationEnabled(activity);
        } else {
            // Location permission already granted, check if location services are enabled
            if (!isLocationEnabled(activity)) {
                // Location services are disabled, prompt the user to enable them
                showLocationSettings(activity);
            }
// location services are enabled
// Location services are enabled, start BLE scanning
//                Toast.makeText(activity, "Everything is okay scannerlistview has Started", Toast.LENGTH_SHORT).show();//testing
        }
    }
    // handling user response to location access
    // for granted permission
    public static void grantedPermission(Context context) {
        if (!isLocationEnabled(context)) {
            // if permission is granted but location show choice dialogue
            showLocationSettings(context);
        }// if permission is granted but location is already on location
    }
    // for denied permission
    public static void deniedPermission(Context context) {
        Toast.makeText(context, "You denied Location Permissions", Toast.LENGTH_SHORT).show();//testing
    }
    private static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    // method to open a location dialog for the user to choose
    private static void showLocationSettings(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Devices location is off. Do you want to turn it on?")
                .setCancelable(false)
                // if users choice is yes open location settings
                .setPositiveButton("Yes", (dialog, id) -> openLocationSettings(context))
                // if users choice is no
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(context, "Some functionalities may not work properly.", Toast.LENGTH_SHORT).show();// testing
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    // Method to open location settings screen
    private static void openLocationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }
}