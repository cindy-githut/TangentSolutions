package com.solutions.tangent.tangentsolutions.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.solutions.tangent.tangentsolutions.NetworkUrls;
import com.solutions.tangent.tangentsolutions.R;
import com.solutions.tangent.tangentsolutions.SharedPreferencesData;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.solutions.tangent.tangentsolutions.NetworkUrls.JSON;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    Button signIn;
    EditText txtUsername, txtPassword;
    ProgressBar login_progress;
    OkHttpClient client;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.signIn);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);
        client = new OkHttpClient();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateLogin();

            }
        });

    }

    private void validateLogin(){

        username = txtUsername.getText().toString();
        password = txtPassword.getText().toString();

        if(TextUtils.isEmpty(username)){

            txtUsername.setError(getString(R.string.error_invalid_username));
            txtUsername.findFocus();

        }else if(TextUtils.isEmpty(password)){

            txtPassword.setError(getString(R.string.error_invalid_username));
            txtPassword.findFocus();

        }else{

            attemptLogin();
        }
    }

    private void attemptLogin(){

        JSONObject loginRequestPayload = null;
        login_progress.setVisibility(View.VISIBLE);
        signIn.setVisibility(View.GONE);

        try {

            loginRequestPayload = new JSONObject();
            loginRequestPayload.put("password",password);
            loginRequestPayload.put("username", username);


        } catch (JSONException e) {

            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, loginRequestPayload.toString());
        Request request = new Request.Builder()
                .url(NetworkUrls.AUTHENTICATE_USER_LOGIN)
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(LoginActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        login_progress.setVisibility(View.GONE);
                        signIn.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responsePayload = response.body().string();

                Log.d(TAG, "Response Code:" + response.code() + responsePayload);

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        login_progress.setVisibility(View.GONE);
                        signIn.setVisibility(View.VISIBLE);

                        if (response.isSuccessful()) {

                            if(response.code() == 200){
                                try {

                                    JSONObject responseObject = new JSONObject(responsePayload);

                                    SharedPreferences.Editor editor = SharedPreferencesData.storeSharedPreference(LoginActivity.this);
                                    editor.putString("token", responseObject.getString("token"));
                                    editor.apply();

                                    startActivity(new Intent(LoginActivity.this, ProjectListActivity.class));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }else{

                            if(response.code() == 400){

                                try {
                                    JSONObject  jsonError = new JSONObject(responsePayload);

                                    if(jsonError.getJSONArray("non_field_errors") != null){

                                        Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                });

            }
        });
    }

}
