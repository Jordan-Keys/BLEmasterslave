package App.Interfaces.Scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.blemasterslave.R;
import java.util.Map;
import eazyble.MasterSlave.Scanner.Scanning;

public class ScannerFragment extends Fragment implements Scanning.ScanResultListener {
    private Scanning scanning;
    private TextView scannedDevicesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View rootView = inflater.inflate(R.layout.scanner, container, false);

        // Initialize the TextView where devices will be displayed
        scannedDevicesTextView = rootView.findViewById(R.id.textView);  // Ensure your TextView has this ID

        // Start scanning for BLE devices
        scanning = new Scanning(requireContext());
        scanning.setScanResultListener(this);  // Set the listener to receive scan results
        scanning.scanLeDevices();
        return rootView;
    }

    @Override
    public void onDeviceFound(Map<String, String> devices) {
        // Prepare the string to display the devices with separators
        StringBuilder deviceInfo = new StringBuilder();
        for (Map.Entry<String, String> entry : devices.entrySet()) {
            String mac = entry.getKey();
            String info = entry.getValue();
            deviceInfo.append("MAC: ").append(mac);
            deviceInfo.append(info.replace("Name:", "\nName:").replace("RSSI:", "\nRSSI:").replace("Proximity:", "\nProximity:")).append("\n");
            deviceInfo.append("----------\n");  // Add a dashed line between devices for separation
        }

        // Set the text of the TextView to display the device information
        scannedDevicesTextView.setText(deviceInfo.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop scanning when the fragment is destroyed to prevent leaks or unnecessary background scanning
        scanning.stopScanning();
    }
}
