package com.fabo.io.fabokit;

import java.util.EventListener;

public interface FaBoBluetoothClientListener extends EventListener {

    /**
     * 結果受信.
     */
    public void onReceiver(String response);

}