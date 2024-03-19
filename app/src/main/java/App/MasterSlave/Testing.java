//package App.MasterSlave;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.os.Handler;
//public class Testing{
//    private BluetoothAdapter bluetoothAdapter;
//    private final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//    private boolean scanning;
//    private ScanCallback scanCallback;
//    private Handler handler = new Handler();
//    // Stops scanning after 10 seconds.
//    private static final long SCAN_PERIOD = 10000;
//    private void scanLeDevice() {
//        if (!scanning) {
//            // Stops scanning after a predefined scan period.
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scanning = false;
//                    bluetoothLeScanner.stopScan(scanCallback);
//                }
//            }, SCAN_PERIOD);
//            scanning = true;
//            bluetoothLeScanner.startScan(scanCallback);
//        } else {
//            scanning = false;
//            bluetoothLeScanner.stopScan(scanCallback);
//        }
//    }
//}
