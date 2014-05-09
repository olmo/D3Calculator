package com.olmo.d3stats;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.olmo.d3stats.interfaces.Communication;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.utils.GetHeroTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddHeroFragment extends Fragment implements Communication {
	private ProgressDialog dialog;
	private ProgressDialog prodialog;
	private String name;
	Context context;
	String url0;
	String url1;
	String url_item = "/d3/en/tooltip/";
	private ArrayList<Hero> heroes = new ArrayList<Hero>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null)
	        return null;
		
		View v = inflater.inflate(R.layout.fragment_add_hero, container, false);

		return v;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        View v = this.getView();

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.servidores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        SharedPreferences settings = this.getActivity().getSharedPreferences("D3Stats", 0);
        int serv = settings.getInt("servidor", 0);
        spinner.setSelection(serv);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                SharedPreferences settings = AddHeroFragment.this.getActivity().getSharedPreferences("D3Stats", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("servidor", arg2);
                editor.commit();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        Button button = (Button) v.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buscarHeroe(v);
            }
        });

        context = this.getActivity();

        prodialog = new ProgressDialog(context);
        prodialog.setMessage(context.getResources().getString(R.string.gethero));
        prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        prodialog.setCancelable(false);
    }

    @Override
    public void setTitle(){
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setIcon(R.drawable.ic_launcher);
        bar.setTitle(R.string.app_name);
        bar.setSubtitle(null);
    }

    @Override
    public int getHeroId() {
        return -1;
    }

    public void buscarHeroe(View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e){
            
        }
		
    	EditText nombre = (EditText) this.getActivity().findViewById(R.id.editText1);
        EditText num = (EditText) this.getActivity().findViewById(R.id.editText2);
    	Spinner servidor = (Spinner) this.getActivity().findViewById(R.id.spinner1);
    	
    	if(servidor.getSelectedItemPosition()== Spinner.INVALID_ROW_ID){
    		Toast.makeText(this.getActivity(), "Error: No has seleccionado una clase.", Toast.LENGTH_SHORT).show();
    	}
    	else if(nombre.getText().toString().equals("")){
    		Toast.makeText(this.getActivity(), R.string.errornamehero, Toast.LENGTH_SHORT).show();
    	}
    	else{
    		url0 = "";
    		url1 = "";
    		if(servidor.getSelectedItemPosition()==0)
    			url0 += "http://us.battle.net";
    		else if(servidor.getSelectedItemPosition()==1){
    			url0 += "http://eu.battle.net";
    		}
    		else if(servidor.getSelectedItemPosition()==2){
    			url0 += "http://kr.battle.net";
    		}
    		else if(servidor.getSelectedItemPosition()==3){
    			url0 += "http://tw.battle.net";
    		}
    		
    		name = nombre.getText().toString();
    		name = name.replace("#", "-");
    		name = name.replace(".", "-");
    		name = name.replace("_", "-");
    		name = name.replace(",", "-");
    		name = name.replace(" ", "");
            String number = num.getText().toString();
            name = name+"-"+number;
    		
    		url1 += "/api/d3/profile/"+name+"/index";
    		
    		dialog = new ProgressDialog(context);
	        dialog.setMessage(context.getResources().getString(R.string.getheroes));
	        dialog.setCancelable(false);
    		
    		new ObtenerHeroes().execute(url0,name);
    		
    		
    		
    	}
    }
	
	void crearVentana(){
        final boolean checkedItems[] = new boolean[heroes.size()];
        for(int i=0; i<checkedItems.length; i++)
            checkedItems[i] = false;
		
		ListAdapter adapter = new ArrayAdapter<Hero>(context, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, heroes){
	        public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
	            TextView tv = (TextView)v.findViewById(android.R.id.text1);
	            tv.setText(heroes.get(position).getName());

	            Drawable img = context.getResources().getDrawable(context.getResources().getIdentifier(
                        heroes.get(position).getClase().toLowerCase()+"_"+heroes.get(position).getGender(),
                        "drawable",
                        context.getPackageName()
                ));
	            img.setBounds( 0, 0, 60, 60 );
	            tv.setCompoundDrawables( img, null, null, null );

	            int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
	            tv.setCompoundDrawablePadding(dp5);

	            return v;
	        }
	    };
			    
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.eligeheroe);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
                //Hero hero = heroes.get(item);


                //Hero hero = new Hero();
		    	
		    	//GetHeroTask oh = new GetHeroTask(hero, true, context);
	   			//oh.execute();
	        }
	    });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    ArrayList<Hero> newHeroes = new ArrayList<Hero>();

                    for(int i=0; i<checkedItems.length; i++){
                        if(checkedItems[i]){
                            newHeroes.add(heroes.get(i));

                        }
                    }

                    GetHeroTask oh = new GetHeroTask(newHeroes, true, context);
                    oh.execute();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            })
        ;

        AlertDialog alertDialog = builder.create();

        ListView listView = alertDialog.getListView();
        //listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedItems[position] = !checkedItems[position];
            }
        });
        listView.setDivider(null);
        listView.setDividerHeight(-1);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                //setCheckedItems(((AlertDialog) dialog).getListView());
            }
        });


        alertDialog.show();
	}
	
	
	class ObtenerHeroes extends AsyncTask<String, Void, Boolean> {
	    private ArrayList<String> ids = new ArrayList<String>();
		private ArrayList<String> nombres = new ArrayList<String>();
		
		protected void onPreExecute(){
			dialog.show();
		}

	    protected Boolean doInBackground(String... urls) {
	    	HashMap<String, String> clases = new HashMap<String, String>();
	    	clases.put("barbarian", "B");
	    	clases.put("demon-hunter", "DH");
	    	clases.put("monk", "M");
	    	clases.put("witch-doctor", "WD");
	    	clases.put("wizard", "W");
	    	
	    	String resultado = null;
	    	HttpClient httpclient = new DefaultHttpClient();
	    	HttpGet request = new HttpGet(urls[0]+"/api/d3/profile/"+urls[1]+"/index");
	    	request.setHeader("content-type", "application/json");
	    	ResponseHandler handler = new BasicResponseHandler();
	    	
	    	try {
	    		resultado = httpclient.execute(request, handler).toString();
	    	} catch (ClientProtocolException e) {
	    		e.printStackTrace();
	    		dialog.dismiss();
				return false;
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		dialog.dismiss();
				return false;
	    	}
	    	httpclient.getConnectionManager().shutdown();
	    	heroes.clear();
	    	
	    	try {
	    		JSONObject jsObject = new JSONObject(resultado);
				JSONArray heroes_arr = jsObject.getJSONArray("heroes");
				
				for (int i=0; i<heroes_arr.length(); i++){
					JSONObject hero = heroes_arr.getJSONObject(i);
					Hero heroe = new Hero();
					if (hero == null)
						continue;
					try {
						String HERO_API_URL = "/api/d3/profile/%s/hero/%d";
						String u = String.format(HERO_API_URL, name, hero.getInt("id"));
						heroe.setServer(url0);
                        heroe.setProfile(urls[1]);
						heroe.setId(hero.getInt("id"));
						heroe.setName(hero.getString("name"));
						heroe.setLevel(hero.getInt("level"));
						heroe.setParagonLevel(hero.getInt("paragonLevel"));
						heroe.setClase(hero.getString("class").replace("-", ""));
                        heroe.setGender(hero.getInt("gender"));
                        heroe.setHardcore(hero.getBoolean("hardcore"));
                        heroe.setDead(hero.getBoolean("dead"));
						/*fragment_estadisticas.hardcore = hero.getBoolean("hardcore");
						fragment_estadisticas.dead = hero.getBoolean("dead");
						fragment_estadisticas.gender = hero.getInt("gender");*/
					} catch (JSONException e) {
						continue;
					}
					heroes.add(heroe);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				dialog.dismiss();
				return false;
			}
	    	
	    	return true;
	    }

	    protected void onPostExecute(Boolean result) {
	    	dialog.dismiss();
	    	
	    	if(!result){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    		builder.setMessage(R.string.errorgetdata)
	    		       .setCancelable(false)
	    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    		    	   public void onClick(DialogInterface dialog, int id) {
	    		                
	    		           }
	    		       });
	    		AlertDialog alert = builder.create();
	    		alert.show();
	    	}
	    	else{
		    	if(heroes.size()>0)
	    			crearVentana();
	    	}
	    }
	}
	
}
