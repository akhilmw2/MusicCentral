package com.example.musiccentral;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;

import android.os.IBinder;

import androidx.annotation.Nullable;


public class MusicService extends Service {

    private enum PlaybackState {
        STOPPED,
        PLAYING,
        PAUSED
    }

    private PlaybackState playbackState = PlaybackState.STOPPED;

    // Generated AIDL stub
    private final IMusicService.Stub binder = new IMusicService.Stub() {
        @Override
        public int[] listClips() {
            int[] ids = new int[CLIP_RES_IDS.length];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = i + 1;
            }
            return ids;
        }

        @Override
        public void play(int clipId) {
            playClip(clipId);
            playbackState = PlaybackState.PLAYING;
        }

        @Override
        public void pause() {
            if (mediaPlayer != null && playbackState == PlaybackState.PLAYING) {
                mediaPlayer.pause();
                playbackState = PlaybackState.PAUSED;
            }
        }

        @Override
        public void resume() {
            if (mediaPlayer != null && playbackState == PlaybackState.PAUSED) {
                mediaPlayer.start();
                playbackState = PlaybackState.PLAYING;
            }
        }

        @Override
        public void stop() {
            if (mediaPlayer != null && (playbackState == PlaybackState.PLAYING || playbackState == PlaybackState.PAUSED)) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                playbackState = PlaybackState.STOPPED;
            }
        }
    };

    private MediaPlayer mediaPlayer;

    /** Static array of your raw resources—no reflection needed */
    private static final int[] CLIP_RES_IDS = {
            R.raw.ajab_si,
            R.raw.dil_ibadat,
            R.raw.perfect,
            R.raw.zara_sa
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /** Play the clip corresponding to clipId (1-based index) */
    private void playClip(int clipId) {
        int index = clipId - 1;
        if (index < 0 || index >= CLIP_RES_IDS.length) return;

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (IllegalStateException ignored) { }

        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(this, CLIP_RES_IDS[index]);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }



    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null && playbackState != PlaybackState.STOPPED) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            playbackState = PlaybackState.STOPPED;
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (mediaPlayer != null && playbackState != PlaybackState.STOPPED) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            playbackState = PlaybackState.STOPPED;
        }
        stopSelf();
    }
}
