package com.olmo.d3stats;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;
import com.olmo.d3stats.utils.DatabaseHelper;
import com.olmo.d3stats.utils.Formulas;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ParagonFragment extends Fragment {
    private int heroeid=0;
    private Hero heroe;

    private DatabaseHelper databaseHelper = null;
    ArrayList<EditText> editTexts;

    public ParagonFragment(){}
    public ParagonFragment(int heroeid){
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

        View v = inflater.inflate(R.layout.fragment_paragon, container, false);

        ArrayList<String> statsText = new ArrayList<String>();
        statsText.add("Main Stat");
        statsText.add("Critic Damage");
        statsText.add("Critic Percent");
        statsText.add("Attack Speed");

        editTexts = new ArrayList<EditText>();

        TableLayout table = (TableLayout)v.findViewById(R.id.table);

        for(int i=0; i<4; i++) {
            TableRow tr = new TableRow(this.getActivity());
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);

            //tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView label = new TextView(this.getActivity());
            label.setText(statsText.get(i));
            //label.setHeight(TableRow.LayoutParams.WRAP_CONTENT);


            EditText et = new EditText(this.getActivity());

            //texts[position].add(et);
            //texts[position].add(et2);

            if (i == 2 || i == 3) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            if(i==0)
                et.setText(new DecimalFormat("#.##").format(heroe.getParagonMainStat()));
            else if(i==1)
                et.setText(new DecimalFormat("#.##").format(heroe.getParagonCriticDamage()));
            else if(i==2)
                et.setText(new DecimalFormat("#.##").format(heroe.getParagonCriticPercent()));
            else if(i==3)
                et.setText(new DecimalFormat("#.##").format(heroe.getParagonAttackSpeed()));

            et.setSelectAllOnFocus(true);
            editTexts.add(et);

            tr.addView(label, params);
            tr.addView(et, params);

            table.addView(tr,new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveParagon();
            }
        });

        return v;
    }

    private void saveParagon(){
        float aux2;
        float values[] = new float[4];
        for(int i=0; i<4; i++){
            if(editTexts.get(i).getText().toString().equals(""))
                aux2=0;
            else{
                String cad2 = editTexts.get(i).getText().toString().replace(',', '.');
                aux2 = Float.valueOf(cad2);
            }
            values[i] = aux2;
        }


        heroe.setParagonMainStat(values[0]);
        heroe.setParagonCriticDamage(values[1]);
        heroe.setParagonCriticPercent(values[2]);
        heroe.setParagonAttackSpeed(values[3]);

        try {
            Dao<Hero, Integer> heroDao = getHelper().getHeroDao();
            heroDao.update(heroe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onPause(){
        super.onPause();

        View v=this.getActivity().getCurrentFocus();
        if(v!=null) {
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
