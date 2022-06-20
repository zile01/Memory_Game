package pavle.vukovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    String username;
    ArrayAdapter<String> Adaptercic;
    List<String> results;
    ListView lista;
    TextView details_username2;
    String get_all_games_for_user_url;
    HTTPHelper http_helper = new HTTPHelper();
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        get_all_games_for_user_url = "http://192.168.85.223:3000/score/?username=" + username;
        Adaptercic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        results = new ArrayList<>();

        details_username2 = findViewById(R.id.details_username2);
        lista = findViewById(R.id.lista);

        details_username2.setText(username);

        lista.setAdapter(Adaptercic);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray json_array = new JSONArray();

                try {
                    json_array = http_helper.getJSONArrayFromURL(get_all_games_for_user_url);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(json_array == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Getting Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    for (int i = 0; i < json_array.length(); i++) {
                        JSONObject json_object = new JSONObject();

                        try {
                            json_object = json_array.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            score = 0;
                            score = json_object.getInt("score");
                            results.add(String.valueOf(score));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(String point : results){
                            Adaptercic.add(point);
                        }
                    }
                });
            }
        }).start();
    }
}