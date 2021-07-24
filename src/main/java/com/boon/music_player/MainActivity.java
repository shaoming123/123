package com.boon.music_player;

import androidx.appcompat.app.AppCompatActivity;;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;
    FragmentManager fragmentManager = getSupportFragmentManager();
    Button playButton, pauseButton, clearButton;
    ImageView albumArt;
    TextView songTitle;
    MaterialCardView relativeLayout;
    Activity activity;
    int position, sessionToken;
    static MediaPlayer mediaPlayer;
    String songName, imageURL;
    ArrayList<String> SongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_page);
        activity = this;


        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Local Playlist"));
        tabLayout.addTab(tabLayout.newTab().setText("Streaming Music"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        if (mediaPlayer != null){
            setMediaControllerBar();
        }


    }


    public void  setMediaControllerBar(){

        relativeLayout = activity.findViewById(R.id.playlist_bottom_now_playing);
        if(relativeLayout.getVisibility() != View.VISIBLE)
        {
            relativeLayout.setVisibility(View.VISIBLE);

        }
        albumArt = activity.findViewById(R.id.bottom_now_playing_album_art);
        songTitle = activity.findViewById(R.id.bottom_now_playing_song_name);
        playButton = activity.findViewById(R.id.bottom_now_playing_song_play);
        pauseButton = activity.findViewById(R.id.bottom_now_playing_song_pause);
        clearButton = activity.findViewById(R.id.bottom_now_playing_song_clear);

        songTitle.setSingleLine();
        songTitle.setSelected(true);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        sessionToken = bundle.getInt("sessionToken");

        if (sessionToken == 1){
            songName = bundle.getString("song_title");
            songTitle.setText(songName);
        }else{
            songName = bundle.getString("song_title");
            imageURL = bundle.getString("imageURL");
            songTitle.setText(songName);
            Picasso.get().load(imageURL).into(albumArt);
        }


        if(mediaPlayer.isPlaying())
        {
            pauseButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);
        }else{
            pauseButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                mediaPlayer.start();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                mediaPlayer.pause();
            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer=null;
                position = -1;
                relativeLayout.setVisibility(View.GONE);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songName = SongList.get(position);
                position = bundle.getInt("position");
                if(sessionToken==1) {
                    Player_UI.mediaPlayer = mediaPlayer;
                    startActivity(new Intent(MainActivity.this, Player_UI.class)
                            .putExtra("position", position)
                            .putExtra("songlist", SongList)
                            .putExtra("sessionToken", sessionToken)
//                            .putExtra("noInterrupt", true)
                    );

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pauseButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
            }
        });

    }
}
