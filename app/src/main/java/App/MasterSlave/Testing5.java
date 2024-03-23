//package App.MasterSlave;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.blemasterslave.R;
//import org.jetbrains.annotations.NotNull;
//
//public class Testing4 extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Declare an instance of Permissions
//        Permissions permissions = new Permissions(); // Initialize Permissions
//
//        // Check Bluetooth support and handle permissions
//        permissions.checkBluetoothSupport(this);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Handle the result of Bluetooth permission request
//        boolean handled = Permissions.handleOnActivityResult(this, requestCode, resultCode);
//        if (!handled) {
//            // If not handled by Bluetooth permission, you can handle other results here
//        }
//    }
//
//    // Override onRequestPermissionsResult to handle location permission result
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // Check if the request is for location permission
//        if (requestCode == Permissions.REQUEST_LOCATION_PERMISSION) {
//            // Check if permission is granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Location permission granted
//                Permissions.grantedPermission(this);
//                //Location permission denied
//            } else {
//                Permissions.deniedPermission(this);
//            }
//        }
//    }
//}
