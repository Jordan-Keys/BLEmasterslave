package eazyble.MasterSlave.Advertiser;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import eazyble.Permissions;
import java.util.UUID;

public class Advertising1 {
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private final BluetoothAdapter bluetoothAdapter;
    public static final long REQUEST_BLUETOOTH_ADVERTISE_PERMISSION = 1;
    private boolean advertising;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private AdvertisingSet currentAdvertisingSet;

    public Advertising1(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAdvertisePermission();
        } else {
            bluetoothLeAdvertiser = this.bluetoothAdapter.getBluetoothLeAdvertiser();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkAdvertisePermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE},
                    (int) REQUEST_BLUETOOTH_ADVERTISE_PERMISSION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startAdvertising() {
        Permissions.checkBluetoothSupport((Activity) context);

        if (!advertising) {
            long ADVERTISE_PERIOD = 100000;
            handler.postDelayed(() -> {
                advertising = false;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkAdvertisePermission();
                    }
                }
                if (bluetoothLeAdvertiser != null) {
                    stopAdvertising();
                    Toast.makeText(context, "Advertising1 stopped due to timeout", Toast.LENGTH_SHORT).show();
                }
            }, ADVERTISE_PERIOD);

            // Check for extended advertising support
            if (!bluetoothAdapter.isLeExtendedAdvertisingSupported()) {
                Toast.makeText(context, "Extended advertising not supported", Toast.LENGTH_SHORT).show();
                return;
            }

            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            if (bluetoothLeAdvertiser == null) {
                Toast.makeText(context, "Bluetooth is turned off", Toast.LENGTH_SHORT).show();
                return;
            }

            // Extended advertising parameters
            AdvertisingSetParameters parameters = new AdvertisingSetParameters.Builder()
                    .setLegacyMode(false) // Use extended advertising
                    .setInterval(AdvertisingSetParameters.INTERVAL_LOW)
                    .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_HIGH)
                    .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
                    .setSecondaryPhy(BluetoothDevice.PHY_LE_2M)
                    .setConnectable(false)
                    .build();

            // Create advertising data with larger payload
            String largeData = "This is a .";
            ParcelUuid serviceUuid = new ParcelUuid(UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB"));

           // ParcelUuid serviceUuid = new ParcelUuid(UUID.randomUUID());
            AdvertiseData data = new AdvertiseData.Builder()
                    .addServiceData(serviceUuid, largeData.getBytes())
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
                    .build();

            advertising = true;
            bluetoothLeAdvertiser.startAdvertisingSet(
                    parameters,
                    data,
                    null, // scan response
                    null, // periodic parameters
                    null, // periodic data
                    new AdvertisingSetCallback() {
                        @Override
                        public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                            super.onAdvertisingSetStarted(advertisingSet, txPower, status);
                            currentAdvertisingSet = advertisingSet;
                            if (status == AdvertisingSetCallback.ADVERTISE_SUCCESS) {
                                Toast.makeText(context, "Extended advertising started", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Advertising1 failed: " + status, Toast.LENGTH_SHORT).show();
                                advertising = false;
                            }
                        }

                        @Override
                        public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                            super.onAdvertisingSetStopped(advertisingSet);
                            currentAdvertisingSet = null;
                            advertising = false;
                            Toast.makeText(context, "Advertising1 stopped", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Advertising1 is already in progress", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopAdvertising() {
        if (advertising) {
            advertising = false;
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkAdvertisePermission();
                }
            }
            if (bluetoothLeAdvertiser != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bluetoothLeAdvertiser.stopAdvertisingSet(new AdvertisingSetCallback() {
                        @Override
                        public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                            super.onAdvertisingSetStopped(advertisingSet);
                            // Log to confirm the stop
                            Log.d("Advertising1", "Advertising1 set stopped successfully.");
                            currentAdvertisingSet = null;
                        }

                        @Override
                        public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                            super.onAdvertisingSetStarted(advertisingSet, txPower, status);
                            // Log to confirm the advertising set started (for debugging)
                            Log.d("Advertising1", "Advertising1 set started successfully.");
                        }
                    });
                }
            }
            Toast.makeText(context, "Advertising1 manually stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Advertising1 is not active", Toast.LENGTH_SHORT).show();
        }
    }
}