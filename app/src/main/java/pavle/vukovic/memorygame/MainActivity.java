package pavle.vukovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextUsername, editTextPassword;
    TextView textViewUsername, textViewPassword;
    Button buttonLogin, buttonRegister;
    HTTPHelper helper = new HTTPHelper();
    String login_url = "http://192.168.85.223:3000/auth/signin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.Username_edit);
        editTextPassword = findViewById(R.id.Password_edit);

        textViewUsername = findViewById(R.id.Username_text);
        textViewPassword = findViewById(R.id.Password_text);

        buttonLogin = findViewById(R.id.Login_button);
        buttonRegister = findViewById(R.id.Register_button);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String editText = editTextUsername.getText().toString();

        if(view.getId() == R.id.Login_button){
            String editTextUser = editTextUsername.getText().toString();
            String editTextPass = editTextPassword.getText().toString();

            JSONObject json_object = new JSONObject();

            try {
                json_object.put("username", editTextUser);
                json_object.put("password", editTextPass);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int ret = 0;

                    try {
                        ret = helper.postJSONObjectFromURL(login_url, json_object);
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
                    }else if(ret == 201){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Successfull Login", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra("username", editTextUsername.getText().toString());
                        startActivity(intent);
                    }else if(ret == 400){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "BOG zna sta je error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();

        }else if(view.getId() == R.id.Register_button){
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}