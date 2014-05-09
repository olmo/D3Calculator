package com.olmo.d3stats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.adapters.HeroesListAdapter;
import com.olmo.d3stats.interfaces.Communication;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;
import com.olmo.d3stats.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HeroesListFragment extends Fragment {
    public static final String PREFS_NAME = "D3StatsPref";
    private DatabaseHelper databaseHelper = null;
    private ArrayList<Hero> heroes = new ArrayList<Hero>();
    private ListView mListView;
    private ActionMode mMode;
    HeroesListAdapter mAdapter;
    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

        mListView = (ListView) inflater.inflate(
                R.layout.fragment_heroes_list, container, false);

        mListView.setSelector(R.drawable.list_selector);
        reloadList();


        /*ContextualUndoAdapter adapter = new ContextualUndoAdapter(mAdapter, R.layout.undo_row, R.id.undo_row_undobutton, this);
        adapter.setAbsListView(mListView);
        mListView.setAdapter(adapter);*/


        //mListView.setItemChecked(mCurrentSelectedPosition, true);
        //mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        mMode = null;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {


                ((HeroesListListener) getActivity()).onChangeHero(heroes.get(position).getId());
            }
        });

        //mListView.set
        /*mListView.setOnItemMovedListener(new DynamicListView.OnItemMovedListener() {
            @Override
            public void onItemMoved(final int newPosition) {
                Toast.makeText(HeroesListFragment.this.getActivity(), mAdapter.getItem(newPosition) + " moved to position " + newPosition, Toast.LENGTH_SHORT).show();
            }
        });*/

        //mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //mListView.setMultiChoiceModeListener(new ModeCallback());

        /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                boolean hasCheckedElement = false;
                for (int i = 0 ; i < checked.size() && ! hasCheckedElement ; i++) {
                    hasCheckedElement = checked.valueAt(i);
                }

                if (hasCheckedElement) {
                    if (mMode == null) {
                        mMode = ((MainActivity)getActivity()).startSupportActionMode(new ModeCallback());
                    }
                } else {
                    if (mMode != null) {
                        mMode.finish();
                    }
                }
                return hasCheckedElement;
            }
        });*/

        /*mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                boolean hasCheckedElement = false;
                for (int i = 0 ; i < checked.size() && ! hasCheckedElement ; i++) {
                    hasCheckedElement = checked.valueAt(i);
                }

                if (hasCheckedElement) {
                    if (mMode == null) {
                        mMode = ((MainActivity)getActivity()).startSupportActionMode(new ModeCallback());
                    }
                } else {
                    if (mMode != null) {
                        mMode.finish();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        mListView.setMultiChoiceModeListener(new ModeCallback());

        return mListView;
    }

    public void reloadList(){
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        int sortType = settings.getInt("sorttype", 0);

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            heroes.clear();

            List<Hero> heros = null;

            if(sortType==0) {
                heros = heroDao.query(
                        heroDao.queryBuilder().orderBy("position", true).prepare()
                );
            }
            else if(sortType==1){
                heros = heroDao.query(
                        heroDao.queryBuilder().orderByRaw("name COLLATE NOCASE").prepare()
                );
            }

            for(Hero hero: heros)
                heroes.add(hero);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mAdapter = new HeroesListAdapter(this.getActivity(), heroes, mListView);

        mListView.setAdapter(mAdapter);
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
        inflater.inflate(R.menu.heroeslist, menu);
        mMenu = menu;

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        int sortType = settings.getInt("sorttype", 0);
        if(sortType==0){
            menu.findItem(R.id.action_sort).setIcon(android.R.drawable.ic_menu_sort_by_size);
        }
        else{
            menu.findItem(R.id.action_sort).setIcon(android.R.drawable.ic_menu_sort_alphabetically);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            ((MainActivity)this.getActivity()).addHero();
            return true;
        }
        else if(id == R.id.menuSortPosition){
            SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("sorttype", 0);
            editor.commit();

            mMenu.findItem(R.id.action_sort).setIcon(android.R.drawable.ic_menu_sort_by_size);
            reloadList();
        }
        else if(id == R.id.menuSortAlphabetically){
            SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("sorttype", 1);
            editor.commit();

            mMenu.findItem(R.id.action_sort).setIcon(android.R.drawable.ic_menu_sort_alphabetically);
            reloadList();
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


    private class ModeCallback implements ListView.MultiChoiceModeListener {
        Menu mMenuSel;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.list_select_menu, menu);
            mode.setTitle("Select Heroes");
            menu.findItem(R.id.up).setVisible(false);
            menu.findItem(R.id.down).setVisible(false);
            mMenuSel = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:

                    ArrayList<Integer> deleteItems = new ArrayList<Integer>();
                    ArrayList<Integer> deleteIds = new ArrayList<Integer>();

                    try {
                        Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
                        Dao<Item, Integer> itemDao = getHelper().getItemDao();

                        for (int i = 0; i < mAdapter.getCount(); i++) {
                            if(mListView.isItemChecked(i)) {
                                Hero h = mAdapter.getItem(i);
                                deleteItems.add(0, i);
                                deleteIds.add(0, h.getId());

                                for(Item it: h.items)
                                    itemDao.delete(it);

                                heroDao.delete(h);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    int idRightPane = ((Communication)((MainActivity)HeroesListFragment.this.getActivity()).getRightPane()).getHeroId();

                    for(int i=0; i<deleteItems.size(); i++) {
                        if(deleteIds.get(i)==idRightPane)
                            ((MainActivity)HeroesListFragment.this.getActivity()).changeRightToBlank();
                        mAdapter.remove(deleteItems.get(i));
                    }
                    mAdapter.notifyDataSetChanged();



                    Toast.makeText(getActivity(), "Deleted " + mListView.getCheckedItemCount() +
                            " heroes", Toast.LENGTH_SHORT).show();

                    mode.finish();
                    break;

                case R.id.up:
                    int pos = -1;
                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        if (mListView.isItemChecked(i)) {
                            pos = i;
                            break;
                        }
                    }
                    if(mAdapter.swapPositions(pos, pos-1)){
                        try {
                            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
                            Hero h = heroDao.queryForId(mAdapter.getItem(pos).getId());
                            Hero h2 = heroDao.queryForId(mAdapter.getItem(pos-1).getId());
                            h.setPosition(pos);
                            h2.setPosition(pos-1);
                            heroDao.update(h);
                            heroDao.update(h2);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    //mAdapter.notifyDataSetChanged();
                    break;

                case R.id.down:
                    int pos2 = -1;
                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        if (mListView.isItemChecked(i)) {
                            pos2 = i;
                            break;
                        }
                    }
                    if(mAdapter.swapPositions(pos2, pos2+1)){
                        try {
                            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
                            Hero h = heroDao.queryForId(mAdapter.getItem(pos2).getId());
                            Hero h2 = heroDao.queryForId(mAdapter.getItem(pos2+1).getId());
                            h.setPosition(pos2);
                            h2.setPosition(pos2+1);
                            heroDao.update(h);
                            heroDao.update(h2);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    //mAdapter.notifyDataSetChanged();
                    break;

                default:
                    Toast.makeText(getActivity(), "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            /*for (int i = 0; i < mListView.getAdapter().getCount(); i++)
                mListView.setItemChecked(i, false);

            if (mode == mMode) {
                mMode = null;
            }*/
        }


        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked) {
            final int checkedCount = mListView.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    mMenuSel.findItem(R.id.up).setVisible(false);
                    mMenuSel.findItem(R.id.down).setVisible(false);
                    break;
                case 1:
                    mode.setSubtitle("One hero selected");
                    mMenuSel.findItem(R.id.up).setVisible(true);
                    mMenuSel.findItem(R.id.down).setVisible(true);
                    break;
                default:
                    mode.setSubtitle("" + checkedCount + " heroes selected");
                    mMenuSel.findItem(R.id.up).setVisible(false);
                    mMenuSel.findItem(R.id.down).setVisible(false);
                    break;
            }
        }
    }

    public interface HeroesListListener {
        public void onChangeHero(int heroId);
        public void addHero();
        public void onNewHero();
        public Fragment getRightPane();
    }


}
