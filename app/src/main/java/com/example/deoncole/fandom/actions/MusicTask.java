package com.example.deoncole.fandom.actions;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.deoncole.fandom.model.ArtistSongs;
import com.example.deoncole.fandom.ui.fragment.MusicFragment;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MusicTask extends AsyncTask<String, Void, ArrayList<ArtistSongs>> {

    private Context context;

    public MusicTask(Context context){
        this.context = context;
    }

    @Override
    protected ArrayList<ArtistSongs> doInBackground(String... params) {
        return getSongList(params[0]);
    }

    @Override
    protected void onPostExecute(ArrayList <ArtistSongs> arrayList) {
        super.onPostExecute(arrayList);

        ArrayList<String> titles = new ArrayList<>();
        for(int i = 0; i<arrayList.size(); i++){

            String songName = arrayList.get(i).getSongTitle();
            titles.add(songName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R
                .layout.simple_list_item_1, titles);

        MusicFragment.musicLv.setAdapter(adapter);

    }

    private ArrayList<ArtistSongs> getSongList(String urlString){

        MusicFragment.previewList = new ArrayList<>();

        try {
            URL url = new URL(urlString);
            //Create a Https connection and open it. Connect the connection and connect the
            // input stream.
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            String data = IOUtils.toString(inputStream);

            //Close the input stream & disconnect the connection
            inputStream.close();
            connection.disconnect();

            JSONObject topObject = new JSONObject(data);
            JSONArray dataArray = topObject.getJSONArray("data");

            //Use a loop to iterate through the JSON array. Get each object out of the array
            for(int i = 0; i<dataArray.length(); i++){
                JSONObject childObject = dataArray.getJSONObject(i);

                String songTitle = childObject.getString("title");
                String songLink = childObject.getString("preview");

                System.out.println("THE SONG LINK IS: " + songLink);

                ArtistSongs songs = new ArtistSongs(songTitle, songLink);
                MusicFragment.previewList.add(songs);

            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return MusicFragment.previewList;

    }

}

