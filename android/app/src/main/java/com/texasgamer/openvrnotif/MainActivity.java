package com.texasgamer.openvrnotif;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private MainAcvitiyReceiver mainAcvitiyReceiver;
    private BottomSheetBehavior bottomSheetBehavior;

    private String serverAddr = "http://127.0.0.1:3753/";
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfFirstRun();

        startSocketService();
        requestConnectionStatus();

        setupUi();

        mainAcvitiyReceiver = new MainAcvitiyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.texasgamer.openvrnotif.MAIN_ACTIVITY");
        registerReceiver(mainAcvitiyReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String enabledNotificationListeners =
                android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if(enabledNotificationListeners.contains("com.texasgamer.openvrnotif.NotificationService")) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mainAcvitiyReceiver);
    }

    private void checkIfFirstRun() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_first_run), true)) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupUi() {
        final Button connectBtn = (Button) findViewById(R.id.connectBtn);
        final Button testNotifBtn = (Button) findViewById(R.id.testNotifBtn);
        final EditText serverAddrField = ((EditText) findViewById(R.id.serverAddrField));
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connected) {
                    serverAddr = serverAddrField.getText().toString();
                    connectBtn.setText(R.string.btn_connecting);
                    Intent i = new  Intent("com.texasgamer.openvrnotif.SOCKET_SERVICE");
                    i.putExtra("type", "connect");
                    i.putExtra("address", serverAddrField.getText().toString());
                    sendBroadcast(i);
                } else {
                    connectBtn.setText(R.string.btn_disconnecting);
                    Intent i = new  Intent("com.texasgamer.openvrnotif.SOCKET_SERVICE");
                    i.putExtra("type", "disconnect");
                    sendBroadcast(i);
                }
            }
        });

        testNotifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected) {
                    Intent i = new  Intent("com.texasgamer.openvrnotif.SOCKET_SERVICE");
                    i.putExtra("type", "test");
                    sendBroadcast(i);
                } else {
                    Snackbar.make(findViewById(R.id.main_content), R.string.snackbar_not_connected, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        serverAddrField.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_last_addr), ""));

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        findViewById(R.id.enableNotificationsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });
    }

    private void updateConnectBtn() {
        runOnUiThread(new Runnable() {
            public void run() {
                ((Button) findViewById(R.id.connectBtn)).setText(connected ? R.string.btn_disconnect : R.string.btn_connect);
            }
        });
    }

    private void startSocketService() {
        Intent i = new Intent(this, SocketService.class);
        startService(i);
    }

    private void requestConnectionStatus() {
        Intent i = new  Intent("com.texasgamer.openvrnotif.SOCKET_SERVICE");
        i.putExtra("type", "status");
        sendBroadcast(i);
    }

    class MainAcvitiyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if (type.equals("connected")) {
                connected = true;
                serverAddr = intent.getStringExtra("address");
                ((EditText) findViewById(R.id.serverAddrField)).setText(serverAddr);
                updateConnectBtn();
                Snackbar.make(findViewById(R.id.main_content), R.string.snackbar_connected, Snackbar.LENGTH_SHORT).show();
            } else if (type.equals("disconnected")) {
                connected = false;
                updateConnectBtn();
                Snackbar.make(findViewById(R.id.main_content), R.string.snackbar_disconnected, Snackbar.LENGTH_SHORT).show();
            } else if (type.equals("notif-sent")) {
                Snackbar.make(findViewById(R.id.main_content), R.string.snackbar_notif_confirm, Snackbar.LENGTH_SHORT).show();
            } else if (type.equals("notif-failed")) {
                Snackbar.make(findViewById(R.id.main_content), R.string.snackbar_notif_fail, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
