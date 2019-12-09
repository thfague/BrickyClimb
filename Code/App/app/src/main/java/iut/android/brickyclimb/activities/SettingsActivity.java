package iut.android.brickyclimb.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import iut.android.brickyclimb.R;
import iut.android.brickyclimb.services.MusicService;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "shared_prefs";
    public static final String PREFS_SCORE = "shared_prefs_score";
    public static final String PREFS_GAME = "shared_prefs_game";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferencesScore;
    private SharedPreferences.Editor mEditorScore;
    private SharedPreferences mPreferencesGame;
    private SharedPreferences.Editor mEditorGame;

    private Switch mMusicChoice;
    private Switch mEffectsChoice;
    private RadioButton mTouchControls;
    private RadioButton mRotationControls;
    private Button mResetScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final MediaPlayer clickSound = MediaPlayer.create(this,R.raw.click_sound1);
        mMusicChoice = findViewById(R.id.switchMusic);
        mEffectsChoice = findViewById(R.id.switchEffects);
        mTouchControls = findViewById(R.id.radioButtonTouch);
        mRotationControls = findViewById(R.id.radioButtonRotation);
        mResetScores = findViewById(R.id.buttonRemoveScores);
        FloatingActionButton confirmButton = findViewById(R.id.confirmButton);
        mPreferencesScore = getSharedPreferences(PREFS_SCORE, Context.MODE_PRIVATE);
        mEditorScore = mPreferencesScore.edit();
        mPreferencesGame = getSharedPreferences(PREFS_GAME, Context.MODE_PRIVATE);
        mEditorGame = mPreferencesGame.edit();
        mPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        mEditor = mPreferences.edit();
        checkSharedPreferences();

        mResetScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditorScore.clear();
                mEditorScore.commit();
                mEditorGame.clear();
                mEditorGame.commit();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMusicChoice.isChecked()){
                    mEditor.putString(getString(R.string.music_switch),"false");
                    mEditor.apply();
                }
                else {
                    mEditor.putString(getString(R.string.music_switch),"true");
                    mEditor.commit();
                }
                if(!mEffectsChoice.isChecked()){
                    mEditor.putString(getString(R.string.effects_switch),"false");
                    mEditor.apply();
                }
                else {
                    clickSound.start();
                    mEditor.putString(getString(R.string.effects_switch),"true");
                    mEditor.commit();
                }
                if(!mRotationControls.isChecked()){
                    mEditor.putString(getString(R.string.controls_rotation),"false");
                    mEditor.apply();
                }
                else {
                    mEditor.putString(getString(R.string.controls_rotation),"true");
                    mEditor.commit();
                }
                if(!mTouchControls.isChecked()){
                    mEditor.putString(getString(R.string.controls_touch),"false");
                    mEditor.apply();
                }
                else {
                    mEditor.putString(getString(R.string.controls_touch),"true");
                    mEditor.commit();
                }
                finish();
            }
        });
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

    private void checkSharedPreferences() {
        String musicChoice = mPreferences.getString(getString(R.string.music_switch), "true");
        String effectsChoice = mPreferences.getString(getString(R.string.effects_switch), "true");
        String rotationControls = mPreferences.getString(getString(R.string.controls_rotation), "true");
        String touchControls = mPreferences.getString(getString(R.string.controls_touch), "false");
        assert musicChoice != null;
        if(musicChoice.equals("false")) {
            mMusicChoice.setChecked(false);
        }
        else {
            mMusicChoice.setChecked(true);
        }
        assert effectsChoice != null;
        if(effectsChoice.equals("false")) {
            mEffectsChoice.setChecked(false);
        }
        else {
            mEffectsChoice.setChecked(true);
        }
        assert rotationControls != null;
        if(rotationControls.equals("false")) {
            mRotationControls.setChecked(false);
        }
        else {
            mRotationControls.setChecked(true);
        }
        assert touchControls != null;
        if(touchControls.equals("false")) {
            mTouchControls.setChecked(false);
        }
        else {
            mTouchControls.setChecked(true);
        }
    }
}
