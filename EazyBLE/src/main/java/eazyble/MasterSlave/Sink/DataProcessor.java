package eazyble.MasterSlave.Sink;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedWriter;

public class DataProcessor {

    // Method to calculate distance from RSSI
    private static double calculateDistance(double rssi, double referenceRssi, double pathLossExponent) {
        return Math.pow(10, (referenceRssi - rssi) / (10 * pathLossExponent));
    }

    @SuppressLint("DefaultLocale")
    public static void handleScannedData(HashMap<String, String> scannedDevices, Set<String> advertisedStrings) {
        List<String> processedList = new ArrayList<>();

        // Process directly scanned devices
        for (String mac : scannedDevices.keySet()) {
            String info = scannedDevices.get(mac);

            // Extract device name and RSSI
            String name = "Unknown";
            String rssiStr = "";

            if (info != null && info.contains("RSSI:")) {
                String[] parts = info.split("RSSI:");
                name = parts[0].trim();
                rssiStr = parts[1].trim();
            }

            // Convert the RSSI to distance
            try {
                double rssi = Double.parseDouble(rssiStr);
                double referenceRssi = -50;
                double pathLossExponent = 2.7;
                double distance = calculateDistance(rssi, referenceRssi, pathLossExponent);

                // Trim name to 4 characters
                if (name.length() > 4) {
                    name = name.substring(0, 4);
                }

                // Add the processed data with distance instead of RSSI
                processedList.add("Sink  " + name + "  " + String.format("%.2f", distance));
            } catch (NumberFormatException e) {
                android.util.Log.e("DataProcessor", "Error parsing RSSI value for device: " + name, e);
            }
        }

        // Process advertised strings
        for (String adv : advertisedStrings) {
            if (adv == null || adv.isEmpty()) continue;

            String[] deviceEntries = adv.split(";");
            for (String deviceEntry : deviceEntries) {
                String[] brandSplit = deviceEntry.split(":");
                if (brandSplit.length < 2) continue;

                String brand = brandSplit[0].trim();
                String[] macDataArray = brandSplit[1].split(",");

                for (String item : macDataArray) {
                    item = item.trim();
                    if (item.length() < 19) continue; // sanity check

                    try {
                        // Extract name and RSSI from the advertised item
                        String deviceName = item.substring(12, item.length() - 3);
                        String rssiStr = item.substring(item.length() - 3);

                        // Convert the RSSI to a double
                        double rssi = Double.parseDouble(rssiStr);

                        // Convert the RSSI to distance for advertised devices
                        double referenceRssi = -50;
                        double pathLossExponent = 2.7;
                        double distance = calculateDistance(rssi, referenceRssi, pathLossExponent);

                        // Add processed data to list
                        processedList.add(brand + "  " + deviceName + "  " + String.format("%.2f", distance));
                    } catch (Exception e) {
                        android.util.Log.e("DataProcessor", "Error processing item: " + item, e);
                    }
                }
            }
        }


        // generate the formatted data and send it to the server directly
        sendProcessedDataToServer(processedList);
    }

    // Method to send the processed data to the server
    private static void sendProcessedDataToServer(List<String> processedList) {
        // Use sets to store device names and brands
        Set<String> deviceNames = new LinkedHashSet<>();
        Set<String> brands = new LinkedHashSet<>();

        for (String entry : processedList) {
            String[] parts = entry.trim().split("\\s+");
            if (parts.length >= 2) {
                brands.add(parts[0]);
                deviceNames.add(parts[1]);
            }
        }


        StringBuilder dataToSend = getStringBuilder(processedList, deviceNames, brands);
        android.util.Log.i("DataProcessor", "Overall: " + dataToSend);

        // Send the formatted data to the server
        sendDataToServer(dataToSend.toString());

        RadarView.receiveData(processedList.toString());

    }

    private static @NonNull StringBuilder getStringBuilder(List<String> processedList, Set<String> deviceNames, Set<String> brands) {
        StringBuilder dataToSend = new StringBuilder();

        // Write device names first
        for (String name : deviceNames) {
            dataToSend.append(name).append("\n");
        }

        // Write brands next
        for (String brand : brands) {
            dataToSend.append(brand).append("\n");
        }

        // Add a blank line
        dataToSend.append("\n");

        // Write the full list of devices
        for (String entry : processedList) {
            dataToSend.append(entry).append("\n");
        }
        return dataToSend;
    }

    // Method to send the actual data to the server
    private static void sendDataToServer(String data) {
        new Thread(() -> {
            try {
                String boundary = "----Boundary" + System.currentTimeMillis();
                String LINE_FEED = "\r\n";
                String charset = "UTF-8";
                String urlString = "http://192.168.43.3:5000/visualize";

                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset));

                // Start of multipart body
                writer.write("--" + boundary);
                writer.write(LINE_FEED);
                writer.write("Content-Disposition: form-data; name=\"data\"; filename=\"device_list.txt\"");
                writer.write(LINE_FEED);
                writer.write("Content-Type: text/plain");
                writer.write(LINE_FEED);
                writer.write(LINE_FEED);
                writer.flush();

                // Write the actual data
                writer.write(data);
                writer.flush();

                // End of multipart body
                writer.write(LINE_FEED);
                writer.write("--" + boundary + "--");
                writer.write(LINE_FEED);
                writer.flush();
                writer.close();

                // Get server response
                int responseCode = connection.getResponseCode();
                android.util.Log.i("DataProcessor", "Upload response code: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } catch (java.net.ConnectException ce) {
                android.util.Log.w("DataProcessor", "Server not reachable. Skipping upload.", ce);
            } catch (Exception e) {
                android.util.Log.e("DataProcessor", "Unexpected error while sending data to server", e);
            }
        }).start();
    }

}
