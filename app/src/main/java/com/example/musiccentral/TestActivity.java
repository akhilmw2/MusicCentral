package com.example.musiccentral;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "MusicServiceTest";

    private IMusicService musicService;
    private boolean bound = false;

    // UI buttons
    private Button btnBind, btnList, btnPlay, btnPause, btnResume, btnStop, btnUnbind;

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = IMusicService.Stub.asInterface(service);
            bound = true;
            Log.d(TAG, "Service connected");
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
        setContentView(R.layout.activity_test);

        // Find the buttons
        btnBind   = findViewById(R.id.btn_bind);
        btnList   = findViewById(R.id.btn_list);
        btnPlay   = findViewById(R.id.btn_play);
        btnPause  = findViewById(R.id.btn_pause);
        btnResume = findViewById(R.id.btn_resume);
        btnStop   = findViewById(R.id.btn_stop);
        btnUnbind = findViewById(R.id.btn_unbind);

        // Bind
        btnBind.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        });

        // List clips
        btnList.setOnClickListener(v -> {
            if (!bound) return;
            try {
                int[] clips = musicService.listClips();
                Log.d(TAG, "Available clips: " + Arrays.toString(clips));
            } catch (Exception e) {
                Log.e(TAG, "listClips failed", e);
            }
        });

        // Play clip #1
        btnPlay.setOnClickListener(v -> {
            if (!bound) return;
            try { musicService.play(1); Log.d(TAG, "play(1) called"); }
            catch (Exception e) { Log.e(TAG, "play failed", e); }
        });

        // Pause
        btnPause.setOnClickListener(v -> {
            if (!bound) return;
            try { musicService.pause(); Log.d(TAG, "pause() called"); }
            catch (Exception e) { Log.e(TAG, "pause failed", e); }
        });

        // Resume
        btnResume.setOnClickListener(v -> {
            if (!bound) return;
            try { musicService.resume(); Log.d(TAG, "resume() called"); }
            catch (Exception e) { Log.e(TAG, "resume failed", e); }
        });

        // Stop
        btnStop.setOnClickListener(v -> {
            if (!bound) return;
            try { musicService.stop();   Log.d(TAG, "stop() called"); }
            catch (Exception e) { Log.e(TAG, "stop failed", e); }
        });

        // Unbind
        btnUnbind.setOnClickListener(v -> {
            if (!bound) return;
            unbindService(conn);
            bound = false;
            Log.d(TAG, "Service unbound");
        });
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
