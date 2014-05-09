package com.olmo.d3stats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;
import com.olmo.d3stats.utils.DatabaseHelper;
import com.olmo.d3stats.utils.Formulas;
import com.olmo.d3stats.utils.Stats;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class EstadisticasFragment extends Fragment {
	private int heroeid = 0;
	private Hero heroe;

    private DatabaseHelper databaseHelper = null;
    public static final String PREFS_NAME = "D3StatsPref";
    public int ID;

    public EstadisticasFragment(){}

    public EstadisticasFragment(int heroeid){
        this.heroeid = heroeid;
    }
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

        if(heroeid==0)
            heroeid = getArguments().getInt("id", 0);

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            heroe = heroDao.queryForId(heroeid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null)
	        return null;
		
		setHasOptionsMenu(true);
		
		View v = inflater.inflate(R.layout.fragment_estadisticas, container, false);
		
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		calcularEstadisticas();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    //inflater.inflate(R.menu.estadisticas, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	    
	    /*if(fragment_estadisticas.getUrl().equals("")){
	    	MenuItem item = menu.findItem(R.id.actualizar);
	    	item.setVisible(false);
	    }*/
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			/*case R.id.info:
				mostrarInfo();
	   			return true;*/
			/*case R.id.actualizar:
	   			ObtenerHeroeTask2 oh = new ObtenerHeroeTask2(fragment_estadisticas,this.getActivity());
	   			oh.execute();
	   			return true;*/
	   		default:            
	   			return super.onOptionsItemSelected(item);    
		}
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
	
	private void calcularEstadisticas(){
        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            heroe = heroDao.queryForId(heroeid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        boolean usePasives = settings.getBoolean("usepasives", false);
        float extraGemStats[] = new float[Stats.NUM_STATS];
        float[] vars = Formulas.sumStats(heroe, null, usePasives, extraGemStats);
        
        // Mostrar valores en los textviews
        int[] textviews = new int[] {R.id.textFuerza, R.id.textDestreza, R.id.textInteligencia, R.id.textVitalidad, R.id.textArmadura, R.id.textHP,
        		R.id.textDPS, R.id.textElementalDPS, R.id.textEliteDPS, R.id.textPCritico, R.id.textDCritico, R.id.textVelocidad, R.id.textRegVida, R.id.textVidaGolpe, R.id.textRoboVida, R.id.textHMagico, R.id.textHOro,
        		R.id.textRFisica, R.id.textRFrio, R.id.textRFuego, R.id.textRRayos, R.id.textRVeneno, R.id.textRArcana};
        
        
        for(int i=0; i<textviews.length; i++) {
        	TextView v = (TextView) this.getActivity().findViewById(textviews[i]);
        	v.setText(new DecimalFormat("###,###.##").format(vars[i]));
        }
        
        /*for(int i=0; i<textviews2.length; i++) {
        	TextView v = (TextView) this.getActivity().findViewById(textviews2[i]);
        	v.setText(new DecimalFormat("#.##").format(vars2[i]));
            if(vars[i]>vars2[i]) v.setTextColor(Color.RED);
            else if(vars2[i]>vars[i]) v.setTextColor(Color.GREEN);
        }*/
        
	}

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
