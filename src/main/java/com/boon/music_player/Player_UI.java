package com.boon.music_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Player_UI extends AppCompatActivity {
    Button playbutton, nextbutton, previousbutton, fastfowardbutton, fastrewindbutton;
    TextView songname, startTime, stopTime, playerArtistName;
    SeekBar seekBar;
    BarVisualizer visualizer;
    ImageView PlayerImageView, clearButton;

    public static MediaPlayer mediaPlayer;
    int position, resID, sessionToken;
    ArrayList<String> SongList;
    ArrayList<HashMap<String, String>> musicList;
    Thread updateSeekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (visualizer != null) {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_ui);

        getSupportActionBar().hide();

        playbutton = findViewById(R.id.playbutton);
        nextbutton = findViewById(R.id.nextbutton);
        previousbutton = findViewById(R.id.previoustbutton);
        fastfowardbutton = findViewById(R.id.fastforwardbutton);
        fastrewindbutton = findViewById(R.id.fastrewindbutton);
        songname = findViewById(R.id.PlayerSongName);
        startTime = findViewById(R.id.Starttime);
        stopTime = findViewById(R.id.Stoptime);
        seekBar = findViewById(R.id.PlayerSeekbar);
        visualizer = findViewById(R.id.blast);
        PlayerImageView = findViewById(R.id.PlayerImage);
        playerArtistName = findViewById(R.id.PlayerArtistName);
        clearButton = findViewById(R.id.btn_clear);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        int id = bundle.getInt("id", -1);
        if (id == -1) {

            sessionToken = bundle.getInt("sessionToken");

            if (sessionToken == 1) {
                SongList = (ArrayList) bundle.getParcelableArrayList("songlist");
                position = bundle.getInt("position", 0);
                String songName = SongList.get(position);
                resID = getResources().getIdentifier(SongList.get(position), "raw", getPackageName());
                songname.setSelected(true);
                songname.setText(songName);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                mediaPlayer.start();
                startAnimation(PlayerImageView);
            } else {
                musicList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("songlist");
                position = bundle.getInt("position", 0);
                HashMap<String, String> hashMap = musicList.get(position);
                String songURL = hashMap.get("preview");
                String imageURL = hashMap.get("image");
                String artistName = hashMap.get("name");
                String songName = hashMap.get("title");
                songname.setSelected(true);
                songname.setText(songName);
                playerArtistName.setSelected(true);
                playerArtistName.setText(artistName);
                Picasso.get().load(imageURL).into(PlayerImageView);
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(songURL);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextbutton.performClick();
            }
        });

        updateSeekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        stopTime.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                startTime.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    playbutton.setBackgroundResource(R.drawable.ic_playbutton);
                    mediaPlayer.pause();
                } else {
                    playbutton.setBackgroundResource(R.drawable.ic_pausebutton);
                    mediaPlayer.start();
                }
            }
        });

        int audiosessionID = mediaPlayer.getAudioSessionId();
        if (audiosessionID != 1) {
            visualizer.setAudioSessionId(audiosessionID);
        }

        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (sessionToken == 1) {
                    position = ((position + 1) % SongList.size());
                    resID = getResources().getIdentifier(SongList.get(position), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                    String songName = SongList.get(position);
                    songname.setText(songName);
                    mediaPlayer.start();
                    playbutton.setBackgroundResource(R.drawable.ic_pausebutton);
                    startAnimation(PlayerImageView);
                } else {
                    position = ((position + 1) % musicList.size());
                    HashMap<String, String> hashMap = musicList.get(position);
                    String songURL = hashMap.get("preview");
                    String imageURL = hashMap.get("image");
                    String artistName = hashMap.get("name");
                    String songName = hashMap.get("title");
                    songname.setSelected(true);
                    songname.setText(songName);
                    playerArtistName.setSelected(true);
                    playerArtistName.setText(artistName);
                    Picasso.get().load(imageURL).into(PlayerImageView);
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(songURL);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateData();
            }
        });

        previousbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (sessionToken == 1) {
                    position = ((position - 1) < 0) ? (SongList.size() - 1) : (position - 1);
                    resID = getResources().getIdentifier(SongList.get(position), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                    String songName = SongList.get(position);
                    songname.setText(songName);
                    mediaPlayer.start();
                    playbutton.setBackgroundResource(R.drawable.ic_pausebutton);
                    startAnimation(PlayerImageView);
                } else {
                    position = ((position - 1) < 0) ? (musicList.size() - 1) : (position - 1);
                    HashMap<String, String> hashMap = musicList.get(position);
                    String songURL = hashMap.get("preview");
                    String imageURL = hashMap.get("image");
                    String artistName = hashMap.get("name");
                    String songName = hashMap.get("title");
                    songname.setSelected(true);
                    songname.setText(songName);
                    playerArtistName.setSelected(true);
                    playerArtistName.setText(artistName);
                    Picasso.get().load(imageURL).into(PlayerImageView);
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(songURL);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateData();
            }
        });

        fastfowardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        fastrewindbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void startAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(PlayerImageView, "rotation", 0f, 360f);
        animator.setDuration(20000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration) {
        String time = "";
        int minute = duration / 1000 / 60;
        int second = duration / 1000 % 60;

        time += minute + ":";

        if (second < 10) {
            time += "0";
        }
        time += second;

        return time;
    }

    public void onBackPressed() {
        moveTaskToBack(true);


        MainActivity mainActivity = new MainActivity();
        mainActivity.mediaPlayer = mediaPlayer;
        String songName;
        if (sessionToken == 1) {
            songName = SongList.get(position);
            startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("song_title", songName)
                    .putExtra("sessionToken", sessionToken));
        } else {
            songName = musicList.get(position).get("title");
            String imageURL = musicList.get(position).get("image");
            startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("song_title", songName)
                    .putExtra("imageURL", imageURL)
                    .putExtra("sessionToken", sessionToken));
        }

    }

    void updateData() {
        String endTime = createTime(mediaPlayer.getDuration());
        stopTime.setText(endTime);
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextbutton.performClick();
            }
        });
        int audiosessionID = mediaPlayer.getAudioSessionId();
        if (audiosessionID != 1) {
            visualizer.setAudioSessionId(audiosessionID);
        }
    }

}
