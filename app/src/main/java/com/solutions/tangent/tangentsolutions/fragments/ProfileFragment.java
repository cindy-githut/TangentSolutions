package com.solutions.tangent.tangentsolutions.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    OkHttpClient client;
    TextView profile;
    String firstName, lastName, username;

    public ProfileFragment() {
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
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new OkHttpClient();
        getUserProfile(NetworkUrls.GET_USER_PROFILE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile = (TextView) view.findViewById(R.id.profile);

        return view;
    }


    private void getUserProfile(String url) {

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

                            if (response.isSuccessful()) {

                                try {

                                    JSONObject profileInfor = new JSONObject(responseString);

                                    if(profileInfor.has("first_name") && profileInfor.getString("first_name") != null){
                                        firstName = profileInfor.getString("first_name");
                                    }

                                    if(profileInfor.has("last_name") && profileInfor.getString("last_name") != null){
                                        lastName = profileInfor.getString("last_name");
                                    }

                                    if(profileInfor.has("username") && profileInfor.getString("username") != null){
                                        username = profileInfor.getString("username");
                                    }

                                    profile.setText(firstName + " " + lastName + "\n"
                                                    + "Username: " + username);

                                } catch (JSONException e) {
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
}
