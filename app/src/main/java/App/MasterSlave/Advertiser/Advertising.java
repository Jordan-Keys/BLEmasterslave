package App.MasterSlave.Advertiser;

import App.MasterSlave.Permissions;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.AdvertiseData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.blemasterslave.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class Advertising extends AppCompatActivity {
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    static BluetoothAdapter bluetoothAdapter;
    private boolean advertising;
    public static final long REQUEST_ADVERTISE_PERMISSION = 2;
    public static final long ADVERTISE_PERIOD = 100000; // Advertise for 10 seconds
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        Permissions permissions = new Permissions();
        permissions.checkBluetoothSupport(this);
        startAdvertising();

    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void checkAdvertisingPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE},
                    (int) REQUEST_ADVERTISE_PERMISSION);
        } else {
            // BLUETOOTH_ADVERTISE permission is already granted
            Toast.makeText(context, "Advertising permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private final AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            advertising = true;
            Toast.makeText(Advertising.this, "Advertising", Toast.LENGTH_SHORT).show();
            Log.d("AdvertiseTesting", "Advertising started successfully");
//      handler.postDelayed(this::stopAdvertising, ADVERTISE_PERIOD);
        }

//        @Override
//        public void onStartFailure(int errorCode) {
//            super.onStartFailure(errorCode);
//            Toast.makeText(Advertising.this, "Advertising failed", Toast.LENGTH_SHORT).show();
//            Log.e("AdvertiseTesting", "Advertising failed with error code: " + errorCode);
//        }
    };

    private void startAdvertising() {
        if (!advertising) {
            handler.postDelayed(() -> {
                advertising = false;
                if (ActivityCompat.checkSelfPermission(Advertising.this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkAdvertisingPermission(Advertising.this);
                    }
                }
                bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
                Toast.makeText(Advertising.this, "Advertising stopped due to timeout", Toast.LENGTH_SHORT).show();
            }, ADVERTISE_PERIOD);
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(false)
                    .build();

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeTxPowerLevel(true)
                    .build();

            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkAdvertisingPermission(this);
                }
            }
            bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
        } else {
            Toast.makeText(Advertising.this, "Advertising is already in progress", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopAdvertising() {
        if (advertising) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkAdvertisingPermission(this);
                }
            }
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            advertising = false;
            Toast.makeText(Advertising.this, "Advertising manually stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Advertising.this, "Advertising is not currently active", Toast.LENGTH_SHORT).show();
        }
    }
}