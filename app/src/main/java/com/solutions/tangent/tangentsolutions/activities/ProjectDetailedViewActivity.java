package com.solutions.tangent.tangentsolutions.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import okhttp3.Response;

public class ProjectDetailedViewActivity extends AppCompatActivity {

    TextView details;
    String projectTitle, projectDescription, projectStartDate, projectEndDate;
    Boolean active = false;
    Boolean billable = false;
    OkHttpClient client;
    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        setToolbar();

        Intent intent = getIntent();
        details = (TextView) findViewById(R.id.details);
        client = new OkHttpClient();

        projectId = Integer.parseInt(intent.getStringExtra("id"));

        getProjectDetail();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProjectDetail();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {

            deleteProject();

        }else if(id == R.id.action_edit){

            Intent intent = new Intent(ProjectDetailedViewActivity.this, AddProjectActivity.class);

            intent.putExtra("projectTitle",projectTitle);
            intent.putExtra("projectDescription",projectDescription);
            intent.putExtra("projectStartDate",projectStartDate);
            intent.putExtra("projectEndDate",projectEndDate);
            intent.putExtra("active",active);
            intent.putExtra("billable",billable);
            intent.putExtra("id",String.valueOf(projectId));

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProjectDetail() {

        Request request = new Request.Builder()
                .url(NetworkUrls.BASE_PROJECT_URL + projectId+"/")
                .header("Authorization","Token " + SharedPreferencesData.getSharedPreference(ProjectDetailedViewActivity.this).getString("token", null))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                try{
                    ProjectDetailedViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProjectDetailedViewActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception exc){
                    Log.d("Error:", exc.getLocalizedMessage());
                }

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responseString = response.body().string();

                try{

                    ProjectDetailedViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (response.isSuccessful()) {

                                try {

                                    JSONObject profileInfor = new JSONObject(responseString);

                                    if(profileInfor.has("title") && profileInfor.getString("title") != null){
                                        projectTitle = profileInfor.getString("title");
                                    }

                                    if(profileInfor.has("description") && profileInfor.getString("description") != null){
                                        projectDescription = profileInfor.getString("description");
                                    }

                                    if(profileInfor.has("start_date") && profileInfor.getString("start_date") != null){
                                        projectStartDate = profileInfor.getString("start_date");
                                    }

                                    if(profileInfor.has("end_date") && profileInfor.getString("end_date") != null){
                                        projectEndDate = profileInfor.getString("end_date");
                                    }

                                    if(profileInfor.getBoolean("is_billable")){
                                        billable = profileInfor.getBoolean("is_billable");
                                    }

                                    if(profileInfor.getBoolean("is_active")){
                                        active = profileInfor.getBoolean("is_active");
                                    }

                                    details.setText(projectTitle + "\n"
                                            + projectDescription + "\n"
                                            + "Project Start Date: " +  projectStartDate + "\n"
                                            + "Project Ends: " + projectEndDate + "\n"
                                            + "Is Active: " + active +  "\n"
                                            + "Is Billable:" + billable);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else{

                                Toast.makeText(ProjectDetailedViewActivity.this, "Something went wrong please, try again later.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }catch(Exception exc){
                    Log.d("Error:", exc.getLocalizedMessage());
                }

            }
        });
    }

    private void deleteProject(){

        Request request = new Request.Builder()
                .url("http://projectservice.staging.tangentmicroservices.com/api/v1/projects/"+projectId+"/")
                .header("Authorization","Token " + SharedPreferencesData.getSharedPreference(ProjectDetailedViewActivity.this).getString("token", null))
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                try{

                    ProjectDetailedViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProjectDetailedViewActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });

                }catch(Exception exc){
                    Log.d("Error:", exc.getLocalizedMessage());
                }

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try{

                    ProjectDetailedViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (response.isSuccessful()) {

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();

                            }else{

                                Toast.makeText(ProjectDetailedViewActivity.this, "Something went wrong please, try again later.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }catch(Exception exc){
                    Log.d("Error:", exc.getLocalizedMessage());
                }

            }
        });

    }

    public void setToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setTitle("Project");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

}
