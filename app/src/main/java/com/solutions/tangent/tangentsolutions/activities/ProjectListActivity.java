package com.solutions.tangent.tangentsolutions.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.solutions.tangent.tangentsolutions.R;
import com.solutions.tangent.tangentsolutions.SharedPreferencesData;
import com.solutions.tangent.tangentsolutions.fragments.ProfileFragment;
import com.solutions.tangent.tangentsolutions.fragments.ProjectsFragment;

public class ProjectListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NEW_PROJECT = 100;
    public static final int PROJECT_DETAIL = 101;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Projects");
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        //check if there's  a current logged in user if not redirect to the login screen
        if (SharedPreferencesData.getSharedPreference(this).getString("token", null)== null){

            startActivity(new Intent(ProjectListActivity.this, LoginActivity.class));
            finish();
        }

        //onCreate will set default to the Projects tab
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentcontainer, new ProjectsFragment(), "Projects");
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            actionBar.setTitle("Profile");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentcontainer, new ProfileFragment());
            ft.commit();


        } else if (id == R.id.nav_projects) {

            actionBar.setTitle("Projects");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentcontainer, new ProjectsFragment());
            ft.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if(requestCode == NEW_PROJECT && resultCode == RESULT_OK){

            Toast.makeText(ProjectListActivity.this, "Project successfully added.", Toast.LENGTH_SHORT).show();

        }else  if(requestCode == PROJECT_DETAIL && resultCode == RESULT_OK){

            Toast.makeText(ProjectListActivity.this, "Project successfully deleted.", Toast.LENGTH_SHORT).show();

        }
    }
}
