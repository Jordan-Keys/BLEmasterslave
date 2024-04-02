//package App.MasterSlave;
//
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothGattServer;
//import android.bluetooth.BluetoothGattServerCallback;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothAdapter;
//import android.content.Context;
//import java.util.UUID;
//
//public class Gatt_Server {
//    private BluetoothManager bluetoothManager;
//    private BluetoothGattServer bluetoothGattServer;
//    private Context context;
//
//    @SuppressLint("MissingPermission")
//    public Gatt_Server(Context context) {
//        this.context = context;
//        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//        // Callback for handling client connections and other events
//        // Override relevant methods as needed, such as onConnectionStateChange, onCharacteristicReadRequest, etc.
//        BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
//            // Override relevant methods as needed, such as onConnectionStateChange, onCharacteristicReadRequest, etc.
//        };
//        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback);
//    }
//
//    @SuppressLint("MissingPermission")
//    public void startServer() {
//        if (bluetoothGattServer != null) {
//            // Create a service and add it to the GATT server
//            BluetoothGattService service = new BluetoothGattService(
//                    UUID.fromString("Provided Service"),
//                    BluetoothGattService.SERVICE_TYPE_PRIMARY);
//
//            // Add characteristics to the service (you can add more characteristics as needed)
//            // For example:
//            // BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
//            //         UUID.fromString("YOUR_CHARACTERISTIC_UUID"),
//            //         BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
//            //         BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
//            // service.addCharacteristic(characteristic);
//
//            bluetoothGattServer.addService(service);
//
//            // Start advertising the service
//            // You would typically advertise periodically or when needed
//            // For simplicity, I'm not including the advertisement part here
//        }
//    }
//}
//
