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
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import eazyble.MasterSlave.Scanner.ResultsProcessor;
import eazyble.Permissions;
import java.nio.charset.StandardCharsets;

public class Advertising {
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private final BluetoothAdapter bluetoothAdapter;
    public static final long REQUEST_BLUETOOTH_ADVERTISE_PERMISSION = 1;
    private boolean advertising;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private AdvertisingSetCallback advertisingSetCallback;
    private static final long SCAN_PERIOD = 30000;

    public Advertising(Context context) {
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
            // Get the latest scanned data with debug logging
            ResultsProcessor processor = ResultsProcessor.getInstance();
            String scannedData = processor.getLatestDeviceData();

            if (!scannedData.isEmpty()) {
                Log.d("Advertising", "Ready Data: " + scannedData);
            } else {
                Log.w("Advertising", "No scanned data available, sink own node information.");
            }

            // time out
            Runnable timeoutRunnable = () -> {
                if (advertising) {
                    advertising = false;
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            checkAdvertisePermission();
                        }
                    }
                    if (bluetoothLeAdvertiser != null) {
                        // Stop advertising only if it was still active
                        bluetoothLeAdvertiser.stopAdvertisingSet(advertisingSetCallback);
                        Toast.makeText(context, "Advertising stopped due to timeout", Toast.LENGTH_SHORT).show();
                    }
                }
            };
                handler.postDelayed(timeoutRunnable, SCAN_PERIOD);

            // Check for extended sink support
            if (!bluetoothAdapter.isLeExtendedAdvertisingSupported()) {
                Toast.makeText(context, "Extended advertising not supported", Toast.LENGTH_SHORT).show();
                return;
            }

            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            if (bluetoothLeAdvertiser == null) {
                Toast.makeText(context, "Bluetooth is turned off", Toast.LENGTH_SHORT).show();
                return;
            }

            // Extended sink parameters
            AdvertisingSetParameters parameters = new AdvertisingSetParameters.Builder()
                    .setLegacyMode(false)
                    .setInterval(AdvertisingSetParameters.INTERVAL_LOW)
                    .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_HIGH)
                    .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
                    .setSecondaryPhy(BluetoothDevice.PHY_LE_2M)
                    .setConnectable(false)
                    .build();

            int manufacturerId = 1234;
            AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(false);

            if (!scannedData.isEmpty()) {
                byte[] stringBytes = scannedData.getBytes(StandardCharsets.UTF_8);
                dataBuilder.addManufacturerData(manufacturerId, stringBytes);
            }

            AdvertiseData data = dataBuilder.build();

            advertisingSetCallback = new AdvertisingSetCallback() {
                @Override
                public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                    super.onAdvertisingSetStarted(advertisingSet, txPower, status);
                    if (status == AdvertisingSetCallback.ADVERTISE_SUCCESS) {
                        Toast.makeText(context, "Advertising started", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Advertising failed: " + status, Toast.LENGTH_SHORT).show();
                        advertising = false;
                    }
                }

                @Override
                public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                    super.onAdvertisingSetStopped(advertisingSet);
                    advertising = false;
                }
            };

            advertising = true;
            bluetoothLeAdvertiser.startAdvertisingSet(
                    parameters,
                    data,
                    null, // scan response
                    null, // periodic parameters
                    null, // periodic data
                    advertisingSetCallback
            );
        } else {
            Toast.makeText(context, "Advertising is already in progress", Toast.LENGTH_SHORT).show();
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
            if (bluetoothLeAdvertiser != null && advertisingSetCallback != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bluetoothLeAdvertiser.stopAdvertisingSet(advertisingSetCallback);
                }
                Toast.makeText(context, "Advertising manually stopped", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Advertising is not active", Toast.LENGTH_SHORT).show();
        }
    }
}