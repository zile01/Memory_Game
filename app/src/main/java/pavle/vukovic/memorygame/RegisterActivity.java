package pavle.vukovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextUsername, editTextPassword, editTextEmail;
    TextView textViewUsername, textViewPassword, textViewEmail;
    Button buttonRegister;
    String register_url = "http://192.168.85.223:3000/auth/signup";
    HTTPHelper http_helper = new HTTPHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.Username_edit);
        editTextPassword = findViewById(R.id.Password_edit);
        editTextEmail = findViewById(R.id.Email_edit);

        textViewUsername = findViewById(R.id.Username_text);
        textViewPassword = findViewById(R.id.Password_text);
        textViewEmail = findViewById(R.id.Email_text);

        buttonRegister = findViewById(R.id.Register_button1);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.Register_button1) {
            String editTextUser = editTextUsername.getText().toString();
            String editTextPass = editTextPassword.getText().toString();
            String editTextMail = editTextEmail.getText().toString();

            JSONObject json_object = new JSONObject();

            try {
                json_object.put("username", editTextUser);
                json_object.put("password", editTextPass);
                json_object.put("email", editTextMail);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int ret = 0;

                    try {
                        ret = http_helper.postJSONObjectFromURL(register_url, json_object);
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
                                Toast.makeText(getApplicationContext(), "Successfull Register", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
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
        }
    }
}