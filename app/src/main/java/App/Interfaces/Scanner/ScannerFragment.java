package App.Interfaces.Scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.blemasterslave.databinding.ScannerFragmentBinding;

public class ScannerFragment extends Fragment {

    private ScannerFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScannerModelView dashboardViewModel =
                new ViewModelProvider(this).get(ScannerModelView.class);

        binding = ScannerFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView textView = binding.results;
        dashboardViewModel.getText(textView);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
