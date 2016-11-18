package com.fabo.io.fabokit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FaBoBluetoothClient extends Thread {

    /**
     * TAG.
     */
    private static String TAG = "FaBoBluetoothClient";

    /**
     * BluetoothDevice.
     */
    private static BluetoothDevice mDevice;

    /**
     * BluetoothAdapter.
     */
    private static BluetoothAdapter mAdapter;

    /**
     * BluetoothSocket.
     */
    private static BluetoothSocket mSocket;

    /**
     * InputStream.
     */
    private static InputStream mInput;

    /**
     * OutputStream.
     */
    private static OutputStream mOutput;


    DataInputStream mmInStream;
    DataOutputStream mmOutStream;

    /**
     * Version.
     */
    private static String mVersion = "1";

    /**
     * Event listener.
     */
    private FaBoBluetoothClientListener listener = null;

    /**
     *  UUIDの生成.
     */
    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * Bluetooth Adapterを設定.
     * @param mAdapter
     */
    public void setAdapter(BluetoothAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    /**
     * Threadの状態
     */
    private boolean isRunning;

    /**
     * Bluetoothデバイスを設定する.
     * @param mBluetoothDevice
     */
    public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mDevice = mBluetoothDevice;
    }

    /**
     * Bluetooth部分の初期化.
     */
    public boolean init() {
        isRunning = false;

        if(mDevice == null) {
            return false;
        }

        if(true)
            Log.i(TAG, "mDevice.getName(): " + mDevice.getName());

        mSocket = null;
        try{
            mSocket = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        }catch(IOException e){

            if(true)
                Log.e(TAG, "Error:" + e);
            e.printStackTrace();
            return false;
        }

        try {
            mSocket.connect();
        } catch (IOException e) {
            if(true)
                Log.e(TAG, "Error:" + e);

            e.printStackTrace();

            return false;
        }

        try {
            mInput = mSocket.getInputStream();
            mOutput = mSocket.getOutputStream();

            mmInStream = new DataInputStream(mInput);
            mmOutStream = new DataOutputStream(mOutput);

        } catch (IOException e1) {
            if(true)
                Log.e(TAG, "Error:" + e1);

            e1.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 送受信開始.
     */
    public void run() {

        //接続要求を出す前に、検索処理を中断する。
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }

        byte[] buf = new byte[1024];
        byte[] stockBuf = new byte[1024];
        String response = null;
        int tmpBuf = 0;
        int offsetBuf = 0;
        int offsetStock = 0;
        try {
            if (true)
                Log.e(TAG, "Write version:");
            write(mVersion.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (true)
            Log.e(TAG, "Run loop:");

        isRunning = true;
        int capacity = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        byte lastByte = 0x00;
        while (isRunning) {

            try {
                if (mmInStream.available() > 0) {
                    try {
                        tmpBuf = mmInStream.read(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (tmpBuf != 0) {

                        int i = 0;
                        for (byte oneBuf : buf) {

                            if (oneBuf == (byte) 0x0a && lastByte == 0x0d) {
                                Log.i(TAG, "0x0d Break!!");
                                try {
                                    byte tmp[] = new byte[buffer.limit()];
                                    buffer.position(0);
                                    buffer.get(tmp);
                                    response = new String(tmp, "UTF-8");

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (listener != null) {
                                        listener.onReceiver(response);
                                    }
                                    lastByte = 0x00;
                                    i = 0;
                                    buf = new byte[1024];
                                    buffer = ByteBuffer.allocate(capacity);

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                lastByte = oneBuf;
                                if (oneBuf != 0) {
                                    Log.i(TAG, "i:" + i + ">" + Integer.toHexString(oneBuf));
                                    buffer.put(oneBuf);
                                }
                                i++;
                            }

                        }

                    }
                } else {
                    SystemClock.sleep(400);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SocketをCloseする.
     */
    public void cancel() {

        isRunning = false;

        if(mOutput != null) {
            try {
                mOutput.close();
                mInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mInput != null) {
            try {
                mInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write.
     *
     * @param buf 書き込むByteバッファー.
     */
    public void write(byte[] buf){
        try {
            mOutput.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * リスナーを追加する
     * @param listener
     */
    public void setListener(FaBoBluetoothClientListener listener){
        this.listener = listener;
    }

    /**
     * リスナーを削除する
     */
    public void removeListener(){
        this.listener = null;
    }
}