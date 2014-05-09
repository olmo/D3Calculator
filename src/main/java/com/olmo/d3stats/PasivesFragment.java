package com.olmo.d3stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.utils.DatabaseHelper;

import java.sql.SQLException;

public class PasivesFragment extends Fragment {
	private Hero hero;
    private int heroid = 0;
	private Spinner spinner1;
	private Spinner spinner2;
	private Spinner spinner3;
    private Spinner spinner4;
	
	private String[] pasivas;
    private DatabaseHelper databaseHelper = null;
    public static final String PREFS_NAME = "D3StatsPref";

    public PasivesFragment(){}
    public PasivesFragment(int heroeid){
        this.heroid = heroeid;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(heroid==0)
            heroid = getArguments().getInt("id", 0);

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            hero = heroDao.queryForId(heroid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null)
	        return null;
		
        View v = inflater.inflate(R.layout.fragment_pasives, container, false);
        
        spinner1 = (Spinner) v.findViewById(R.id.spinner1);
        spinner2 = (Spinner) v.findViewById(R.id.spinner2);
        spinner3 = (Spinner) v.findViewById(R.id.spinner3);
        spinner4 = (Spinner) v.findViewById(R.id.spinner4);
        
        int id_array = this.getResources().getIdentifier(hero.getClase()+"_pasivasEn", "array", this.getActivity().getPackageName());
        pasivas = this.getResources().getStringArray(id_array);
		
		spinner1.setAdapter(new MyCustomAdapter(this.getActivity(), R.layout.fila_pasiva, pasivas));
		spinner2.setAdapter(new MyCustomAdapter(this.getActivity(), R.layout.fila_pasiva, pasivas));
		spinner3.setAdapter(new MyCustomAdapter(this.getActivity(), R.layout.fila_pasiva, pasivas));
        spinner4.setAdapter(new MyCustomAdapter(this.getActivity(), R.layout.fila_pasiva, pasivas));
        
        spinner1.setSelection(hero.getPasive(0));
        spinner2.setSelection(hero.getPasive(1));
        spinner3.setSelection(hero.getPasive(2));
        spinner3.setSelection(hero.getPasive(3));

        int id = hero.getPasive(3);

        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        boolean usePasives = settings.getBoolean("usepasives", false);

        Switch switch1 = (Switch)v.findViewById(R.id.switch1);
        switch1.setChecked(usePasives);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("usepasives", b);
                editor.commit();
            }
        });
        
        return v;
    }
	
	@Override
	public void onPause(){
		super.onPause();
		guardarPasivas();
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
	
	public void guardarPasivas() {
		int pas1 = spinner1.getSelectedItemPosition();
		int pas2 = spinner2.getSelectedItemPosition();
		int pas3 = spinner3.getSelectedItemPosition();
        int pas4 = spinner4.getSelectedItemPosition();
		
		if(pas1!=pas2 && pas2!=pas3){
			hero.setPasive(0, pas1);
			hero.setPasive(1, pas2);
			hero.setPasive(2, pas3);
            hero.setPasive(3, pas4);

            try {
                Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
                heroDao.update(hero);
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		else
			Toast.makeText(this.getActivity(), R.string.errorpassive, Toast.LENGTH_SHORT).show();
	}
	
	public class MyCustomAdapter extends ArrayAdapter<String> {

		public MyCustomAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getDropDownView(int position, View convertView,
		ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.fila_pasiva, parent, false);
			
			TextView label=(TextView)row.findViewById(R.id.nombre);
			label.setText(pasivas[position]);
	
			ImageView icon=(ImageView)row.findViewById(R.id.icon);
			icon.setImageResource(getActivity().getResources().getIdentifier(hero.getClase().toLowerCase()+"_passive_"+pasivas[position].replace("-",""), "drawable", getActivity().getPackageName()));

			return row;
		}
	}
}
