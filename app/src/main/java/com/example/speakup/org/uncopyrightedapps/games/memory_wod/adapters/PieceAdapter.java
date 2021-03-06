package com.example.speakup.org.uncopyrightedapps.games.memory_wod.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.speakup.R;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.activities.AddHighScoreActivity;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.activities.PlayGameActivity;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.engine.GameEngine;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.engine.Piece;
import com.example.speakup.org.uncopyrightedapps.games.memory_wod.media.MediaCenter;

public class PieceAdapter extends BaseAdapter {

    private final MediaCenter mMediaCenter;
    private PlayGameActivity mContext;
    private GameEngine mEngine;

    public PieceAdapter(PlayGameActivity context, GameEngine engine, MediaCenter mediaCenter) {
        this.mContext = context;
        this.mEngine = engine;
        this.mMediaCenter = mediaCenter;
    }

    @Override
    public int getCount() {
        return mEngine.piecesCount();
    }

    @Override
    public Object getItem(int position) {
        return mEngine.getPieces()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View pieceView;

        if (!mEngine.isFlipped(position)) {
            pieceView = inflater().inflate(R.layout.piece_view, null);
            TextView textView = (TextView) pieceView.findViewById(R.id.pieceText);
            textView.setHeight(mContext.getGridView().getColumnWidth());
            textView.setOnClickListener(new PieceOnClickListener(mEngine, position));
        } else {
            pieceView = inflater().inflate(R.layout.piece_view_flipped, null);
            ImageView imageView = (ImageView) pieceView.findViewById(R.id.pieceImage);
            imageView.setImageResource(getBackgroundDrawableFrom(position));
        }

        return pieceView;
    }

    private int getBackgroundDrawableFrom(int position) {
        Piece piece = (Piece) getItem(position);
        int pieceNumber = piece.getPieceNumber();
        return getDrawableIdentifier(pieceNumber);
    }

    private Integer getDrawableIdentifier(int pieceNumber) {
        String graphicSuffix = mContext.getGraphic().getFilePrefix();
        return mContext.getResources().getIdentifier(graphicSuffix + pieceNumber, "drawable", mContext.getPackageName());
    }

    private LayoutInflater inflater() {
        return (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class PieceOnClickListener implements View.OnClickListener {
        private GameEngine mEngine;
        private int mPosition;

        PieceOnClickListener(GameEngine engine, int position) {
            this.mEngine = engine;
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mEngine.numberOfPiecesFlippedIs(2)) {
                return;
            }

            mEngine.flip(mPosition);
            notifyDataSetChanged();

            if (mEngine.numberOfPiecesFlippedIs(2)) {
                if (mEngine.matchNotFound()) {
                    flipPiecesDown();
                } else {
                    if (mEngine.gameOver()) {
                        mMediaCenter.playGameOverSound();
                        startAddScoreActivity();
                        mContext.finish();
                    } else {
                        mEngine.clearFlippedPieces();
                    }
                }
                mContext.updateNumberOfTries();
            }
        }

        private void flipPiecesDown() {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEngine.resetFlippedPieces();
                    notifyDataSetChanged();
                }
            }, 1000);
        }

        private void startAddScoreActivity() {
            Intent intent = new Intent(mContext, AddHighScoreActivity.class);
            Bundle b = new Bundle();
            b.putInt(AddHighScoreActivity.ARG_SCORE, mEngine.getNumberOfTries());
            b.putSerializable(AddHighScoreActivity.ARG_GAME_TYPE, mEngine.getGameType());
            intent.putExtras(b);
            mContext.startActivity(intent);
        }
    }
}
