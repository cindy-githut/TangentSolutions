package com.solutions.tangent.tangentsolutions.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.solutions.tangent.tangentsolutions.activities.AddProjectActivity;
import com.solutions.tangent.tangentsolutions.NetworkUrls;
import com.solutions.tangent.tangentsolutions.R;
import com.solutions.tangent.tangentsolutions.SharedPreferencesData;
import com.solutions.tangent.tangentsolutions.adapters.ProjectAdapter;
import com.solutions.tangent.tangentsolutions.models.ProjectItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsFragment extends Fragment {

    OkHttpClient client;
    ProgressBar progressBar;
    ListView listview;
    ProjectItem projectItem;
    ArrayList<ProjectItem> projectList;
    ProjectAdapter projectAdapter;
    FloatingActionButton addProject;
    public static final int NEW_PROJECT = 100;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectsFragment newInstance(String param1, String param2) {
        ProjectsFragment fragment = new ProjectsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new OkHttpClient();
        projectList = new ArrayList<>();

        populateListWithData(NetworkUrls.BASE_PROJECT_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.projects_fragment, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        listview = (ListView) view.findViewById(R.id.listview);
        addProject = (FloatingActionButton) view.findViewById(R.id.addProject);

        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().startActivityForResult(new Intent(getActivity(), AddProjectActivity.class), NEW_PROJECT);
            }
        });

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        populateListWithData(NetworkUrls.BASE_PROJECT_URL);

    }

    private void populateListWithData(String url) {

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","Token " + SharedPreferencesData.getSharedPreference(getActivity()).getString("token", null))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);

                            if (response.isSuccessful()) {

                                try {

                                    JSONArray projects = new JSONArray(responseString);

                                    if(projectList != null){
                                        projectList.clear();
                                    }

                                    handleServerResponse(projects);

                                } catch (JSONException | ParseException e) {
                                    e.printStackTrace();
                                }

                            }else{

                                Toast.makeText(getActivity(), "Something went wrong please, try again later.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }catch(Exception exc){
                    Log.d("Error:", exc.getLocalizedMessage());
                }

            }
        });
    }

    private void handleServerResponse(JSONArray jsonArray) throws JSONException, ParseException {


        for (int i = 0; i < jsonArray.length(); i++){

            JSONObject projects = jsonArray.getJSONObject(i);

            projectItem = new ProjectItem();

            projectItem.setProjectId(projects.getInt("pk"));

            if(projects.has("title") && projects.getString("title") != null){
                projectItem.setProjectTitle(projects.getString("title"));
            }else{
                projectItem.setProjectTitle("");
            }

            if(projects.has("description") && projects.getString("description") != null){
                projectItem.setProjectDescription(projects.getString("description"));
            }else{
                projectItem.setProjectDescription("");
            }

            if(projects.has("start_date") && projects.getString("start_date") != null){
                projectItem.setStartDate(projects.getString("start_date"));
            }else{
                projectItem.setStartDate("");
            }

            if(projects.has("end_date") && projects.getString("end_date") != null){
                projectItem.setEndDate(projects.getString("end_date"));
            }else{
                projectItem.setEndDate("");
            }

            if(projects.has("is_billable")){
                projectItem.setBillable(projects.getBoolean("is_billable"));
            }

            if(projects.has("is_active")){
                projectItem.setActive(projects.getBoolean("is_active"));
            }

            projectList.add(projectItem);
        }

        try{

            projectAdapter = new ProjectAdapter(projectList, getActivity());
            listview.setAdapter(projectAdapter);

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
