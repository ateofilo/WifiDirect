package com.example.android.wifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DR AT on 06/07/2015.
 *
 * Note: The broadcast executes continuously, in delay of (???) 15 secs, we stopped it
 * when received the first scan
 *
 */
public class WiFiControlActivity extends Activity {

    Context context;

    private WifiManager wiFiManager;


    private TextView tvConsole;
    private Button wiFiScanNetworksButton;

    ExpandableListView expListViewScannedNetworks;
    ExpandableListAdapter<String, String> expListViewAdapterScannedNetworks;
    IntentFilter intentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_control_activity);

        context = getApplicationContext();
        wiFiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        tvConsole = ((TextView) findViewById(R.id.tvConsole));

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        expListViewScannedNetworks = (ExpandableListView) findViewById(R.id.expListViewScannedNetworks);
        expListViewAdapterScannedNetworks = new ExpandableListAdapter<>(this);
        expListViewScannedNetworks.setAdapter(expListViewAdapterScannedNetworks);

        wiFiScanNetworksButton = (Button) findViewById(R.id.btnScanNetworks);
        wiFiScanNetworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiFiScanNetworksButton.setEnabled(false);
                startScan();
            }
        });
    }

    // create a broadcast receives to receive wifi scan notifications
    BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                scanComplete();
            }
        }
    };

    public void scanComplete() {
        expListViewAdapterScannedNetworks.clear();
        // get scan results
        List<ScanResult> networkList = wiFiManager.getScanResults();
        tvConsole.append("\nScan Completed, with results:");
        for (ScanResult network : networkList) {
            tvConsole.append("\n   " + network.SSID);
            expListViewAdapterScannedNetworks.addDataChild(network.SSID, network.toString());
        }
        wiFiScanNetworksButton.setEnabled(true);
        tvConsole.append("\n   ");
       stopScan();
    }

    private void stopScan() {
        unregisterReceiver(scanReceiver);
    }

    private void startScan() {
        registerReceiver(scanReceiver, intentFilter);
        ((WifiManager)getSystemService(Context.WIFI_SERVICE)).startScan();
    }





}