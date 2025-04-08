//package App;
//
//import android.os.Bundle;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.blemasterslave.R;
//import eazyble.MasterSlave.Advertiser.Advertising1;
//import eazyble.MasterSlave.Advertiser.Testing2;
//
//public class Testing extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.hello);
//        Advertising1 advertising = new Advertising1(this);
//        Button defaultAdvertise = findViewById(R.id.testId);
//        defaultAdvertise.setOnClickListener(v -> {
//            advertising.startAdvertising(false, null, true);
//        });
//        Testing2 testing = new Testing2(this);
//        Button testsupport = findViewById(R.id.testFeatures);
//        testsupport.setOnClickListener(v -> {
//            testing.testSupportedFeatures();
//        });
//    }
//}
//
