package iut.android.brickyclimb.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import iut.android.brickyclimb.views.DetailScoreFragment;

public class DetailScoreActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            DetailScoreFragment detailScore = DetailScoreFragment.newInstance(getIntent().getIntExtra("idScore",0));
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, detailScore).commit();
        }
    }
}
