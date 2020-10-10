package com.furkancoskun.exoplayer.ui;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.furkancoskun.exoplayer.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.Objects;

import static com.furkancoskun.exoplayer.VideoQuality.HIGH_BITRATE;
import static com.furkancoskun.exoplayer.VideoQuality.LOW_BITRATE;
import static com.furkancoskun.exoplayer.VideoQuality.MID_BITRATE;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variable
    PlayerView playerView;
    ProgressBar progressBar;
    ImageView btnFullScreen, btnRew, btnForward, btnPlay, btnPause, btnSettings;
    SimpleExoPlayer exoPlayer;
    Boolean flagFullScreen = false;
    DefaultTrackSelector trackSelector;
    Uri videoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialize();

        // Get intent (Movies activity)
        Bundle bundle = getIntent().getExtras();
        videoURL = Uri.parse(Objects.requireNonNull(bundle).getString("url"));

        buildExoPlayer(videoURL);
        exoPlayerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop video
        playerPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Play video when ready
        playerStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rew: {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
                break;
            }
            case R.id.btn_pause: {
                // Stop video
                exoPlayer.setPlayWhenReady(false);
                // Get playback state
                exoPlayer.getPlaybackState();

                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.btn_play: {
                // Play video when ready
                exoPlayer.setPlayWhenReady(true);
                // Get playback state
                exoPlayer.getPlaybackState();

                btnPause.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_forward: {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
                break;
            }
            case R.id.btn_settings: {
                showSettingsDialogButtonClicked();

                break;
            }
            case R.id.btn_fullscreen: {
                // Check condition
                if (flagFullScreen) {
                    screenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    screenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            }
        }
    }

    private void initialize() {
        // Assign variable
        playerView = findViewById(R.id.player_view);
        btnRew = findViewById(R.id.btn_rew);
        btnPause = findViewById(R.id.btn_pause);
        btnPlay = findViewById(R.id.btn_play);
        btnForward = findViewById(R.id.btn_forward);
        progressBar = findViewById(R.id.progress_bar);
        btnFullScreen = findViewById(R.id.btn_fullscreen);
        btnSettings = findViewById(R.id.btn_settings);

        btnRew.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnFullScreen.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
    }

    private void buildExoPlayer(Uri videoURL) {
        // Init load control
        LoadControl loadControl = new DefaultLoadControl();
        // Init band width meter
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Init track selector
        trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        // Init simple exo player
        exoPlayer = ExoPlayerFactory.newSimpleInstance(PlayerActivity.this, trackSelector, loadControl);
        // Init media source
        MediaSource mediaSource = buildMediaSource(videoURL);

        // Set player
        playerView.setPlayer(exoPlayer);
        // Keep screen on
        playerView.setKeepScreenOn(true);
        // Prepare media
        exoPlayer.prepare(mediaSource);

        // Play video when ready
        exoPlayer.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri videoUrl) {
        String userAgent = "exoplayer_video";

        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        if (videoUrl.getLastPathSegment().contains("mp3") || videoUrl.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(videoUrl);
        } else if (videoUrl.getLastPathSegment().contains("m3u8")) {
            // Hls
            return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(videoUrl);
        } else {
            // Dash
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ua", new DefaultBandwidthMeter());
            DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(dataSourceFactory);

            return new DashMediaSource.Factory(dashChunkSourceFactory, dataSourceFactory).createMediaSource(videoUrl);
        }
    }

    private void exoPlayerListener() {
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // Check condition
                if (playbackState == Player.STATE_BUFFERING) {
                    // When buffering
                    // Show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    // When buffering
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_IDLE) {
                    Log.d("TAG", "onPlayerStateChanged - STATE_IDLE");
                } else if (playbackState == Player.STATE_ENDED) {
                    Log.d("TAG", "onPlayerStateChanged - STATE_ENDED");
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                switch (error.type) {
                    case ExoPlaybackException.TYPE_RENDERER:
                        // TODO: Do something!
                        break;
                    case ExoPlaybackException.TYPE_SOURCE:
                        // TODO: Do something!
                        break;
                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        // TODO: Do something!
                        break;
                    default:
                        // TODO: Do something!
                        break;
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    // Change Screen orientation
    private void screenOrientation(int orientation) {
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            // Set enter full screen image
            btnFullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen));
            // Set portrait orientation
            setRequestedOrientation(orientation);
            // Set flag value is false
            flagFullScreen = false;
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            // Set enter full screen image
            btnFullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit));
            // Set portrait orientation
            setRequestedOrientation(orientation);
            // Set flag value is false
            flagFullScreen = true;
        }
    }

    // Stop video
    private void playerPause() {
        // Stop video
        exoPlayer.setPlayWhenReady(false);
        // Get playback state
        exoPlayer.getPlaybackState();
    }

    // Play video
    private void playerStart() {
        // Play video when ready
        exoPlayer.setPlayWhenReady(true);
        // Get playback state
        exoPlayer.getPlaybackState();
    }

    // Setting dialog
    public void showSettingsDialogButtonClicked() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.VideoQuality);
        // add a list
        String[] animals = {getString(R.string.Low), getString(R.string.Mid), getString(R.string.High)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters().buildUpon()
                                .setMaxVideoBitrate(LOW_BITRATE)
                                .build();
                        trackSelector.setParameters(parameters);
                        break;
                    }
                    case 1: {
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters().buildUpon()
                                .setMaxVideoBitrate(MID_BITRATE)
                                .build();
                        trackSelector.setParameters(parameters);
                        break;
                    }
                    case 2: {
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters().buildUpon()
                                .setMaxVideoBitrate(HIGH_BITRATE)
                                .build();
                        trackSelector.setParameters(parameters);
                        break;
                    }
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}