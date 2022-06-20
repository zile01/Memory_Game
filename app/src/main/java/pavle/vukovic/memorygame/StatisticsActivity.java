package pavle.vukovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private ListView lista;
    private final String DB_NAME = "GAMES";
    Button buttonRefresh;
    String get_all_games_url = "http://192.168.85.223:3000/score";
    HTTPHelper http_helper = new HTTPHelper();
    Adapter adapter;
    PlayerDBHelper helper = new PlayerDBHelper(this, DB_NAME, null, 1);
    String username;
    private IMyAidlInterface binder;
    boolean flag = false;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        adapter = new Adapter(this, username);

        lista = findViewById(R.id.lista);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Element e = (Element) adapter.getItem(i);

                String username_details = e.getmText1_1();

                Intent intent = new Intent(StatisticsActivity.this, DetailsActivity.class);
                intent.putExtra("username", username_details);
                startActivity(intent);
            }
        });

        adapter.deleteAll();

        List<Element> players = helper.readPlayers();

        if(players == null){
            Toast.makeText(getApplicationContext(), "Local Data Base is empty", Toast.LENGTH_SHORT).show();
        }else{
            for(Element player:players){
                adapter.addElement(player);
            }
        }

        buttonRefresh = findViewById(R.id.Refresh_button);
        buttonRefresh.setOnClickListener(this);

        Intent intent_service = new Intent(StatisticsActivity.this, MyService.class);
        if (bindService(intent_service, StatisticsActivity.this, BIND_AUTO_CREATE)) {
            Toast.makeText(getApplicationContext(), "Bind successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bind failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = IMyAidlInterface.Stub.asInterface(iBinder);

        try {
            binder.refresh();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){

                    try {
                        flag = binder.getFlag();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    if(flag){
                        try {
                            binder.setFlag();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Refresh adapter
                                adapter.deleteAll();

                                List<Element> players = helper.readPlayers();

                                for(Element player:players){
                                    adapter.addElement(player);
                                }

                                Toast.makeText(getApplicationContext(), "Successfull Refresh", Toast.LENGTH_SHORT).show();

                                //Notification
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CharSequence name = "name";
                                    String description = "description";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel("1", name, importance);
                                    channel.setDescription(description);
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                                        //TODO dodaj sliku, tj. logo koji ces da napravis
                                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                                        .setContentTitle("Refresh")
                                        .setContentText("Your data base has just been refreshed")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                notificationManager.notify(1, builder.build());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        binder = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.Refresh_button) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (binder.getRun() == false){
                            JSONArray json_array = new JSONArray();
                            String _id = "";
                            String username = "";
                            int score = 0;

                            //brisanje iz lokalne baze, kao i iz adaptera
                            List<Element> players1 = helper.readPlayers();

                            if(players1 == null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Local Data Base is empty", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                for(Element player:players1){
                                    helper.delete(player.getmText1_1());
                                }
                            }

                            try {
                                json_array = http_helper.getJSONArrayFromURL(get_all_games_url);
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
                                        _id = json_object.getString("_id");
                                        username = json_object.getString("username");
                                        score = json_object.getInt("score");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String email = username + "@gmail.com";

                                    helper.insert(_id, username, email, String.valueOf(score));
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.deleteAll();

                                    List<Element> players = helper.readPlayers();

                                    for(Element player:players){
                                        adapter.addElement(player);
                                    }

                                    Toast.makeText(getApplicationContext(), "Successfull Refresh", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Refresh has already started", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(StatisticsActivity.this);
    }
}