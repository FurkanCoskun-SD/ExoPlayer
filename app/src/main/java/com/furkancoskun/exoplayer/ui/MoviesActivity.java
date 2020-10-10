package com.furkancoskun.exoplayer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.furkancoskun.exoplayer.R;
import com.furkancoskun.exoplayer.model.Movie;
import com.furkancoskun.exoplayer.ui.adapter.MoviesAdapter;
import com.furkancoskun.exoplayer.ui.adapter.OnMovieListener;

import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements OnMovieListener {

    // Init variable
    MoviesAdapter moviesAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        initialize();

        // Movie list
        ArrayList<Movie> movies = new ArrayList<>();
        movies.add(0,new Movie("mp4","https://www.radiantmediaplayer.com/media/big-buck-bunny-360p.mp4"));
        movies.add(0,new Movie("m3u8","https://bitmovin-a.akamaihd.net/content/playhouse-vr/m3u8s/105560.m3u8"));
        movies.add(0,new Movie("mpd","https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"));

        moviesAdapter = new MoviesAdapter(getApplicationContext(), movies, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(moviesAdapter);
    }

    private void initialize() {
        recyclerView = findViewById(R.id.rv_movies);
    }

    @Override
    public void onItemClickListener(int position) {
        // Selected movie
        Movie movie = moviesAdapter.getSelectedMovieItem(position);

        // Intent player activity
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("name", movie.getName());
        intent.putExtra("url", movie.getUrl());
        startActivity(intent);
    }
}