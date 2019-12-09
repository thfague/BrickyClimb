package iut.android.brickyclimb.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import iut.android.brickyclimb.R;
import iut.android.brickyclimb.activities.DetailScoreActivity;

public class ListScoresFragment extends ListFragment {
    public static final String PREFS_SCORE = "shared_prefs_score";
    public static final String PREFS_GAME = "shared_prefs_game";
    private boolean mListeDetail = false;
    private int mScoreSelectionneIndex = 0;
    private Map<String,Integer> mapScores;
    private Map<String,Integer> mapGame;
    public static Map<String, Integer[]> mapAffichage; // Nom => highScore,nbGame
    public static List<String> listUsername;
    private SharedPreferences mPreferencesScore;
    private SharedPreferences mPreferencesGame;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Si on a un état sauvegardé on met a jour l'élément par défaut
        if(savedInstanceState != null) {
            mScoreSelectionneIndex = savedInstanceState.getInt("scoreSelectionneIndex", 0);
        }

        mPreferencesScore = super.getContext().getSharedPreferences(PREFS_SCORE,Context.MODE_PRIVATE);
        mPreferencesGame = super.getContext().getSharedPreferences(PREFS_GAME,Context.MODE_PRIVATE);
        mapScores = (Map<String,Integer>) mPreferencesScore.getAll();
        mapGame = (Map<String,Integer>) mPreferencesGame.getAll();
        mapAffichage = new LinkedHashMap<>();

        Integer[] entries;
        String username1;
        String username2;

        for (Map.Entry<String, Integer> entry : mapScores.entrySet()) {
            username1 = entry.getKey();
            for (Map.Entry<String, Integer> entry2 : mapGame.entrySet()) {
                username2 = entry2.getKey();
                if(username1.equals(username2)) {
                    entries = new Integer[2];
                    entries[0] = entry.getValue();
                    entries[1] = entry2.getValue();
                    mapAffichage.put(username1,entries);
                    break;
                }
            }
        }
        mapAffichage.remove("score_game");
        listUsername = new ArrayList<>();
        for(Map.Entry<String, Integer[]> entry : mapAffichage.entrySet()) {
            listUsername.add(entry.getKey());
        }

        //En fonction du layout affiché on connait le mode d'ouverture
        View scoreDetailFragment = getActivity().findViewById(R.id.scoreDetail);
        mListeDetail = scoreDetailFragment != null && scoreDetailFragment.getVisibility() == View.VISIBLE;

        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, listUsername));
        if (mListeDetail) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            selectionScore(mScoreSelectionneIndex);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scoreSelectionneIndex", mScoreSelectionneIndex);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        selectionScore(position);
    }

    void selectionScore(int index) {
        mScoreSelectionneIndex = index;

        if (mListeDetail) {
            //Mise a jour de la liste
            getListView().setItemChecked(index, true);

            DetailScoreFragment detailJoueur = (DetailScoreFragment) getFragmentManager().findFragmentById(R.id.scoreDetail);

            if (detailJoueur == null || detailJoueur.getIdScoreEnCours() != index) {
                detailJoueur = DetailScoreFragment.newInstance(index);
                //On remplace le fragment existant avec le nouveau
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.scoreDetail, detailJoueur);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else {
            //En mode liste simple le detail s'affiche dans une nouvelle activité
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailScoreActivity.class);
            //on passe à l'actvité l'id du score sélectionné
            intent.putExtra("idScore", mScoreSelectionneIndex);
            startActivity(intent);
        }
    }
}
