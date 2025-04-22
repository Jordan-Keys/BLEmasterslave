package App.Interfaces.Scanner;

import eazyble.MasterSlave.Advertiser.Advertising;
import eazyble.MasterSlave.Scanner.ScanResultAdapter;
import android.graphics.Color;
import android.os.Build;
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
import eazyble.MasterSlave.Scanner.Scanning;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;

public class ScannerFragment extends Fragment {
    private final List<ScannerResultsBuilder> scanResults = new ArrayList<>();
    private Button startScanButton;
    private Button stopScanButton;
    private Button startAdvertisingButton;
    private boolean isScanning = false;
    private boolean isAdvertising = false;
    private boolean isAutoMode = false;
    private final Handler autoHandler = new Handler();
    private Runnable autoRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.scannerlist, container, false);

        ListView listView = rootView.findViewById(R.id.scannerListView);
        ScanResultAdapter adapter = new ScanResultAdapter(getActivity(), scanResults);
        listView.setAdapter(adapter);

        // Initialize scanning and sink
        Scanning scanning = new Scanning(requireActivity(), scanResults, adapter);
        Advertising advertising = new Advertising(requireContext());

        startScanButton = rootView.findViewById(R.id.startButton);
        startAdvertisingButton = rootView.findViewById(R.id.startAdv);
        stopScanButton = rootView.findViewById(R.id.stopButton);
        Button auto = rootView.findViewById(R.id.autoButton);
        Button manual = rootView.findViewById(R.id.manual);

        // start scanning
        startScanButton.setOnClickListener(v -> {
            if (isAdvertising) {
                Toast.makeText(getContext(), "Stop advertising first", Toast.LENGTH_SHORT).show();
                return;
            }
            // Handle advertise button click
            scanning.scanLeDevices();
            isScanning = true;
            isAdvertising = false;
            startScanButton.setEnabled(false);
            stopScanButton.setEnabled(true);

            startScanButton.setBackgroundColor(Color.GREEN);
        });

        // Start sink
        startAdvertisingButton.setOnClickListener(v -> {
            if (isScanning) {
                Toast.makeText(getContext(), "Stop scanning first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Handle advertise button click
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                advertising.startAdvertising();
            }
            isAdvertising = true;
            isScanning = false;
            startAdvertisingButton.setEnabled(false);
            //startScanButton.setEnabled(false);
            stopScanButton.setEnabled(true);

            startAdvertisingButton.setBackgroundColor(Color.GREEN);
           // startScanButton.setBackgroundColor(Color.BLUE);

        });

        // Stop scanning or advertising
        stopScanButton.setOnClickListener(v -> {
            if (isScanning) {
                scanning.stopScanning();
                isScanning = false;
            }

            if (isAdvertising) {
                advertising.stopAdvertising();
                isAdvertising = false;
            }

            startScanButton.setEnabled(true);
            startAdvertisingButton.setEnabled(true);
            stopScanButton.setEnabled(false);

            startScanButton.setBackgroundColor(Color.BLUE);
            startAdvertisingButton.setBackgroundColor(Color.BLUE);
        });

        // Initial state
        startScanButton.setEnabled(true);
        startAdvertisingButton.setEnabled(true);
        stopScanButton.setEnabled(false);

        startScanButton.setBackgroundColor(Color.BLUE);
        startAdvertisingButton.setBackgroundColor(Color.BLUE);

        auto.setOnClickListener(v -> {
            if (isAutoMode) return;

            isAutoMode = true;
            Toast.makeText(getContext(), "Auto mode started", Toast.LENGTH_SHORT).show();

            // Disable manual buttons during auto
            startScanButton.setEnabled(false);
            startAdvertisingButton.setEnabled(false);
            startScanButton.setBackgroundColor(Color.GRAY);
            startAdvertisingButton.setBackgroundColor(Color.GRAY);
            stopScanButton.setEnabled(false);
            stopScanButton.setBackgroundColor(Color.GRAY);

            // Define the auto-switching behavior
            autoRunnable = new Runnable() {
                private boolean autoScanning = true;

                @Override
                public void run() {
                    if (!isAutoMode) return;

                    if (autoScanning) {
                        if (isAdvertising) {
                            advertising.stopAdvertising();
                            isAdvertising = false;
                        }
                        scanning.scanLeDevices();
                        isScanning = true;
                    } else {
                        if (isScanning) {
                            scanning.stopScanning();
                            isScanning = false;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            advertising.startAdvertising();
                        }
                        isAdvertising = true;
                    }

                    autoScanning = !autoScanning;

                    // Schedule next switch in 10 minutes
                    autoHandler.postDelayed(this, 20 * 1000);
                }
            };

            // Start first switch immediately
            autoHandler.post(autoRunnable);
        });

        manual.setOnClickListener(v -> {
            if (!isAutoMode) {
                Toast.makeText(getContext(), "Auto mode is not active", Toast.LENGTH_SHORT).show();
                return;
            }

            // Stop the auto switching
            isAutoMode = false;
            autoHandler.removeCallbacks(autoRunnable);

            // Stop any ongoing sink or scanning
            if (isScanning) {
                scanning.stopScanning();
                isScanning = false;
            }
            if (isAdvertising) {
                advertising.stopAdvertising();
                isAdvertising = false;
            }

            Toast.makeText(getContext(), "Switched to manual mode", Toast.LENGTH_SHORT).show();

            // Re-enable manual controls
            startScanButton.setEnabled(true);
            startAdvertisingButton.setEnabled(true);
            stopScanButton.setEnabled(false);

            startScanButton.setBackgroundColor(Color.BLUE);
            startAdvertisingButton.setBackgroundColor(Color.BLUE);
            stopScanButton.setBackgroundColor(Color.RED);
        });
        return rootView;
    }
}
