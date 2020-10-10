package com.furkancoskun.exoplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkancoskun.exoplayer.R;
import com.furkancoskun.exoplayer.model.Movie;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Movie> mMovies;
    LayoutInflater inflater;
    OnMovieListener mOnMovieListener;

    public MoviesAdapter(Context context, ArrayList<Movie> moviesItems, OnMovieListener onMovieListener) {
        inflater = LayoutInflater.from(context);
        this.mOnMovieListener = onMovieListener;
        this.mMovies = moviesItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie, viewGroup, false);
        return new MovieViewHolder(view, mOnMovieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (mMovies != null) {
            ((MovieViewHolder) viewHolder).tvName.setText(mMovies.get(i).getName());
        }
    }

    @Override
    public int getItemCount() {
        if (mMovies != null) {
            return mMovies.size();
        } else {
            return 0;
        }
    }

    public Movie getSelectedMovieItem(int position) {
        if (mMovies != null) {
            return mMovies.get(position);
        }
        return null;
    }
}
