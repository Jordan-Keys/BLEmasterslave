package App.Interfaces.Scanner;

import eazyble.MasterSlave.Scanner.ScanResultAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.blemasterslave.R;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import eazyble.MasterSlave.Scanner.ScannerResultsBuilder;
import eazyble.MasterSlave.Scanner.Scanning1;

public class ScannerFragment extends Fragment {
    private ScanResultAdapter adapter;
    private final List<ScannerResultsBuilder> scanResults = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.scannerlistview, container, false);

        ListView listView = rootView.findViewById(R.id.scannerListView);
        adapter = new ScanResultAdapter(getActivity(), scanResults);
        listView.setAdapter(adapter);

        // Initialize and start scanning
        Scanning1 scanning = new Scanning1(requireActivity(), scanResults, adapter);
        scanning.scanLeDevices();

        return rootView;

    }

    // Method to update scannerListview results
    public void updateScanResults(List<ScannerResultsBuilder> results) {
        scanResults.clear();
        scanResults.addAll(results);
        adapter.notifyDataSetChanged();
    }
}



//package App.Interfaces.Scanner;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import androidx.fragment.app.Fragment;
//import com.example.blemasterslave.R;
//import java.util.Map;
//import eazyble.MasterSlave.Scanner.Scanning;
//
//public class ScannerFragment extends Fragment implements Scanning.ScanResultListener {
//    private Scanning scanning;
//    private TextView scannedDevicesTextView;
//    private Button startScanButton, stopScanButton;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for the fragment
//        View rootView = inflater.inflate(R.layout.scanner, container, false);
//
//        // Initialize the TextView where devices will be displayed
//        scannedDevicesTextView = rootView.findViewById(R.id.textView);
//        // Start scanning for BLE devices
//        scanning = new Scanning(requireContext());
//        scanning.setScanResultListener(this);
//        startScanButton = rootView.findViewById(R.id.scan);
//        startScanButton.setOnClickListener(v -> {
//            // Handle advertise button click
//            scanning.scanLeDevices();
//            startScanButton.setEnabled(false);
//            stopScanButton.setEnabled(true);
//        });
//
//        stopScanButton = rootView.findViewById(R.id.stop);
//        stopScanButton.setOnClickListener(v -> {
//            // Handle advertise button click
//            scanning.stopScanning();
//            startScanButton.setEnabled(true);
//            stopScanButton.setEnabled(false);
//        });
//        stopScanButton.setEnabled(false);
//
//        return rootView;
//    }
//
//    @Override
//    public void onDeviceFound(Map<String, String> devices) {
//        // Prepare the string to display the devices with separators
//        StringBuilder deviceInfo = new StringBuilder();
//        for (Map.Entry<String, String> entry : devices.entrySet()) {
//            String mac = entry.getKey();
//            String info = entry.getValue();
//            deviceInfo.append("MAC: ").append(mac);
//            deviceInfo.append(info.replace("Name:", "\nName:").replace("RSSI:", "\nRSSI:").replace("Proximity:", "\nProximity:")).append("\n");
//            deviceInfo.append("----------\n");  // Add a dashed line between devices for separation
//        }
//
//        // Set the text of the TextView to display the device information
//        scannedDevicesTextView.setText(deviceInfo.toString());
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Stop scanning when the fragment is destroyed to prevent leaks or unnecessary background scanning
//        scanning.stopScanning();
//    }
//}
