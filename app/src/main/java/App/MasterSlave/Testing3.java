//package App.MasterSlave.Scanner;
//
//import App.Interfaces.Scanner.ScannerResultsBuilder;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import com.example.blemasterslave.R;
//
//import java.util.List;
//
//public class ScanResultAdapter extends ArrayAdapter<ScannerResultsBuilder> {
//    public ScanResultAdapter(Context context, List<ScannerResultsBuilder> scanResults) {
//        super(context, 0, scanResults);
//    }
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scannerlistbulder, parent, false);
//        }
//        // Getting the data items for the positions
//        ScannerResultsBuilder result = getItem(position);
//
//        // Finding views within layout
//        TextView deviceNameTextView = convertView.findViewById(R.id.deviceNameTextView);
//        TextView rssiTextView = convertView.findViewById(R.id.rssiTextView);
//        TextView uuidTextView = convertView.findViewById(R.id.uuidTextView);
//        TextView macAddressTextView = convertView.findViewById(R.id.macAddressTextView);
//
//        // Populating the data into the template view using the data object
//        if (result != null) {
//            deviceNameTextView.setText("Device Name: " + result.getDeviceName());
//            rssiTextView.setText("RSSI: " + result.getRssi() + " dBms");
//            uuidTextView.setText("UUIDs: " + result.getUuid());
//            macAddressTextView.setText("MAC ADDRESS: " + result.getMacAddress());
//
//        }
//        return convertView;
//    }
//}
//


//package App.Interfaces.Scanner;
//
//import App.MasterSlave.Scanner.ScanResultAdapter;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import com.example.blemasterslave.R;
//import androidx.fragment.app.Fragment;
//import java.util.ArrayList;
//import java.util.List;
//import App.MasterSlave.Scanner.Scanning;
//
//public class ScannerFragment extends Fragment {
//    private ScanResultAdapter adapter;
//    private List<ScannerResultsBuilder> scanResults = new ArrayList<>();
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.scannerlistview, container, false);
//
//        ListView listView = rootView.findViewById(R.id.scannerListView);
//        adapter = new ScanResultAdapter(getActivity(), scanResults);
//        listView.setAdapter(adapter);
//
//        // Initialize and start scanning
//        Scanning scanning = new Scanning(getActivity(), scanResults, adapter);
//        scanning.scanLeDevices();
//
//        return rootView;
//
//    }
//
//    // Method to update scannerlistview results
//    public void updateScanResults(List<ScannerResultsBuilder> results) {
//        scanResults.clear();
//        scanResults.addAll(results);
//        adapter.notifyDataSetChanged();
//    }
//}