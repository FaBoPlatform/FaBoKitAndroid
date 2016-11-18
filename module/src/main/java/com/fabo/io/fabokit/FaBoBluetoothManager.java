package com.fabo.io.fabokit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

public class FaBoBluetoothManager {
    /**
     * TAG.
     */
    private static String TAG = "FaBoBluetoothClient";

    /**
     * Adapter.
     */
    private BluetoothAdapter mAdapter;

    /**
     * Constructor.
     */
    public FaBoBluetoothManager(){
        if(true)
            Log.i(TAG, "FaBoBluetoothManager()");

        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Init
     */
    public boolean isSupport() {

        if(true)
            Log.i(TAG, "isSupport");

        if(!mAdapter.equals(null)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Bluetoothが有効かどうかのチェック.
     */
    public boolean isEnable() {

        boolean btEnable = mAdapter.isEnabled();
        if(btEnable == true){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Bluetoothデバイスの一覧を返す.
     * @return
     */
    public Set<BluetoothDevice> getDevices(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        return devices;
    }

    /**
     * ペアリング済みFaBoデバイス候補一覧を返す.
     * @return
     */
    public ArrayList<BluetoothDevice> getParingDevice(){

        ArrayList<BluetoothDevice> paringDevices = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();

        for (BluetoothDevice device : devices){
            paringDevices.add(device);
        }

        return paringDevices;
    }
}
