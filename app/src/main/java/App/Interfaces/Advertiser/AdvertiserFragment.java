package App.Interfaces.Advertiser;

import App.MasterSlave.Advertiser.Advertising;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.blemasterslave.R;

public class AdvertiserFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.testing, container, false);

        Button advertiseButton = root.findViewById(R.id.advertiser);
        advertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle advertise button click
                startAdvertising(); // Call your advertising method here
            }
        });

        return root;
    }

    // Method to start advertising
    private void startAdvertising() {
        // Call the advertising method from your Advertising class
        Advertising advertising = new Advertising(this.getContext());
        advertising.startAdvertising();
    }
}
