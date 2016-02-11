package com.example.terrelewis.bluetooth_test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity
{
    TextView myLabel;
    TextView myLabel1;
    //BroadcastReceiver bluetoothreceiver;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    float[] weights;
    int readBufferPosition;
    float counter;
    String data1;
    volatile boolean stopWorker;
    IntentFilter bluetoothIntentFilter = new IntentFilter();
    int flag=0;
int flag1=0;
    int count=0;
String data;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bluetoothreceiver, intentFilter);
        Button openButton = (Button)findViewById(R.id.open);
        weights=new float[3];
        Button closeButton = (Button)findViewById(R.id.close);
        myLabel = (TextView)findViewById(R.id.label);
        myLabel1=(TextView)findViewById((R.id.label1));

        bluetoothIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bluetoothreceiver, bluetoothIntentFilter);
        bluetoothIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothreceiver, bluetoothIntentFilter);
        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    findBT();

                }
                catch (Exception ex) { }
            }
        });


        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothreceiver != null) {
            //this.unregisterReceiver(bluetoothreceiver);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void findBT() throws IOException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
            Log.e("TAG", "badaptnull");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            myLabel.setText("bluetooth adapter available");
            Log.e("TAG", "badaptavailnot");
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-05"))
                {
                    mmDevice = device;
                    myLabel.setText("Bluetooth Device Found");
                    Log.e("TAG", "bdfound");

                    mmDevice.getBondState();

                    mmDevice.createBond();

                    openBT();
                    break;
                }
            }
        }

    }

    void openBT() throws IOException
    {
Log.e("TAG", "in openBT");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        myLabel.setText("Bluetooth Opened");
        if(!mmSocket.isConnected()) {
           Log.e("TAG", "not connected");
            mmSocket.connect();

        }
            if (mmSocket.isConnected()){
            mmOutputStream = mmSocket.getOutputStream();
            Log.e("asd", "connected$");
        }

        flag=1;

        Log.e("TAG", "Connected");
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();


    }

    void beginListenForData()
    {   myLabel.setText("Entered thread");
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {

                    try
                    {

           int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {

                                        public void run()
                                        {

                                            if(data.length()>1) {
                                                myLabel1.setText(" ");
                                                myLabel.setText(data);
                                               Log.d("FLOAT", data);

                                                //data1=data.substring(0, (data.length() - 2));
                                                counter=Float.parseFloat(data);
                                                if(flag1==0) {
                                                    Log.e("TAG", "IF1.1");
                                                    if (counter > 3000.0) {
                                                        Log.e("TAG", "IF2");
                                                        if (count == 0) {
                                                            Log.e("TAG", "IF3.1");
                                                            weights[count] = counter;
                                                            count++;
                                                        } else if (Math.abs(counter - weights[(count - 1)]) < 1000.0) {
                                                            Log.e("TAG", "IF3.2");
                                                            weights[count] = counter;
                                                            count++;
                                                            if (count >= 3) {
                                                                Log.e("TAG", "IF4");
                                                                myLabel1.setText("order placed with weight=" + String.valueOf(weights[2]));
                                                                flag1 = 1;
                                                            }
                                                        }
                                                    }
                                                }
                                                else
                                                        if(flag1==1)
                                                        {
                                                            Log.e("TAG", "IF1.2");
                                                            if(Math.abs(counter-weights[2])>=3000.0)
                                                            {
                                                                Log.e("TAG", "IF5");
                                                                count=0;
                                                                flag1=0;

                                                            }

                                                        }




                                        }}
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }



    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");

    }



    public BroadcastReceiver bluetoothreceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView t1 = (TextView) findViewById(R.id.label);
            TextView t2 = (TextView) findViewById(R.id.label1);
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
               Log.e("TAG", "ACTION_STATE_CHANGED");
                t2.setText("Change ACTION_STATE_CHANGED");
            } else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.e("TAG", "DISCONNECT");
                t2.setText("Change ACL_DISCONNECT");
                try {
                    flag = 0;
                    closeBT();
                    Log.e("TAG", "CloseBT");
                } catch (IOException e) {
                    Log.e("TAG", "CloseBTerror");
                            e.printStackTrace();
                }
                    while(flag!=1){

                        try {
                            Log.e("TAG", "Loop entered");
                            findBT();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


            }
        }
                else
                if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
                    Log.e("TAG", "CONNECT");
                    t2.setText("Change ACL_CONNECT");
                }
else
            if(intent.getAction().equals(BluetoothAdapter.STATE_DISCONNECTED)){
                Log.e("TAG", "STATEDISCONNECt");
                t2.setText("Change STATE_DISCONNECTED ");
            }

            }
    };
}