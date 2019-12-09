package iut.android.brickyclimb.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import iut.android.brickyclimb.R;
import iut.android.brickyclimb.services.MusicService;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "shared_prefs";
    private MediaPlayer clickSound;
    private SharedPreferences sharedPref;
    private String effectsChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE); //Récupère les préférences de l'utilisateur
        clickSound = MediaPlayer.create(this,R.raw.click_sound1);
    }

    @Override
    protected void onResume() {
        effectsChoice = sharedPref.getString(getString(R.string.effects_switch), "true");
        startService(new Intent(this, MusicService.class));
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this,MusicService.class));
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,MusicService.class));
        super.onDestroy();
    }

    public void makeSound() {
        if(effectsChoice.equals("true")) clickSound.start();
    }

    public void playButtonClicked(View view) {
        startActivity(new Intent(this, GameActivity.class));
        makeSound();
    }

    public void settingsButtonClicked(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
        makeSound();
    }

    public void scoresButtonClicked(View view) {
        startActivity(new Intent(this, ScoresActivity.class));
        makeSound();
    }

    public void exitButtonClicked(View view) {
        makeSound();
        stopService(new Intent(this,MusicService.class));
        finish();
        System.exit(0);
    }
}
