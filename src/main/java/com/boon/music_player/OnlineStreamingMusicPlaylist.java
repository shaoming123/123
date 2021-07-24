package com.boon.music_player;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlineStreamingMusicPlaylist extends Fragment {

    View view;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    EditText editText;
    ArrayList<HashMap<String, String>> musicList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_online_streaming_music_playlist, container, false);
        musicList = new ArrayList<>();
        listView = view.findViewById(R.id.onlineStreamingMusicPlaylist);
        editText = view.findViewById(R.id.searchField);
        editText.setOnEditorActionListener(editorListener);
        return view;
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                new GetContacts().execute();
            }
            return false;
        }
    };


    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Music Album is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            musicList.clear();

            EditText searchStr = view.findViewById(R.id.searchField);
            String qString = searchStr.getText().toString();
            HttpHandler sessionHandler = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.deezer.com/search/track?q="+qString;
            String jsonStr = sessionHandler.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray jsonArray = jsonObj.getJSONArray("data");

                    // looping through All Contacts
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String title = c.getString("title");
                        String preview = c.getString("preview");

                        JSONObject album = c.getJSONObject("album");
                        String image = album.getString("cover_small");

                        JSONObject artist = c.getJSONObject("artist");
                        String name = artist.getString("name");

                        // tmp hash map for single contact
                        HashMap<String, String> track = new HashMap<>();

                        // adding each child node to HashMap key => value
                        track.put("title", title);
                        track.put("image", image);
                        track.put("preview", preview);
                        track.put("name", name);

                        // adding contact to contact list
                        musicList.add(track);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Music and albums parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get music and albums from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get Music and albums from server. Please connect to the Internet.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    HashMap<String, String> hashMap = musicList.get(position);
                    int sessionToken = 2;

                    startActivity(new Intent(getActivity().getApplicationContext(), Player_UI.class)
                            .putExtra("position", position)
                            .putExtra("songlist", musicList)
                            .putExtra("sessionToken", sessionToken));
                }
            });
        }


    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return musicList.size();
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
            //Setting up the song name in the list view
            View myView = getLayoutInflater().inflate(R.layout.list_songmenu, null);

            HashMap<String, String> hashMap = musicList.get(i);
            String title = hashMap.get("title");
            String name = hashMap.get("name");
            String image = hashMap.get("image");

            TextView titleView= myView.findViewById(R.id.title);
            titleView.setSelected(true);
            titleView.setText(title);

            TextView nameView= myView.findViewById(R.id.name);
            nameView.setText(name);

            ImageView imageView = myView.findViewById(R.id.image);
            Picasso.get().load(image).into(imageView);

            return myView;
        }
    }

}