package iut.android.brickyclimb.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import iut.android.brickyclimb.R;
import iut.android.brickyclimb.services.MusicService;

public class SaveScoreActivity extends AppCompatActivity {
    public static final String PREFS_SCORE = "shared_prefs_score";
    public static final String PREFS_GAME = "shared_prefs_game";
    public static final String PREFS_USER = "shared_prefs_user";
    private SharedPreferences mPreferencesScore;
    private SharedPreferences.Editor mEditorScore;
    private SharedPreferences mPreferencesGame;
    private SharedPreferences.Editor mEditorGame;
    private SharedPreferences mPreferencesUser;
    private SharedPreferences.Editor mEditorUser;

    private int score;
    private int highScore;
    private int nbGame;
    private String username;

    private TextView tvScore;
    private TextView tvError;
    private EditText etUsername;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_score);

        startService(new Intent(this, MusicService.class));

        buttonSave = (Button) findViewById(R.id.saveButton);
        tvScore = (TextView) findViewById(R.id.showScore);
        etUsername = (EditText) findViewById(R.id.usernameEt);
        tvError = (TextView) findViewById(R.id.errorTv);

        mPreferencesScore = getSharedPreferences(PREFS_SCORE, Context.MODE_PRIVATE);
        mEditorScore = mPreferencesScore.edit();
        mPreferencesGame = getSharedPreferences(PREFS_GAME, Context.MODE_PRIVATE);
        mEditorGame = mPreferencesGame.edit();
        mPreferencesUser = getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
        mEditorUser = mPreferencesUser.edit();

        etUsername.setText(mPreferencesUser.getString("last", ""));
        score = mPreferencesScore.getInt("score_game",-1);

        String text = getString(R.string.showScore_text)+": "+Integer.toString(score);
        tvScore.setText(text);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                if (username.equals(""))
                    tvError.setText(getString(R.string.error_text));
                else {
                    tvError.setText("");
                    highScore = mPreferencesScore.getInt(username,-1);
                    if(highScore == -1 || score > highScore) {
                        mEditorScore.putInt(username, score);
                        mEditorScore.apply();
                    }
                    nbGame = mPreferencesGame.getInt(username,-1);
                    if(nbGame == -1) {
                        mEditorGame.putInt(username,1);
                        mEditorGame.apply();
                    }
                    else {
                        mEditorGame.putInt(username,nbGame+1);
                        mEditorGame.apply();
                    }
                    mEditorUser.putString("last", username);
                    mEditorUser.apply();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
    }
}
