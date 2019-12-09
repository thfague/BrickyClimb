package iut.android.brickyclimb.views;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import iut.android.brickyclimb.R;

import static iut.android.brickyclimb.views.ListScoresFragment.mapAffichage;

public class DetailScoreFragment extends Fragment {

    public static DetailScoreFragment newInstance(int index) {
        DetailScoreFragment f = new DetailScoreFragment();
        Bundle args = new Bundle();
        args.putInt("idScore", index);
        f.setArguments(args);
        return f;
    }

    public int getIdScoreEnCours() {
        assert getArguments() != null;
        return getArguments().getInt("idScore", 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) return null;

        ScrollView scroller = new ScrollView(getActivity());
        TextView statsTv = new TextView(getActivity());
        scroller.addView(statsTv);

        List<String> indexes = new ArrayList<>(mapAffichage.keySet());
        String user = null;
        for(String s : indexes) {
            if (indexes.indexOf(s) == getIdScoreEnCours()) {
                user = indexes.get(getIdScoreEnCours());
                break;
            }
        }
        Integer[] playerStats = mapAffichage.get(user);
        String highScoreText = playerStats[0].toString();
        String nbGameText = playerStats[1].toString();
        statsTv.setText(getString(R.string.highScore_text) + " " + highScoreText + "\n" + getString(R.string.nbGame_text) + " " + nbGameText);
        return scroller;
    }
}
