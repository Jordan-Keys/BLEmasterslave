package eazyble.MasterSlave.Sink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RadarView extends View {
    private static final float MAX_RADIUS = 350f;
    private static final float MAX_DISTANCE_METERS = 20f;
    private static final long REFRESH_RATE = 1000;

    private float centerX, centerY;
    private final List<Device> devices = new ArrayList<>();
    private final Paint paint = new Paint();
    private static RadarView instance;
    private final Handler handler = new Handler();
    private final Random random = new Random();

    private float sweepAngle = 0f;

    public RadarView(Context context) {
        super(context);
        instance = this;
        startSimulation();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;
        startSimulation();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Dynamically set the center of the radar based on the view's dimensions
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Background
        canvas.drawColor(Color.BLACK);

        // Radar Sweep Effect (moving line)
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(4);
        paint.setAlpha(100);
        float sweepX = centerX + MAX_RADIUS * (float) Math.cos(sweepAngle);
        float sweepY = centerY + MAX_RADIUS * (float) Math.sin(sweepAngle);
        canvas.drawLine(centerX, centerY, sweepX, sweepY, paint);

        // Increment the sweep angle
        sweepAngle += 0.05f;
        if (sweepAngle > 2 * Math.PI) {
            sweepAngle -= (float) (2 * Math.PI);
        }

        // Radar circles (military style)
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setAlpha(50);
        for (int i = 1; i <= 5; i++) {
            canvas.drawCircle(centerX, centerY, (MAX_RADIUS / 5) * i, paint);
        }

        // Cross lines (military look)
        paint.setColor(Color.GREEN);
        paint.setAlpha(100);
        canvas.drawLine(centerX - MAX_RADIUS, centerY, centerX + MAX_RADIUS, centerY, paint);
        canvas.drawLine(centerX, centerY - MAX_RADIUS, centerX, centerY + MAX_RADIUS, paint);

        // Devices (targets)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        for (Device device : devices) {
            // Convert polar to cartesian
            float pixelDistance = (device.distance / MAX_DISTANCE_METERS) * MAX_RADIUS;
            float x = centerX + pixelDistance * (float) Math.cos(device.angle);
            float y = centerY + pixelDistance * (float) Math.sin(device.angle);

            canvas.drawCircle(x, y, 10f, paint);
        }
    }

    // Simulate rotation / movement of devices and the radar sweep
    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Device device : devices) {
                    // Slowly rotate device
                    device.angle += 0.2f + random.nextFloat() * 0.2f;
                    if (device.angle > 2 * Math.PI) {
                        device.angle -= 2 * (float) Math.PI;
                    }
                }
                invalidate();
                handler.postDelayed(this, REFRESH_RATE);
            }
        }, REFRESH_RATE);
    }

    // External method to receive data string like: [Sink  BT5.  10.89]
    public static void receiveData(String data) {
        if (instance == null) return;
        instance.parseAndDisplay(data);
    }

    private void parseAndDisplay(String rawData) {
        devices.clear();

        rawData = rawData.replace("[", "").replace("]", "").trim();

        String[] lines = rawData.split("\n");
        for (String line : lines) {
            try {
                if (!line.contains("Sink")) continue;

                String[] parts = line.trim().split("\\s+");
                if (parts.length < 3) continue;

                float distanceMeters = Float.parseFloat(parts[2]);
                float angle = random.nextFloat() * 2f * (float) Math.PI;
                devices.add(new Device(distanceMeters, angle));
            } catch (Exception e) {
                android.util.Log.e("RadarView", "Parse error: " + line, e);
            }
        }

        invalidate();
    }

    // Device info
    public static class Device {
        float distance;
        float angle;

        public Device(float distance, float angle) {
            this.distance = distance;
            this.angle = angle;
        }
    }
}
