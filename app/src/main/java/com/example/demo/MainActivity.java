package com.example.demo;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.Set;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private Button listBtn;
    private Button findBtn;
    private TextView text;
    private IntentFilter filter;
    private BluetoothAdapter defaultBtAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultBtAdapter = BluetoothAdapter.getDefaultAdapter();
        text = findViewById(R.id.text);

        listBtn = findViewById(R.id.paired);
        listBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                list(v);
            }
        });

        findBtn = findViewById(R.id.search);
        findBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                find(v);
            }
        });

        myListView = findViewById(R.id.listView1);
        btAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(btAdapter);

        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReceiver, filter);
        defaultBtAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (defaultBtAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    public void find(View view) {
        btAdapter.clear();
        Toast.makeText(getApplicationContext(), "Fetching Devices", Toast.LENGTH_SHORT).show();
    }

    public void list(View view) {
        pairedDevices = defaultBtAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
            btAdapter.add(device.getName() + "\n" + device.getAddress());
        }

    }

    BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btAdapter.add(device.getName() + "\n" + device.getAddress());
                Toast.makeText(MainActivity.this, device.getName(), Toast.LENGTH_SHORT).show();
                btAdapter.notifyDataSetChanged();
            }
        }
    };

    public void on(View view) {
        if (!defaultBtAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Bluetooth turned on",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void off(View view) {
        defaultBtAdapter.disable();
        text.setText("Status: Disconnected");
        Toast.makeText(getApplicationContext(), "Bluetooth turned off", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

}
