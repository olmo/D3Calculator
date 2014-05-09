package com.olmo.d3stats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.olmo.d3stats.models.Gem;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;
import com.olmo.d3stats.utils.DatabaseHelper;
import com.olmo.d3stats.utils.Formulas;
import com.olmo.d3stats.utils.Stats;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class ItemActivity extends ActionBarActivity {
    private Hero hero;
    private Item item;
    private Item itemNew;
    private DatabaseHelper databaseHelper = null;
    private TextView mtextNewDPS;
    private TextView mtextNewEDPS;
    private TextView mtextNewEliteDPS;
    private TextView mtextDPSPercent;
    private TextView mtextEDPSPercent;
    private TextView mtextEliteDPSPercent;
    private static final String IMAGE_CACHE_DIR = "images";
    private float dps;
    private float elementalDPS;
    private float eliteDps;
    ArrayList<EditText> texts;
    private boolean usePasives;
    public static final String PREFS_NAME = "D3StatsPref";

    ArrayList<EditText> textsExtraGems = new ArrayList<EditText>();
    ArrayList<Integer> numExtraGems = new ArrayList<Integer>();
    private String[] stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Bundle b = getIntent().getExtras();
        int heroid = b.getInt("heroid");
        int itemId = b.getInt("itemid");

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            hero = heroDao.queryForId(heroid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        stats = getResources().getStringArray(R.array.Stats);
        Object[] itemsO = hero.items.toArray();
        Item[] items = Arrays.asList(itemsO).toArray(new Item[itemsO.length]);
        item = items[itemId];
        itemNew = item;

        ActionBar bar = getSupportActionBar();
        bar.setIcon(getResources().getIdentifier(hero.getClase().toLowerCase() + "_" + hero.getGender(), "drawable", this.getPackageName()));
        bar.setTitle(hero.getName() + " " + hero.getLevel() + " (" + hero.getParagonLevel() + ")");
        bar.setSubtitle(this.getResources().getIdentifier(hero.getClase(), "string", this.getPackageName()));
        bar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        usePasives = settings.getBoolean("usepasives", false);

        TextView textName = (TextView)this.findViewById(R.id.textName);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        textName.setText(item.getName());

        ImageLoader.getInstance().displayImage("http://media.blizzard.com/d3/icons/items/large/" + item.getIcon() + ".png", imageView);


        TextView textDPS = (TextView)this.findViewById(R.id.textDPS);
        TextView textEDPS = (TextView)this.findViewById(R.id.textEDPS);
        TextView textEliteDPS = (TextView)this.findViewById(R.id.textEliteDPS);
        mtextNewDPS = (TextView)this.findViewById(R.id.textNewDPS);
        mtextNewEDPS = (TextView)this.findViewById(R.id.textNewEDPS);
        mtextNewEliteDPS = (TextView)this.findViewById(R.id.textNewEliteDPS);
        mtextDPSPercent = (TextView)this.findViewById(R.id.textDPSPercent);
        mtextEDPSPercent = (TextView)this.findViewById(R.id.textEDPSPercent);
        mtextEliteDPSPercent = (TextView)this.findViewById(R.id.textEliteDPSPercent);

        float extraGemStats[] = new float[Stats.NUM_STATS];
        dps = Formulas.calculateDPS(hero, null, 0, usePasives, extraGemStats);
        elementalDPS = Formulas.calculateDPS(hero, null, 1, usePasives, extraGemStats);
        eliteDps = Formulas.calculateDPS(hero, null, 2, usePasives, extraGemStats);
        textDPS.setText(new DecimalFormat("###,###.##").format(dps));
        textEDPS.setText(new DecimalFormat("###,###.##").format(elementalDPS));
        textEliteDPS.setText(new DecimalFormat("###,###.##").format(eliteDps));

        final TableLayout v = (TableLayout)this.findViewById(R.id.tabla);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        texts = new ArrayList<EditText>();
        final MyCursorAdapter adapter = new MyCursorAdapter(this, stats);

        for(int i=0; i<item.stats.length; i++){
            if(((i==0 || i==1) && itemId>7) || (i==2 && itemId>10) || (i==12 && itemId!=11) || (i>2 && i!=12)){
                if(item.stats[i]!=0){
                    v.addView(crearFila(i, -1),new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    adapter.deleteItem(i);
                }
            }
            else{
                adapter.deleteItem(i);
            }
        }

        final TableLayout tableGems = (TableLayout)this.findViewById(R.id.tableGems);

        boolean hasGems = false;
        for(int i=0; i<3; i++)
            if(item.gems[i]!=null){
                hasGems = true;
                break;
            }
        final MyCursorAdapter adapterGems = new MyCursorAdapter(this, stats);
        if(hasGems) {
            for (int i = 0; i < item.gems.length; i++) {
                if (item.gems[i] != null) {
                    tableGems.addView(crearFila(item.gems[i].getType(), i), new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        }



        LinearLayout scroll = (LinearLayout)findViewById(R.id.linearStats);
        LinearLayout lin = new LinearLayout(this);
        //LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER;
        params2.topMargin = 25;

        lin.setLayoutParams(params2);

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.Stats, R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        final Spinner spin = new Spinner(this);
        adapter.notifyDataSetChanged();
        spin.setAdapter(adapter);
        lin.addView(spin);

        Button but = new Button(this);
        but.setText(R.string.add);

        //final int pos = itemId;
        but.setOnClickListener(new View.OnClickListener() {
             public void onClick(View vi) {
                 int id = (int)spin.getSelectedItemId();
                 v.addView(crearFila(id, -1),new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                 adapter.deleteItem(id);
                 adapter.notifyDataSetChanged();
             }
         });

        lin.addView(but);
        scroll.addView(lin);



        LinearLayout linearGems = (LinearLayout)findViewById(R.id.linearGems);
        LinearLayout lin2 = new LinearLayout(this);
        lin2.setLayoutParams(params2);

        final Spinner spin2 = new Spinner(this);
        adapterGems.notifyDataSetChanged();
        spin2.setAdapter(adapterGems);
        lin2.addView(spin2);

        Button but2 = new Button(this);
        but2.setText(R.string.add);

        //final int pos = itemId;
        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vi) {
                int id = (int)spin2.getSelectedItemId();
                tableGems.addView(crearFila(id, 10),new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });

        lin2.addView(but2);
        linearGems.addView(lin2);
    }

    @Override
    public void onPause(){
        super.onPause();

        try{
            texts.get(0).requestFocus();
            texts.get(1).requestFocus();
        } catch(Exception e){

        }

    }


    public class MyCursorAdapter extends BaseAdapter implements SpinnerAdapter {
	    private Activity activity;
	    private ArrayList<Prop> map = new ArrayList<Prop>();

	    public MyCursorAdapter(Activity activity, String[] stats){
	        this.activity = activity;

	        for(int i=0; i<stats.length; i++){
	        	Prop prop = new Prop(i, stats[i]);
	        	map.add(prop);
	        }
	    }

	    public int getCount() {
	        return map.size();
	    }

	    public Object getItem(int position) {
	        return map.get(position);
	    }

	    public long getItemId(int position) {
	        return map.get(position).getId();
	    }

	    public void deleteItem(int id){
	    	for(int i=0; i<map.size(); i++){
	    		if(map.get(i).getId()==id){
	    			map.remove(i);
	    			break;
	    		}
	    	}
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = activity.getLayoutInflater();
	        View spinView = inflater.inflate(R.layout.spinner_propiedad, null);
	        TextView t1 = (TextView) spinView.findViewById(R.id.text1);
	        t1.setText(map.get(position).getCadena());

	        return spinView;
		}

	}

	public class Prop {
		public int id;
		public String cadena;

		Prop(int id, String cadena){
			this.id = id;
			this.cadena = cadena;
		}

		public int getId(){
			return this.id;
		}

		public String getCadena(){
			return this.cadena;
		}
	}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private TableRow crearFila(int i, final int gem){
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        TableRow.LayoutParams paramsLabel = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT, 2);
        TableRow.LayoutParams paramsText = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT, 1);

        //tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView label = new TextView(this);
        label.setText(stats[i]);
        label.setLayoutParams(paramsLabel);
        //label.setHeight(TableRow.LayoutParams.WRAP_CONTENT);

        final EditText et = new EditText(this);
        final EditText et2 = new EditText(this);

        texts.add(et);
        texts.add(et2);
        et.setLayoutParams(paramsText);
        et2.setLayoutParams(paramsText);

        if(i==0 || i==1 || i==2 || i==10 || i==12 || i==21){
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        else{
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            et2.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if(gem==-1) {
            et.setText(new DecimalFormat("#.##").format(item.stats[i]));
            et2.setText(new DecimalFormat("#.##").format(item.stats[i]));
        }
        else if(gem<3){
            et.setText(new DecimalFormat("#.##").format(item.gems[gem].getValue()));
            et2.setText(new DecimalFormat("#.##").format(item.gems[gem].getValue()));
        }
        else{
            et.setText(new DecimalFormat("#.##").format(0));
            et2.setText(new DecimalFormat("#.##").format(0));
            textsExtraGems.add(et2);
            numExtraGems.add(i);
        }

        et.setSelectAllOnFocus(true);
        et2.setSelectAllOnFocus(true);

        //final int pos = position;
        final int num = i;

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    float aux1;
                    if(et.getText().toString().equals(""))
                        aux1=0;
                    else{
                        String cad1 = et.getText().toString().replace(',', '.');
                        aux1 = Float.valueOf(cad1);
                    }

                    if(gem==-1) {
                        if (aux1 != item.stats[num]) {
                            item.stats[num] = aux1;

                            try {
                                Dao<Item, Integer> itemDao = getHelper().getItemDao();
                                itemDao.update(item);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    /*else{
                        if (aux1 != item.gems[gem].getValue()) {
                            item.gems[gem].setValue(aux1);

                            try {
                                Dao<Item, Integer> itemDao = getHelper().getItemDao();
                                itemDao.update(item);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }*/
                }
            }
        });

        et2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    float aux2;

                    if(et2.getText().toString().equals(""))
                        aux2=0;
                    else{
                        String cad2 = et2.getText().toString().replace(',', '.');
                        aux2 = Float.valueOf(cad2);
                    }

                    if((gem==-1 && aux2 != itemNew.stats[num])){
                        itemNew.stats[num] = aux2;

                        float aux3;
                        float extraGemStats[] = new float[Stats.NUM_STATS];
                        for(int j=0; j<textsExtraGems.size(); j++){
                            if(textsExtraGems.get(j).getText().toString().equals(""))
                                aux3=0;
                            else{
                                String cad2 = textsExtraGems.get(j).getText().toString().replace(',', '.');
                                aux3 = Float.valueOf(cad2);
                            }

                            extraGemStats[numExtraGems.get(j)] += aux3;
                        }

                        float newDps = Formulas.calculateDPS(hero, itemNew, 0, usePasives, extraGemStats);
                        float newEdps = Formulas.calculateDPS(hero, itemNew, 1, usePasives, extraGemStats);
                        float newEliteDps = Formulas.calculateDPS(hero, itemNew, 2, usePasives, extraGemStats);

                        mtextNewDPS.setText(new DecimalFormat("###,###.##").format(newDps));
                        mtextNewEDPS.setText(new DecimalFormat("###,###.##").format(newEdps));
                        mtextNewEliteDPS.setText(new DecimalFormat("###,###.##").format(newEliteDps));

                        float dpsPercent = newDps*100/dps - 100;
                        float edpsPercent = newEdps*100/ elementalDPS - 100;
                        float eliteDpsPercent = newEliteDps*100/ eliteDps - 100;

                        mtextDPSPercent.setText(new DecimalFormat("###,###.##").format(dpsPercent));
                        mtextEDPSPercent.setText(new DecimalFormat("###,###.##").format(edpsPercent));
                        mtextEliteDPSPercent.setText(new DecimalFormat("###,###.##").format(eliteDpsPercent));
                    }
                    else if(gem!=-1){
                        if(gem<3 && aux2 != item.gems[gem].getValue() ) {
                            itemNew.gems[gem].setValue(aux2);
                        }

                        float aux3;
                        float extraGemStats[] = new float[Stats.NUM_STATS];
                        for(int j=0; j<textsExtraGems.size(); j++){
                            if(textsExtraGems.get(j).getText().toString().equals(""))
                                aux3=0;
                            else{
                                String cad2 = textsExtraGems.get(j).getText().toString().replace(',', '.');
                                aux3 = Float.valueOf(cad2);
                            }

                            extraGemStats[numExtraGems.get(j)] += aux3;
                        }

                        float newDps = Formulas.calculateDPS(hero, itemNew, 0, usePasives, extraGemStats);
                        float newEdps = Formulas.calculateDPS(hero, itemNew, 1, usePasives, extraGemStats);
                        float newEliteDps = Formulas.calculateDPS(hero, itemNew, 2, usePasives, extraGemStats);

                        mtextNewDPS.setText(new DecimalFormat("###,###.##").format(newDps));
                        mtextNewEDPS.setText(new DecimalFormat("###,###.##").format(newEdps));
                        mtextNewEliteDPS.setText(new DecimalFormat("###,###.##").format(newEliteDps));

                        float dpsPercent = newDps*100/dps - 100;
                        float edpsPercent = newEdps*100/ elementalDPS - 100;
                        float eliteDpsPercent = newEliteDps*100/ eliteDps - 100;

                        mtextDPSPercent.setText(new DecimalFormat("###,###.##").format(dpsPercent));
                        mtextEDPSPercent.setText(new DecimalFormat("###,###.##").format(edpsPercent));
                        mtextEliteDPSPercent.setText(new DecimalFormat("###,###.##").format(eliteDpsPercent));
                    }
                }
            }
        });

        tr.addView(label, params);
        tr.addView(et, params);
        tr.addView(et2, params);

        return tr;
    }

}
