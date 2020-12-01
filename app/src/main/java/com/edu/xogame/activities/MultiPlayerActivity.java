package com.edu.xogame.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.edu.xogame.IFunction;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.network.Client;
import com.edu.xogame.network.Host;
import com.edu.xogame.network.WiFiDirectBroadcastReceiver;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MultiPlayerActivity extends AppCompatActivity {
    private static WifiP2pManager manager;
    private static WifiP2pManager.Channel channel;
    private static BroadcastReceiver receiver;
    private ListView listView;
    private IntentFilter intentFilter;

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] devicesNameArray;
    private WifiP2pDevice[] devicesArray;
    private boolean isHost;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utilities.IS_AVAILABLE = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);

        resetWifi();
        listView = findViewById(R.id.listView);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> discoverPlayers());
    }

    private void resetWifi() {
        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);
    }

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            if (!peerList.getDeviceList().equals(peers))
                peers.clear();
            peers.addAll(peerList.getDeviceList());

            int size = peerList.getDeviceList().size();
            devicesArray = new WifiP2pDevice[size];
            devicesNameArray = new String[size];

            int index = 0;
            for (WifiP2pDevice device : peerList.getDeviceList()) {
                devicesNameArray[index] = device.deviceName;
                devicesArray[index] = device;
                ++index;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, devicesNameArray);
            listView.setAdapter(adapter);

            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No device Found", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private void startGamePlayActivity() {
        Intent intent = new Intent(this, GamePlayActivity.class);
        if (dialog != null)
            dialog.dismiss();

        intent.putExtra("PlayType", "Player");
        intent.putExtra("GoFirst", isHost);
        startActivity(intent);
    }

    private void discoverPlayers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("Connect", "GOOD");

            }

            @Override
            public void onFailure(int reason) {
                Log.e("Connect", reason + "");
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            final WifiP2pDevice device = devicesArray[position];
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Connect to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    showLoadingDialog(device.deviceName);
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "Cannot connect to " + device.deviceName, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showLoadingDialog(String deviceName) {
        Utilities.HANDLER.post(() -> dialog = ProgressDialog.show(this, "",
                "Đang chờ đối thủ", true));
    }

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = info -> {
        final InetAddress groupOwnerAddress = info.groupOwnerAddress;

        IFunction startActivity = this::startGamePlayActivity;
        if (info.groupFormed && info.isGroupOwner) {
            // Host
            Log.e("Connect", "HOST");

            if (Utilities.HOST != null)
                Utilities.HOST.kill();

            Utilities.HOST = new Host(startActivity);
            Utilities.HOST.start();
            isHost = true;

        } else if (info.groupFormed) {
            // Client
            Log.e("Connect", "CLIENT");
            if (Utilities.CLIENT != null)
                Utilities.CLIENT.kill();
            Utilities.CLIENT = new Client(groupOwnerAddress, startActivity);
            Utilities.CLIENT.start();
            isHost = false;
        }
    };

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        registerReceiver(receiver, intentFilter);
        discoverPlayers();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        if(dialog != null)
            dialog.dismiss();
    }

    public static void disconnect(Context context) {
        if (manager != null && channel != null) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (manager != null) {
                manager.requestGroupInfo(channel, group -> {
                    if (group != null && manager != null && channel != null) {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d("connect", "removeGroup onSuccess -");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("connect", "removeGroup onFailure -" + reason);
                            }
                        });
                    }
                });
            }

        }
    }

}
