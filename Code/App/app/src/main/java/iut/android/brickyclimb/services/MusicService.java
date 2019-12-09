package iut.android.brickyclimb.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

import iut.android.brickyclimb.R;

public class MusicService extends Service {
    private MediaPlayer music;
    private SharedPreferences sharedPrefs;
    public static final String PREFS_NAME = "shared_prefs";

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPrefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String musicChoice = sharedPrefs.getString(getString(R.string.music_switch), "true");
        assert musicChoice != null;
        if (musicChoice.equals("true")) {
            if (music != null) return START_STICKY;
            music = MediaPlayer.create(this, R.raw.brick_breaker);
            music.setLooping(true);
            music.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(music == null) return;
        if(music.isPlaying()) {
            music.stop();
        }
    }
}
