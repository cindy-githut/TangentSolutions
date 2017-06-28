package com.solutions.tangent.tangentsolutions.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class AddProjectActivity extends AppCompatActivity {

    private static final String TAG = AddProjectActivity.class.getSimpleName();
    Button bntAdd;
    EditText title, description, startDate, endDate;
    CheckBox chkBillable, chkActive;
    OkHttpClient client;
    ProgressBar project_progress;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        setToolbar();

        bntAdd = (Button) findViewById(R.id.bntAdd);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        chkBillable = (CheckBox) findViewById(R.id.billable);
        chkActive = (CheckBox) findViewById(R.id.active);
        project_progress = (ProgressBar) findViewById(R.id.project_progress);
        client = new OkHttpClient();
        intent = getIntent();

        //for edit mode
        if(intent.hasExtra("id")){

            bntAdd.setText(getResources().getString(R.string.save));
            title.append(intent.getStringExtra("projectTitle"));
            description.append(intent.getStringExtra("projectDescription"));
            startDate.append(intent.getStringExtra("projectStartDate"));
            endDate.append(intent.getStringExtra("projectEndDate"));

            if(!intent.getBooleanExtra("active", false)){
                chkActive.setChecked(false);
            }

            if(!intent.getBooleanExtra("billable", false)){
                chkBillable.setChecked(false);
            }

        }

        bntAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProjectField();
            }
        });
    }

    public void setToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setTitle("Add Project");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }
    private void validateProjectField(){

        if(TextUtils.isEmpty(title.getText().toString())){

            title.setError(getString(R.string.error_invalid_title));
            title.findFocus();

        }else if(TextUtils.isEmpty(description.getText().toString())){

            description.setError(getString(R.string.error_invalid_description));
            description.findFocus();

        }else if(TextUtils.isEmpty(startDate.getText().toString())){

            startDate.setError(getString(R.string.error_invalid_startdate));
            startDate.findFocus();

        }else{

            attemptAddproject();

        }
    }
    private void attemptAddproject(){

        JSONObject projectRequestPayload = null;

        project_progress.setVisibility(View.VISIBLE);
        bntAdd.setVisibility(View.GONE);

        try {

            projectRequestPayload = new JSONObject();
            projectRequestPayload.put("title",title.getText().toString());
            projectRequestPayload.put("description", description.getText().toString());
            projectRequestPayload.put("start_date", startDate.getText().toString());

            if(!TextUtils.isEmpty(endDate.getText().toString())){

                projectRequestPayload.put("end_date", endDate.getText().toString());

            }

            if(!chkActive.isChecked()){

                projectRequestPayload.put("is_active", false);

            }else{

                projectRequestPayload.put("is_active", true);

            }

            if(!chkBillable.isChecked()){

                projectRequestPayload.put("is_billable", false);

            }else{

                projectRequestPayload.put("is_billable", true);

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, projectRequestPayload.toString());
        Request request;

        //check if the intent has an id, if it does then we doing PATCH else we adding a new project using POST
        if(intent.hasExtra("id")) {

            request = new Request.Builder()
                    .url(NetworkUrls.BASE_PROJECT_URL+getIntent().getStringExtra("id") + "/")
                    .header("Authorization","Token " + SharedPreferencesData.getSharedPreference(this).getString("token", null))
                    .patch(body)
                    .build();
        }else{

            request = new Request.Builder()
                    .url(NetworkUrls.BASE_PROJECT_URL)
                    .header("Authorization","Token " + SharedPreferencesData.getSharedPreference(this).getString("token", null))
                    .post(body)
                    .build();

        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                AddProjectActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        project_progress.setVisibility(View.GONE);
                        bntAdd.setVisibility(View.VISIBLE);

                        Toast.makeText(AddProjectActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responsePayload = response.body().string();

                Log.d(TAG, "Response Code:" + response.code() + responsePayload);

                AddProjectActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        project_progress.setVisibility(View.GONE);
                        bntAdd.setVisibility(View.VISIBLE);

                        if (response.isSuccessful()) {

                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();

                        }else{

                            try {

                                JSONObject jsonObjectError = new JSONObject(responsePayload);

                                if(jsonObjectError.has("start_date")){

                                    startDate.setError(String.valueOf(jsonObjectError.getJSONArray("start_date").get(0)));
                                    startDate.findFocus();

                                }

                                if(jsonObjectError.has("end_date")){

                                    endDate.setError(String.valueOf(jsonObjectError.getJSONArray("end_date").get(0)));
                                    endDate.findFocus();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });
    }
}
