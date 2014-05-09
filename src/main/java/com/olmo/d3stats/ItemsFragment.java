package com.olmo.d3stats;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.adapters.ItemsListAdapter;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;
import com.olmo.d3stats.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.Arrays;

public class ItemsFragment extends Fragment {
    private DatabaseHelper databaseHelper = null;
    private int heroeid = 0;
    private Hero hero;
    private ItemsListAdapter adapter;

    public ItemsFragment(){}
    public ItemsFragment(int heroeid){
        this.heroeid = heroeid;
    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(heroeid==0)
            heroeid = getArguments().getInt("id", 0);

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            hero = heroDao.queryForId(heroeid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null)
            return null;

        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_items, container, false);
        ListView lv = (ListView) v.findViewById(R.id.listView);

        Object[] itemsO = hero.items.toArray();

        Item[] items = Arrays.asList(itemsO).toArray(new Item[itemsO.length]);

        adapter = new ItemsListAdapter(this.getActivity(), items);
        lv.addFooterView(new View(this.getActivity()), null, false);
        lv.addHeaderView(new View(this.getActivity()), null, false);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ItemsFragment.this.getActivity(), ItemActivity.class);
                intent.putExtra("heroid", hero.getId());
                intent.putExtra("itemid", position-1);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            hero = heroDao.queryForId(heroeid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Object[] itemsO = hero.items.toArray();
        Item[] items = Arrays.asList(itemsO).toArray(new Item[itemsO.length]);
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
