package com.olmo.d3stats;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.olmo.d3stats.utils.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.interfaces.Communication;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
        implements HeroesListFragment.HeroesListListener {

    public static final String PREFS_NAME = "D3StatsPref";
    private CharSequence mTitle;
    private SlidingPaneLayout pane;
    private DatabaseHelper databaseHelper = null;
    private boolean firstStart = true;
    private boolean noHeroes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_main);

        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new PaneListener());

        float d = this.getResources().getDisplayMetrics().density;
        pane.setParallaxDistance((int)(60*d));
        pane.setShadowResource(R.drawable.shadow_slidingpane);

        ArrayList<Hero> heroes = new ArrayList<Hero>();

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            for(Hero hero: heroDao)
                heroes.add(hero);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(heroes.size()==0){
            noHeroes = true;
            AddHeroFragment newFragment = new AddHeroFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.rightpane, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else{
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            int lastheroid = settings.getInt("lastheroid", -1);

            if(lastheroid!=1){
                onChangeHero(lastheroid);
            }
            else{
                EmptyFragment newFragment = new EmptyFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.rightpane, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        /*if (!pane.isSlideable()) {
            getSupportFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(false);
            getSupportFragmentManager().findFragmentById(R.id.rightpane).setHasOptionsMenu(true);
        }*/

        //pane.openPane();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(noHeroes && firstStart){
            firstStart = false;
            pane.closePane();

            getSupportFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(false);
            getSupportFragmentManager().findFragmentById(R.id.rightpane).setHasOptionsMenu(true);
            //((Communication)getSupportFragmentManager().findFragmentById(R.id.leftpane)).setTitle();
        }
        else if(firstStart) {
            pane.openPane();
            firstStart = false;

            getSupportFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(true);
            getSupportFragmentManager().findFragmentById(R.id.rightpane).setHasOptionsMenu(false);
            //((Communication)getSupportFragmentManager().findFragmentById(R.id.leftpane)).setTitle();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setTitle(R.string.short_name);
            getSupportActionBar().setSubtitle(null);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                pane.openPane();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(pane.isOpen()){
            this.finish();
        }
        else{
            pane.openPane();
        }
    }

    @Override
    public void onChangeHero(int heroId) {
        HeroFragment newFragment = new HeroFragment();
        Bundle args = new Bundle();
        args.putInt("id", heroId);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rightpane, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("lastheroid", heroId);
        editor.commit();

        //pane.closePane();
    }

    @Override
    public void addHero(){
        AddHeroFragment newFragment = new AddHeroFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rightpane, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        pane.closePane();
    }

    @Override
    public void onNewHero(){
        HeroesListFragment hlf = ((HeroesListFragment)getSupportFragmentManager().findFragmentById(R.id.leftpane));
        if(hlf != null)
            hlf.reloadList();
    }

    public void changeRightToBlank(){
        EmptyFragment newFragment = new EmptyFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rightpane, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void closePane(){
        pane.closePane();
    }

    public void openPane(){
        pane.openPane();
    }

    @Override
    public Fragment getRightPane(){
        return getSupportFragmentManager().findFragmentById(R.id.rightpane);
    }

    private class PaneListener implements SlidingPaneLayout.PanelSlideListener {

        @Override
        public void onPanelClosed(View view) {
            getSupportFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(false);
            getSupportFragmentManager().findFragmentById(R.id.rightpane).setHasOptionsMenu(true);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ((Communication)getSupportFragmentManager().findFragmentById(R.id.rightpane)).setTitle();
        }

        @Override
        public void onPanelOpened(View view) {
            getSupportFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(true);
            getSupportFragmentManager().findFragmentById(R.id.rightpane).setHasOptionsMenu(false);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setTitle(R.string.short_name);
            getSupportActionBar().setSubtitle(null);
        }

        @Override
        public void onPanelSlide(View view, float arg1) {

        }

    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }



}
