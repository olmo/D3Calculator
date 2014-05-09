package com.olmo.d3stats.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.olmo.d3stats.R;
import com.olmo.d3stats.models.Hero;

import java.util.ArrayList;
import java.util.Collections;

public class HeroesListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Hero> data;
    private ListView mLv;

    public HeroesListAdapter(Context context, ArrayList<Hero> data, ListView lv){
        this.context = context;
        this.data = data;
        mLv = lv;
    }

    public void toggleSelection(int index) {
        mLv.setItemChecked(index, !mLv.isItemChecked(index));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Hero getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= data.get(data.size()-1).getPosition()) {
            return -1;
        }
        return data.get(position).getPosition();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_hero, null);
        }

        /*if (is_element_selected[position])
            convertView.setBackgroundColor(context.getResources().getColor(R.color.blue_item_selector));
        else
            convertView.setBackgroundColor(Color.TRANSPARENT);*/

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtSubtitle = (TextView) convertView.findViewById(R.id.subtitle);
        ImageView imgHardcore = (ImageView) convertView.findViewById(R.id.harcore);


        imgIcon.setImageResource(context.getResources().getIdentifier(
                data.get(position).getClase().toLowerCase()+"_"
                        + data.get(position).getGender(),
                "drawable",
                context.getPackageName()
        ));

        final int pos = position;

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(pos);
            }
        });

        txtTitle.setText(data.get(position).getName());
        txtSubtitle.setText(data.get(position).getProfile());

        // displaying count
        // check whether it set visible or not
        /*if(data.get(position).getCounterVisibility()){
            txtCount.setText(data.get(position).getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }*/

        if(data.get(position).isHardcore()){
            imgHardcore.setVisibility(View.VISIBLE);
        }
        else{
            imgHardcore.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void remove(int position){
        data.remove(position);
        //notifyDataSetChanged();
    }

    public boolean swapPositions(int pos1, int pos2){
        if(pos2!=-1 && pos2!=data.size()) {
            Collections.swap(data, pos1, pos2);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
