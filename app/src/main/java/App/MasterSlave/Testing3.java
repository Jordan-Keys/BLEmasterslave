package App.MasterSlave;

import App.MasterSlave.Scanner.Scanning;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.blemasterslave.R;

public class Testing3 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permissions permissions = new Permissions();
        permissions.checkBluetoothSupport(this);
        setContentView(R.layout.scannerlistbulder);
        Scanning scanning = new Scanning(this);
        scanning.scanLeDevices();
    }
}