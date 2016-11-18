package com.fabo.io.fabokit;

public class FaBoBluetoothConfig {

    public static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        FaBoBluetoothConfig.debug = debug;
    }
}
