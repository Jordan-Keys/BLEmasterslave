package App.MasterSlave.Advertiser;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class Advertising {
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothAdapter bluetoothAdapter;
    private boolean advertising;
    private final long ADVERTISE_PERIOD = 10000; // Advertise for 10 seconds
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;

    public Advertising(Context context) {
        this.context = context;
        initializeBluetooth();
    }

    private void initializeBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public void startAdvertising() {
        //Permissions.checkBluetoothSupport((Activity) context);
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!advertising) {
            handler.postDelayed(() -> {
                advertising = false;
                //stopAdvertising();
                Toast.makeText(context, "Advertising stopped due to timeout", Toast.LENGTH_SHORT).show();
            }, ADVERTISE_PERIOD);

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(false)
                    .build();

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
                    .addServiceUuid(ParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB"))
                    .build();

            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            if (bluetoothLeAdvertiser != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                   initializeBluetooth();
                }
                bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
                advertising = true;
            } else {
                Toast.makeText(context, "Bluetooth LE Advertising is not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Advertising is already in progress", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopAdvertising() {
        if (advertising && bluetoothLeAdvertiser != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth();
            }
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            advertising = false;
            Toast.makeText(context, "Advertising manually stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Advertising is not currently active", Toast.LENGTH_SHORT).show();
        }
    }

    private final AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Toast.makeText(context, "Advertising", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Toast.makeText(context, "Advertising failed with error code: " + errorCode, Toast.LENGTH_SHORT).show();
        }
    };
}
