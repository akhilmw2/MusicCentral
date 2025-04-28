package com.example.musiccentral;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MusicServiceTest";
    private IMusicService musicService;
    private boolean bound = false;

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = IMusicService.Stub.asInterface(service);
            bound = true;
            Log.d(TAG, "Service connected");
            try {
                int[] clips = musicService.listClips();
                Log.d(TAG, "Available clips: " + java.util.Arrays.toString(clips));
            } catch (Exception e) {
                Log.e(TAG, "Failed to list clips", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            musicService = null;
            Log.d(TAG, "Service disconnected");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show the simple splash UI
        setContentView(R.layout.activity_main);
        // Automatically bind to the MusicService on startup
        Intent intent = new Intent(this, MusicService.class);
        boolean ok = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService returned: " + ok);
    }

    @Override
    protected void onDestroy() {
        if (bound) {
            unbindService(conn);
            bound = false;
        }
        super.onDestroy();
    }
}
