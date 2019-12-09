package iut.android.brickyclimb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import iut.android.brickyclimb.R;
import iut.android.brickyclimb.services.MusicService;

public class ScoresActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MusicService.class));
    }
}
