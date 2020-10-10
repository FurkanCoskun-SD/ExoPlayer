package com.furkancoskun.exoplayer.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.furkancoskun.exoplayer.R;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView tvName;
    ConstraintLayout constraintLayout;
    OnMovieListener mOnMovieListener;

    public MovieViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
        super(itemView);

        this.mOnMovieListener = onMovieListener;

        tvName = itemView.findViewById(R.id.tv_name);
        constraintLayout = itemView.findViewById(R.id.layout);
        constraintLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout) {
            mOnMovieListener.onItemClickListener(getAdapterPosition());
        }
    }
}
