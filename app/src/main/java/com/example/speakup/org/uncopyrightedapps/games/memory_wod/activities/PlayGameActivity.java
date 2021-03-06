package com.example.speakup.org.uncopyrightedapps.games.memory_wod.activities;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.speakup.R;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.adapters.PieceAdapter;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.engine.GameEngine;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.engine.Graphic;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.media.MediaCenter;

public class PlayGameActivity extends AbstractGameActivity {
    private GameEngine mEngine;
    private MediaCenter mMediaCenter;
    private Graphic mGraphic;

    public static String ARG_GAME_ENGINE = "GAME_ENGINE";
    public static final String ARG_GRAPHICS = "GRAPHICS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        mEngine = (GameEngine) b.getSerializable(ARG_GAME_ENGINE);
        if (mEngine == null) {
            throw new IllegalArgumentException("Activity must receive a valid GameEngine");
        }
        mEngine.shuffle();

        mGraphic = (Graphic) b.getSerializable(ARG_GRAPHICS);
        if (mGraphic == null) {
            mGraphic = Graphic.ANIMALS;
        }

        setContentView(R.layout.activity_play_game);

        mMediaCenter = new MediaCenter(this);

        initButtons();

        mView = findViewById(R.id.gridview);
        getGridView().setNumColumns(mEngine.colCount());
        setAdapter();

        updateNumberOfTries();
    }

    public void updateNumberOfTries() {
        TextView mNumberOfTries = (TextView) findViewById(R.id.numberOfTries);
        String formattedValue = String.format("%s %s", getString(R.string.numberOfTries), String.valueOf(mEngine.getNumberOfTries()));
        mNumberOfTries.setText(formattedValue);
    }

    private void initButtons() {
        FloatingActionButton restartButton = (FloatingActionButton) findViewById(R.id.restartButton);
        final FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.soundButton);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEngine.reset();
                updateNumberOfTries();
                setAdapter();
            }
        });

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaCenter.soundIsOn()) {
                    mMediaCenter.pauseMusic();
                    mMediaCenter.turnSoundOff();
                    soundButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
                } else {
                    mMediaCenter.turnSoundOn();
                    mMediaCenter.startMusic();
                    soundButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }
            }
        });
    }

    private void setAdapter() {
        PieceAdapter adapter = new PieceAdapter(this, mEngine, mMediaCenter);
        getGridView().setAdapter(adapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        goFullScreen();
        mMediaCenter.startMusic();

    }

    @Override
    protected void onResume() {
        super.onResume();
        goFullScreen();
        mMediaCenter.startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaCenter.pauseMusic();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        goFullScreen();
        mMediaCenter.startMusic();
    }

    public GridView getGridView() {
        return (GridView) mView;
    }

    public Graphic getGraphic() {
        return mGraphic;
    }
}
