package com.olmo.d3stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.interfaces.Communication;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.utils.DatabaseHelper;
import com.olmo.d3stats.utils.GetHeroTask;

import java.sql.SQLException;
import java.util.ArrayList;

public class HeroFragment extends Fragment implements Communication {
    private DatabaseHelper databaseHelper = null;
    private ArrayList<Hero> heroes = new ArrayList<Hero>();
    private Hero hero = null;
    private FragmentTabHost mTabHost;
    private int heroId;
    private ViewPager mViewPager;

    private TabHost.TabContentFactory mFactory = new TabHost.TabContentFactory() {

        @Override
        public View createTabContent(String tag) {
            View v = new View(getActivity());
            v.setMinimumHeight(0);
            return v;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            heroId = getArguments().getInt("id", -1);
            try {
                Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
                hero = heroDao.queryForId(heroId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            heroId = -1;
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(heroId==-1 || hero==null) {
            FrameLayout mDrawerListView = (FrameLayout) inflater.inflate(
                    R.layout.fragment_empty, container, false);


            return mDrawerListView;

        }
        else {
            //mTabHost = (FragmentTabHost) inflater.inflate(R.layout.fragment_hero, container, false);

            mTabHost = new FragmentTabHost(getActivity());
            mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.rightpane);

            Bundle arg1 = new Bundle();
            arg1.putInt("id", hero.getId());
            mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Stats"),
                    EstadisticasFragment.class, arg1);

            mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Items"),
                    ItemsFragment.class, arg1);

            mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Paragon"),
                    ParagonFragment.class, arg1);

            mTabHost.addTab(mTabHost.newTabSpec("Tab4").setIndicator("Pasives"),
                    PasivesFragment.class, arg1);

            setTitle();

            return mTabHost;

            /*View fragContent = inflater.inflate(R.layout.fragment_hero, container, false);
            mViewPager = (ViewPager) fragContent.findViewById(R.id.pager);
            mViewPager.setAdapter(new CustomerInnerPagerAdapter(getChildFragmentManager()));
            final TabHost tabHost = (TabHost) fragContent.findViewById(R.id.tabhost);

            tabHost.setup();
            tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("Stats").setContent(mFactory));
            tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("Items").setContent(mFactory));
            tabHost.addTab(tabHost.newTabSpec("Tab3").setIndicator("Paragon").setContent(mFactory));
            tabHost.addTab(tabHost.newTabSpec("Tab4").setIndicator("Pasives").setContent(mFactory));


            mViewPager.setOffscreenPageLimit(4);

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    tabHost.setCurrentTab(position);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

                @Override
                public void onTabChanged(String tabId) {
                    if (tabId.equals("Tab1")) {
                        mViewPager.setCurrentItem(0);
                    } else if (tabId.equals("Tab2")) {
                        mViewPager.setCurrentItem(1);
                    } else if (tabId.equals("Tab3")) {
                        mViewPager.setCurrentItem(2);
                    } else if (tabId.equals("Tab4")) {
                        mViewPager.setCurrentItem(3);
                    }
                }
            });
            return fragContent;*/
        }
    }

    private class CustomerInnerPagerAdapter extends FragmentPagerAdapter {

        public CustomerInnerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new EstadisticasFragment(hero.getId());
            } else if (position == 1) {
                return new ItemsFragment(hero.getId());
            } else if (position == 2) {
                return new ParagonFragment(hero.getId());
            } else if (position == 3) {
                return new PasivesFragment(hero.getId());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

    @Override
    public void onStart(){
        super.onStart();

        ( (MainActivity) getActivity()).closePane();
    }

    @Override
    public void setTitle(){
        if(hero!=null) {
            ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setIcon(getResources().getIdentifier(hero.getClase().toLowerCase()+"_"+hero.getGender(), "drawable", this.getActivity().getPackageName()));
            bar.setTitle(hero.getName() + " " + hero.getLevel() + " (" + hero.getParagonLevel() + ")");
            bar.setSubtitle(this.getResources().getIdentifier(hero.getClase(), "string", this.getActivity().getPackageName()));
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mTabHost = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }*/
        inflater.inflate(R.menu.hero, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            ArrayList<Hero> updateHero = new ArrayList<Hero>();
            updateHero.add(hero);
            GetHeroTask oh = new GetHeroTask(updateHero,false,this.getActivity());
            oh.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void setHero(int heroId){
        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            hero = heroDao.queryForId(heroId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getHeroId(){
        return heroId;
    }

}
