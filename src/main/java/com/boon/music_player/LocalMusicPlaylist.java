package com.boon.music_player;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class LocalMusicPlaylist extends Fragment {

    View view;
    ListView listView;
    String[] items;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_local_music_playlist, container, false);
        listView = view.findViewById(R.id.LocalMusicPlaylist);

        runtimePermission();
        return view;
    }

    public void runtimePermission(){
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySong();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                // If user disagree the permission, then ask the permission again
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    void displaySong(){
        final ArrayList<String> SongList = new ArrayList<String>();
        final Field[] fields = R.raw.class.getFields();
        //A loop for importing all the .mp3 file in the raw folder
        for (int i = 0; i<fields.length; i++){
            SongList.add(fields[i].getName());
        }
        items = new String[SongList.size()];
        //Importing the name in the array to items for the list view menu display
        for (int i = 0; i < SongList.size(); i++) {
            items[i] = SongList.get(i);
        }
        LocalMusicPlaylist.customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        //Set up the list view onclick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int sessionToken = 1;
                startActivity(new Intent(getActivity().getApplicationContext(), Player_UI.class)
                        .putExtra("position", i)
                        .putExtra("songlist", SongList)
                        .putExtra("sessionToken", sessionToken));
            }
        });
    }

    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.list_songmenu, null);
            TextView Songname = myView.findViewById(R.id.title);
            Songname.setSelected(true);
            Songname.setText(items[i]);
            return myView;
        }
    }
}