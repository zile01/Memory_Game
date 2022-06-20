package pavle.vukovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    int[][] button_ids = {{R.id.button1_1, R.id.button1_2, R.id.button1_3, R.id.button1_4},
            {R.id.button2_1, R.id.button2_2, R.id.button2_3, R.id.button2_4},
            {R.id.button3_1, R.id.button3_2, R.id.button3_3, R.id.button3_4},
            {R.id.button4_1, R.id.button4_2, R.id.button4_3, R.id.button4_4}
    };

    int[][] image_ids = {{R.id.slika1_1, R.id.slika1_2, R.id.slika1_3, R.id.slika1_4},
            {R.id.slika2_1, R.id.slika2_2, R.id.slika2_3, R.id.slika2_4},
            {R.id.slika3_1, R.id.slika3_2, R.id.slika3_3, R.id.slika3_4},
            {R.id.slika4_1, R.id.slika4_2, R.id.slika4_3, R.id.slika4_4}
    };

    Button[][] button = new Button[4][4];
    Button buttonStart, buttonStatistics;
    ImageView[][] slika = new ImageView[4][4];

    Bitmap bitmap1;
    Bitmap bitmap2;

    Handler handler = new Handler();

    String save_game_url = "http://192.168.85.223:3000/score";
    HTTPHelper http_helper = new HTTPHelper();

    int[][] matrica_identifikacije_klika = new int[4][4];
    int brojac_global = 0;
    int brojac_start = 0;
    int brojac_slika = 0;
    int poeni = 0;
    int end = 0;
    int id = 0;

    int[][] matrica_identifikacije_slika = new int[4][4];

    String username, email;
    private final String DB_NAME = "GAMES";
    private PlayerDBHelper helper;

    JNIexample jni = new JNIexample();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = username + "@gmail.com";

        helper = new PlayerDBHelper(this, DB_NAME, null, 1);

        this.findViews();
        this.setListeners();
        this.disableButtons();
    }

    @Override
    public void onClick(View view) {
        //Start
        if (view.getId() == R.id.Start_button) {
            brojac_start++;

            if (brojac_start == 1) {
                buttonStart.setBackgroundColor(getResources().getColor(R.color.tamno_zelena));
                buttonStart.setText("Restart");
                enableButtons();
                randomImages();

            } else if (brojac_start == 2) {
                buttonStart.setBackgroundColor(getResources().getColor(R.color.tamno_zelena));
                buttonStart.setText("Start");
                brojac_start = 0;
                id++;

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        matrica_identifikacije_slika[i][j] = 0;
                    }
                }

                poeni = 0;

                disableButtons();
                randomImages();

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        slika[i][j].setVisibility(View.INVISIBLE);
                    }
                }

                visibleButtons();

                helper.insert( Integer.toString(id), username, email, Integer.toString(poeni));

                int score = poeni;

                if(score == 0){
                    score--;
                }

                JSONObject json_object = new JSONObject();

                try {
                    json_object.put("username", username);
                    json_object.put("score", score);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int ret = 0;

                        try {
                            ret = http_helper.postJSONObjectFromURL(save_game_url, json_object);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (ret == -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (ret == 201) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Successfull Save", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (ret == 400) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Bad Request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "BOG zna sta je error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }else{
                buttonStart.setBackgroundColor(getResources().getColor(R.color.tamno_zelena));
                buttonStart.setText("Start");
                brojac_start = 0;
                id = 0;

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        matrica_identifikacije_slika[i][j] = 0;
                    }
                }

                disableButtons();
                randomImages();

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        slika[i][j].setVisibility(View.INVISIBLE);
                    }
                }

                visibleButtons();

                helper.insert( Integer.toString(id), username, email, Integer.toString(poeni));

                int score = poeni;

                if(score == 0){
                    score--;
                }

                JSONObject json_object = new JSONObject();

                try {
                    json_object.put("username", username);
                    json_object.put("score", score);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int ret = 0;

                        try {
                            ret = http_helper.postJSONObjectFromURL(save_game_url, json_object);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (ret == -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (ret == 201) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Successfull Save", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (ret == 400) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Bad Request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "BOG zna sta je error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                poeni = 0;
            }
        //Statistics
        } else if (view.getId() == R.id.Statistics_button) {
            Intent intent = new Intent(GameActivity.this, StatisticsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            //Button1_1
        } else if (view.getId() == R.id.button1_1) {
            if (matrica_identifikacije_klika[0][0] == 0) {
                matrica_identifikacije_klika[0][0]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[0][0].setVisibility(View.INVISIBLE);
                slika[0][0].setVisibility(View.VISIBLE);
            } else {
                button[0][0].setVisibility(View.INVISIBLE);
                slika[0][0].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button1_2
        else if (view.getId() == R.id.button1_2) {
            if (matrica_identifikacije_klika[0][1] == 0) {
                matrica_identifikacije_klika[0][1]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[0][1].setVisibility(View.INVISIBLE);
                slika[0][1].setVisibility(View.VISIBLE);
            } else {
                button[0][1].setVisibility(View.INVISIBLE);
                slika[0][1].setVisibility(View.VISIBLE);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button1_3
        else if (view.getId() == R.id.button1_3) {
            if (matrica_identifikacije_klika[0][2] == 0) {
                matrica_identifikacije_klika[0][2]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[0][2].setVisibility(View.INVISIBLE);
                slika[0][2].setVisibility(View.VISIBLE);
            } else {
                button[0][2].setVisibility(View.INVISIBLE);
                slika[0][2].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button1_4
        else if (view.getId() == R.id.button1_4) {
            if (matrica_identifikacije_klika[0][3] == 0) {
                matrica_identifikacije_klika[0][3]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[0][3].setVisibility(View.INVISIBLE);
                slika[0][3].setVisibility(View.VISIBLE);
            } else {
                button[0][3].setVisibility(View.INVISIBLE);
                slika[0][3].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button2_1
        else if (view.getId() == R.id.button2_1) {
            if (matrica_identifikacije_klika[1][0] == 0) {
                matrica_identifikacije_klika[1][0]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[1][0].setVisibility(View.INVISIBLE);
                slika[1][0].setVisibility(View.VISIBLE);
            } else {
                button[1][0].setVisibility(View.INVISIBLE);
                slika[1][0].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button2_2
        else if (view.getId() == R.id.button2_2) {
            if (matrica_identifikacije_klika[1][1] == 0) {
                matrica_identifikacije_klika[1][1]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[1][1].setVisibility(View.INVISIBLE);
                slika[1][1].setVisibility(View.VISIBLE);
            } else {
                button[1][1].setVisibility(View.INVISIBLE);
                slika[1][1].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button2_3
        else if (view.getId() == R.id.button2_3) {
            if (matrica_identifikacije_klika[1][2] == 0) {
                matrica_identifikacije_klika[1][2]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[1][2].setVisibility(View.INVISIBLE);
                slika[1][2].setVisibility(View.VISIBLE);
            } else {
                button[1][2].setVisibility(View.INVISIBLE);
                slika[1][2].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button2_4
        else if (view.getId() == R.id.button2_4) {
            if (matrica_identifikacije_klika[1][3] == 0) {
                matrica_identifikacije_klika[1][3]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[1][3].setVisibility(View.INVISIBLE);
                slika[1][3].setVisibility(View.VISIBLE);
            } else {
                button[1][3].setVisibility(View.INVISIBLE);
                slika[1][3].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button3_1
        else if (view.getId() == R.id.button3_1) {
            if (matrica_identifikacije_klika[2][0] == 0) {
                matrica_identifikacije_klika[2][0]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[2][0].setVisibility(View.INVISIBLE);
                slika[2][0].setVisibility(View.VISIBLE);
            } else {
                button[2][0].setVisibility(View.INVISIBLE);
                slika[2][0].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button3_2
        else if (view.getId() == R.id.button3_2) {
            if (matrica_identifikacije_klika[2][1] == 0) {
                matrica_identifikacije_klika[2][1]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[2][1].setVisibility(View.INVISIBLE);
                slika[2][1].setVisibility(View.VISIBLE);
            } else {
                button[2][1].setVisibility(View.INVISIBLE);
                slika[2][1].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button3_3
        else if (view.getId() == R.id.button3_3) {
            if (matrica_identifikacije_klika[2][2] == 0) {
                matrica_identifikacije_klika[2][2]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[2][2].setVisibility(View.INVISIBLE);
                slika[2][2].setVisibility(View.VISIBLE);
            } else {
                button[2][2].setVisibility(View.INVISIBLE);
                slika[2][2].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button3_4
        else if (view.getId() == R.id.button3_4) {
            if (matrica_identifikacije_klika[2][3] == 0) {
                matrica_identifikacije_klika[2][3]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[2][3].setVisibility(View.INVISIBLE);
                slika[2][3].setVisibility(View.VISIBLE);
            } else {
                button[2][3].setVisibility(View.INVISIBLE);
                slika[2][3].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button4_1
        else if (view.getId() == R.id.button4_1) {
            if (matrica_identifikacije_klika[3][0] == 0) {
                matrica_identifikacije_klika[3][0]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[3][0].setVisibility(View.INVISIBLE);
                slika[3][0].setVisibility(View.VISIBLE);
            } else {
                button[3][0].setVisibility(View.INVISIBLE);
                slika[3][0].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button4_2
        else if (view.getId() == R.id.button4_2) {
            if (matrica_identifikacije_klika[3][1] == 0) {
                matrica_identifikacije_klika[3][1]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[3][1].setVisibility(View.INVISIBLE);
                slika[3][1].setVisibility(View.VISIBLE);
            } else {
                button[3][1].setVisibility(View.INVISIBLE);
                slika[3][1].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button4_3
        else if (view.getId() == R.id.button4_3) {
            if (matrica_identifikacije_klika[3][2] == 0) {
                matrica_identifikacije_klika[3][2]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[3][2].setVisibility(View.INVISIBLE);
                slika[3][2].setVisibility(View.VISIBLE);
            } else {
                button[3][2].setVisibility(View.INVISIBLE);
                slika[3][2].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
        //Button4_4
        else if (view.getId() == R.id.button4_4) {
            if (matrica_identifikacije_klika[3][3] == 0) {
                matrica_identifikacije_klika[3][3]++;
            } else {
                return;
            }

            brojac_global++;

            if (brojac_global == 1) {
                button[3][3].setVisibility(View.INVISIBLE);
                slika[3][3].setVisibility(View.VISIBLE);
            } else {
                button[3][3].setVisibility(View.INVISIBLE);
                slika[3][3].setVisibility(View.VISIBLE);
                this.disableButtons();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_loop();
                    }
                }, 1000);
            }
        }
    }

    public void game_loop() {
        end = 1;
        this.invisibleImages();
        this.visibleButtons();

        //matrix ~ 0
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrica_identifikacije_klika[i][j] = 0;
            }
        }

        if (end == 1) {
            buttonStart.setBackgroundColor(getResources().getColor(R.color.svetlo_zelena));
            buttonStart.setText("End");
            brojac_start = 2;
        }

        brojac_global = 0;
        brojac_slika = 0;
        this.enableButtons();
    }

    public void disableButtons() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                button[i][j].setEnabled(false);
            }
        }
    }

    public void enableButtons() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                button[i][j].setEnabled(true);
            }
        }
    }

    public void findViews() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                button[i][j] = findViewById(button_ids[i][j]);
            }
        }

        buttonStart = findViewById(R.id.Start_button);
        buttonStatistics = findViewById(R.id.Statistics_button);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                slika[i][j] = findViewById(image_ids[i][j]);
            }
        }
    }

    public void setListeners() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                button[i][j].setOnClickListener(this);
            }
        }

        buttonStart.setOnClickListener(this);
        buttonStatistics.setOnClickListener(this);
    }

    public void visibleButtons() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (matrica_identifikacije_slika[i][j] != 1) {
                    button[i][j].setVisibility(View.VISIBLE);
                } else {
                    button[i][j].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void invisibleImages(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (matrica_identifikacije_klika[i][j] == 1) {
                    if (brojac_slika == 0) {
                        bitmap1 = ((BitmapDrawable) slika[i][j].getDrawable()).getBitmap();
                        brojac_slika++;
                    } else {
                        bitmap2 = ((BitmapDrawable) slika[i][j].getDrawable()).getBitmap();
                    }
                }
            }
        }

        if (bitmap1 == bitmap2 && brojac_slika != 0) {
            //poeni += 5;
            poeni = jni.points(true, poeni);
            Toast.makeText(getApplicationContext(), String.valueOf(poeni), Toast.LENGTH_SHORT).show();

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (matrica_identifikacije_klika[i][j] == 1) {
                        matrica_identifikacije_slika[i][j] = 1;
                    }
                }
            }
        } else{
            //poeni--;
            poeni = jni.points(false, poeni);

            if (poeni < 0) {
                poeni = 0;
            }

            Toast.makeText(getApplicationContext(), String.valueOf(poeni), Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (matrica_identifikacije_slika[i][j] == 1) {
                    slika[i][j].setVisibility(View.VISIBLE);
                } else {
                    slika[i][j].setVisibility(View.INVISIBLE);
                    end = 0;
                }
            }
        }
    }

    public void randomImages() {
        List<Integer> image_res_copy = new ArrayList<Integer>() {{
            add(R.drawable.jez);
            add(R.drawable.jez);
            add(R.drawable.konj);
            add(R.drawable.konj);
            add(R.drawable.krava);
            add(R.drawable.krava);
            add(R.drawable.lav);
            add(R.drawable.lav);
            add(R.drawable.majmun);
            add(R.drawable.majmun);
            add(R.drawable.ovca);
            add(R.drawable.ovca);
            add(R.drawable.sova);
            add(R.drawable.sova);
            add(R.drawable.zmija);
            add(R.drawable.zmija);
        }};

        Random rand = new Random();

        int r = 16;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int p = rand.nextInt(r);

                slika[i][j].setImageResource(image_res_copy.get(p));
                slika[i][j].setVisibility(View.INVISIBLE);
                image_res_copy.remove(p);
                r--;
            }
        }
    }
}