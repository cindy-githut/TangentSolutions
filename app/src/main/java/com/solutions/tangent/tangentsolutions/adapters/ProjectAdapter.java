package com.solutions.tangent.tangentsolutions.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.solutions.tangent.tangentsolutions.activities.ProjectDetailedViewActivity;
import com.solutions.tangent.tangentsolutions.R;
import com.solutions.tangent.tangentsolutions.models.ProjectItem;
import java.util.ArrayList;

import static com.solutions.tangent.tangentsolutions.activities.ProjectListActivity.PROJECT_DETAIL;

public class ProjectAdapter extends BaseAdapter{

    private ArrayList<ProjectItem> listProjects;
    private LayoutInflater inflater;
    private Activity activity;

    public ProjectAdapter(ArrayList<ProjectItem> listProjects, Activity activity) {
        this.listProjects = listProjects;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);

    }

    @Override
    public int getCount() {
        return listProjects.size();
    }

    @Override
    public ProjectItem getItem(int position) {
        return listProjects.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        
        final ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();

            view = this.inflater.inflate(R.layout.project_layout,
                    viewGroup, false);

            holder.projectTitle = (TextView) view
                    .findViewById(R.id.projectTitle);
            holder.projectDescription = (TextView) view
                    .findViewById(R.id.projectDescription);
            holder.cardView = (CardView) view.findViewById(R.id.cardView);

            view.setTag(holder);

        } else {

            holder = (ViewHolder) view.getTag();
        }


        // Current flight
        holder.projectTitle.setText(listProjects.get(position).getProjectTitle());
        holder.projectDescription.setText(listProjects.get(position).getProjectDescription());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, ProjectDetailedViewActivity.class);

                intent.putExtra("projectTitle",listProjects.get(position).getProjectTitle());
                intent.putExtra("projectDescription",listProjects.get(position).getProjectDescription());
                intent.putExtra("projectStartDate",listProjects.get(position).getStartDate());
                intent.putExtra("projectEndDate",listProjects.get(position).getEndDate());
                intent.putExtra("active",listProjects.get(position).getActive());
                intent.putExtra("billable",listProjects.get(position).getBillable());
                intent.putExtra("id",String.valueOf(listProjects.get(position).getProjectId()));

                activity.startActivityForResult(intent, PROJECT_DETAIL);
            }
        });

        return view;
    }

    private class ViewHolder {

        private TextView projectTitle;
        private TextView projectDescription;
        private CardView cardView;
    }
    
}
