package App.Interfaces.Advertiser;

import android.os.Build;
import android.widget.*;
import eazyble.MasterSlave.Advertiser.Advertising1;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.blemasterslave.R;

public class AdvertiserFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.advertising, container, false);

        Advertising1 advertising = new Advertising1(requireContext());
        Button defaultAdvertise = root.findViewById(R.id.default_advertiser);
        defaultAdvertise.setOnClickListener(v -> {
                // Handle advertise button click
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                advertising.startAdvertising();
            }
        });

        Button stopAdvertise = root.findViewById(R.id.stop_advertiser);
        stopAdvertise.setOnClickListener(v -> {
            // Handle advertise button click
            advertising.stopAdvertising();
        });
        return root;
    }
}
