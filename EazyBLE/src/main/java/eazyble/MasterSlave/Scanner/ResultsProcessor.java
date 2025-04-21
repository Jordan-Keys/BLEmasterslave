package eazyble.MasterSlave.Scanner;

import static io.grpc.okhttp.internal.Platform.logger;

public class ResultsProcessor {
    private static volatile ResultsProcessor instance;
    private String latestDeviceData;
    private ResultsProcessor() {}

    public static ResultsProcessor getInstance() {
        if (instance == null) {
            synchronized (ResultsProcessor.class) {
                if (instance == null) {
                    instance = new ResultsProcessor();
                }
            }
        }
        return instance;
    }

    public void processScannedData(String deviceData) {
        logger.info("Processing device data: " + deviceData);
        this.latestDeviceData = deviceData;
    }

    public String getLatestDeviceData() {
        return latestDeviceData != null ? latestDeviceData : "";
    }
}