package App;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.blemasterslave.databinding.ActivityMainBinding;
import com.example.blemasterslave.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //inflating the custom toolbar layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customToolbarView = inflater.inflate(R.layout.custom_toolbar, null);
        ConstraintLayout mainLayout = findViewById(R.id.container);
        mainLayout.addView(customToolbarView);


        // Logic for handling the bottom navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            if (item.getItemId() == R.id.scanner) {
                navController.navigate(R.id.scanner);
                return true;
            } else if (item.getItemId() == R.id.advertiser) {
                navController.navigate(R.id.advertiser);
                return true;
            }
            return false;
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        if (item.getItemId() == R.id.action_history) {
//            // Handle history action
//            navController.navigate(R.id.history_fragment);
//            return true;
//        } else if (item.getItemId() == R.id.action_map) {
//            // Handle map action
//            navController.navigate(R.id.map_fragment);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
