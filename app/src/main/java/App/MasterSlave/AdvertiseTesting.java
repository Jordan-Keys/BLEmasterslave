//package App.MasterSlave;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.AdvertiseCallback;
//import android.bluetooth.le.AdvertiseSettings;
//import android.bluetooth.le.BluetoothLeAdvertiser;
//import android.bluetooth.le.AdvertiseData;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.*;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import com.example.blemasterslave.R;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.UUID;
//
//public class AdvertiseTesting extends AppCompatActivity {
//    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
//    static BluetoothAdapter bluetoothAdapter;
//    private boolean advertising;
//    public static final long REQUEST_ADVERTISE_PERMISSION = 2;
//    public static final long ADVERTISE_PERIOD = 100000; // Advertise for 10 seconds
//    private final Handler handler = new Handler(Looper.getMainLooper());
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_activity);
//
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();
//        Permissions permissions = new Permissions();
//        permissions.checkBluetoothSupport(this);
//
////        Button startAdvertisingButton = findViewById(R.id.advertiseButton);
////        startAdvertisingButton.setOnClickListener(v -> startAdvertising());
////
////        Button stopAdvertisingButton = findViewById(R.id.stopAdvertiseButton);
////        stopAdvertisingButton.setOnClickListener(v -> stopAdvertising());
////        Button closeButton = findViewById(R.id.closeButton);
////        closeButton.setOnClickListener(v -> System.exit(0));
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Permissions.handleOnActivityResult(this, requestCode, resultCode);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    public void checkAdvertisingPermission(Context context) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE},
//                    (int) REQUEST_ADVERTISE_PERMISSION);
//        } else {
//            // BLUETOOTH_ADVERTISE permission is already granted
//            Toast.makeText(context, "Advertising permission already granted", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_ADVERTISE_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed with advertising
//                Toast.makeText(this, "Advertising permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission denied, handle accordingly
//                Toast.makeText(this, "Advertising permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private final AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
//        @Override
//        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//            super.onStartSuccess(settingsInEffect);
//            advertising = true;
//            Toast.makeText(AdvertiseTesting.this, "Advertising", Toast.LENGTH_SHORT).show();
//            Log.d("AdvertiseTesting", "Advertising started successfully");
////      handler.postDelayed(this::stopAdvertising, ADVERTISE_PERIOD);
//        }
//
//        @Override
//        public void onStartFailure(int errorCode) {
//            super.onStartFailure(errorCode);
//            Toast.makeText(AdvertiseTesting.this, "Advertising failed", Toast.LENGTH_SHORT).show();
//            Log.e("AdvertiseTesting", "Advertising failed with error code: " + errorCode);
//        }
//    };
//
//    private void startAdvertising() {
//        if (!advertising) {
//            handler.postDelayed(() -> {
//                advertising = false;
//                if (ActivityCompat.checkSelfPermission(AdvertiseTesting.this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        checkAdvertisingPermission(AdvertiseTesting.this);
//                    }
//                }
//                bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
//                Toast.makeText(AdvertiseTesting.this, "Advertising stopped due to timeout", Toast.LENGTH_SHORT).show();
//            }, ADVERTISE_PERIOD);
//            AdvertiseSettings settings = new AdvertiseSettings.Builder()
//                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
//                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
//                    .setConnectable(false)
//                    .build();
//
//            ParcelUuid uuid = new ParcelUuid(UUID.randomUUID());
////            String url = "http://example.com";
////            byte[] urlBytes = url.getBytes();
//            AdvertiseData data = new AdvertiseData.Builder()
//                    .setIncludeDeviceName(true)
//                    .setIncludeTxPowerLevel(true)
//                    .addServiceUuid(uuid)
////                    .addServiceData(uuid, urlBytes)
////                    .addServiceUuid(ParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB"))
////                    .addServiceData(ParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB"), "Rice selling here".getBytes())
//                    .build();
//
//            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    checkAdvertisingPermission(this);
//                }
//            }
//            bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
//        } else {
//            Toast.makeText(AdvertiseTesting.this, "Advertising is already in progress", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void stopAdvertising() {
//        if (advertising) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    checkAdvertisingPermission(this);
//                }
//            }
//            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
//            advertising = false;
//            Toast.makeText(AdvertiseTesting.this, "Advertising manually stopped", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(AdvertiseTesting.this, "Advertising is not currently active", Toast.LENGTH_SHORT).show();
//        }
//    }
//}