package App.Interfaces.Advertiser;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.blemasterslave.R;

import eazyble.MasterSlave.Sink.RadarView;
import eazyble.MasterSlave.Sink.Sink;

public class SinkFragment extends Fragment {
    private boolean isLooping = false;
    private boolean isSinkRunning = false;
    private final Handler handler = new Handler();
    private Runnable sinkToggleRunnable;
    private Sink sink;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sink, container, false);

        root.findViewById(R.id.radarView);

        sink = new Sink(requireContext());

        Button startSink = root.findViewById(R.id.startSink);
        Button stopSink = root.findViewById(R.id.stopSink);
        Button startRadar = root.findViewById(R.id.radar);

        // Start button behavior
        startSink.setOnClickListener(v -> {
            if (isLooping) return; // Already running

            isLooping = true;

            // Define loop behavior
            sinkToggleRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!isLooping) return;

                    if (isSinkRunning) {
                        sink.stopSink();
                        isSinkRunning = false;
                    } else {
                        sink.startSink();
                        isSinkRunning = true;
                    }

                    // Schedule next toggle after 5 seconds
                    handler.postDelayed(this, 1000);
                }
            };

            handler.post(sinkToggleRunnable); // Start the loop
        });

        // Stop button behavior
        stopSink.setOnClickListener(v -> {
            isLooping = false;
            handler.removeCallbacks(sinkToggleRunnable);

            if (isSinkRunning) {
                sink.stopSink();
                isSinkRunning = false;
            }
            Toast.makeText(getContext(), "Sink terminated", Toast.LENGTH_SHORT).show();
        });

        startRadar.setOnClickListener(v -> {
            String mockData = "[Sink  BT5.  10.89]\n[Sink  DEV1.  5.42]\n[Sink  TEST3.  7.25]\n[Sink  TEST3.  5.25\n[Sink  TEST3.  6]";
            RadarView.receiveData(mockData);
            Toast.makeText(getContext(), "Simulated radar data", Toast.LENGTH_SHORT).show();

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLooping = false;
        handler.removeCallbacks(sinkToggleRunnable);

        if (isSinkRunning) {
            sink.stopSink();
            isSinkRunning = false;
        }
    }
}
