package pavle.vukovic.memorygame;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class Binder extends IMyAidlInterface.Stub{
    private final String DB_NAME = "GAMES";
    public boolean run;
    private Adapter adapter;
    String get_all_games_url = "http://192.168.85.223:3000/score";
    private boolean flag = false;
    HTTPHelper http_helper;
    PlayerDBHelper helper;
    Handler handler = new Handler();

    public Binder(PlayerDBHelper helper, HTTPHelper http_helper) {
        this.helper = helper;
        this.http_helper = http_helper;
        this.run = false;
    }

    @Override
    public void refresh() throws RemoteException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    run = false;
                    JSONArray json_array = new JSONArray();
                    String _id = "";
                    String username = "";
                    int score = 0;

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    run = true;

                    //brisanje iz lokalne baze
                    List<Element> players1 = helper.readPlayers();

                    if(players1 == null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("TAG", "Local Data Base is empty");
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

                    if (json_array == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("TAG", "Getting Error");
                            }
                        });

                        flag = false;
                    } else {
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

                        flag = true;
                    }
                }
            }
        }).start();
    }

    public boolean getFlag(){
        return this.flag;
    }

    public void setFlag(){
        this.flag = false;
    }

    public boolean getRun(){
        return this.run;
    }

    public void setRun(boolean bool){
        this.run = bool;
    }

    public void stop() {
        run = false;
    }
}
