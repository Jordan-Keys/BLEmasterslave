//package App.MasterSlave;
//
//import App.Interfaces.Scanner.ScannerResultsBuilder;
//import App.MasterSlave.Scanner.Scanning;
//import android.os.Bundle;
//import android.widget.ListView;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.blemasterslave.R;
//import java.util.ArrayList;
//import java.util.List;
//import App.MasterSlave.Scanner.ScanResultAdapter;
//
//
//public class Testing3 extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.scannerlistview);
//
//        ListView listView = findViewById(R.id.scannerListView);
//        List<ScannerResultsBuilder> scanResults = new ArrayList<>();
//        ScanResultAdapter adapter = new ScanResultAdapter(this, scanResults);
//        listView.setAdapter(adapter);
//
//        // Initialize and start scanning
//        Scanning scanning = new Scanning(this, scanResults, adapter);
//        scanning.scanLeDevices();
//    }
//}
