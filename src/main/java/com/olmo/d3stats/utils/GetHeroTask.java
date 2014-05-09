package com.olmo.d3stats.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.olmo.d3stats.HeroesListFragment;
import com.olmo.d3stats.MainActivity;
import com.olmo.d3stats.R;
import com.olmo.d3stats.models.Gem;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;

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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GetHeroTask extends AsyncTask<Void, Void, Boolean> {
    private final String LOG_TAG = getClass().getSimpleName();
	private Context context;
	private ProgressDialog prodialog;
	//private Hero hero;
    private ArrayList<Hero> heroes;
	private boolean nuevo = false;
	//private HeroeDB hdb;
    private DatabaseHelper databaseHelper = null;
    private Dao<Hero, Integer> heroDao;
    private Dao<Item, Integer> itemDao;

	public GetHeroTask(ArrayList<Hero> heroes, boolean nuevo, Context context){
		this.context = context;
		this.heroes = heroes;
        this.nuevo = nuevo;
		prodialog = new ProgressDialog(context);
		prodialog.setMessage(context.getResources().getString(R.string.gethero));
		prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		prodialog.setCancelable(false);

        try {
            heroDao = getHelper().getHeroDao();
            itemDao = getHelper().getItemDao();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Database exception", e);
        }

		//hdb = new HeroeDB(context);
	}
	
	protected void onPreExecute(){
		prodialog.show();

        if(nuevo) {

            try {
                for(int j=0; j<heroes.size(); j++) {

                    Hero hero2 = heroDao.queryForId(heroes.get(j).getId());
                    if (hero2 == null) {
                        heroDao.create(heroes.get(j));

                        for (int i = 0; i < 14; i++) {
                            Item item = new Item();
                            item.setType(i);
                            item.setHero(heroes.get(j));
                            itemDao.create(item);
                        }

                        long numRows = heroDao.countOf();
                        heroes.get(j).setPosition((int)numRows);
                        heroDao.update(heroes.get(j));
                        //hero = heroDao.queryForId(hero.getId());

                    }
                }

            } catch (SQLException e) {
                Log.e(LOG_TAG, "Database exception", e);
            }
        }

        try {
            for(int j=0; j<heroes.size(); j++)
                heroDao.refresh(heroes.get(j));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Boolean doInBackground(Void...voids) {
    	ArrayList<String> itemNames = new ArrayList<String>();
    	itemNames.add("head");
    	itemNames.add("shoulders");
    	itemNames.add("torso");
    	itemNames.add("hands");
    	itemNames.add("bracers");
    	itemNames.add("waist");
    	itemNames.add("legs");
    	itemNames.add("feet");
    	itemNames.add("neck");
    	itemNames.add("leftFinger");
    	itemNames.add("rightFinger");
    	itemNames.add("mainHand");
    	itemNames.add("offHand");

        for(int k=0; k<heroes.size(); k++) {
            HashMap<String, Integer> sets = new HashMap<String, Integer>();
            HashMap<String, JSONObject> setsValues = new HashMap<String, JSONObject>();
            int setDiscount = 0;
            String resultado = null;

            String HERO_API_URL = "/api/d3/profile/%s/hero/%d";
            String u = String.format(HERO_API_URL, heroes.get(k).getProfile(), heroes.get(k).getId());
            String url = heroes.get(k).getServer() + u;

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.setHeader("content-type", "application/json");
            ResponseHandler handler = new BasicResponseHandler();

            try {
                resultado = httpclient.execute(request, handler).toString();
                JSONObject jsObject = new JSONObject(resultado);

                heroes.get(k).setLevel(jsObject.getInt("level"));
                heroes.get(k).setParagonLevel(jsObject.getInt("paragonLevel"));

                JSONObject jsObjectItems = jsObject.getJSONObject("items");

                ArrayList<Item> items = new ArrayList<Item>(heroes.get(k).items);

                for (int j = 0; j < items.get(13).stats.length; j++)
                    items.get(13).stats[j] = 0;

                int i = 0;

                for (Item item : items) {
                    if (item.getType() == 13)
                        continue;

                    for (int j = 0; j < item.stats.length; j++)
                        item.stats[j] = 0;
                    for(int j=0; j<3; j++)
                        item.gems[j] = null;

                    JSONObject itemObj = jsObjectItems.optJSONObject(itemNames.get(i));
                    if (itemObj != null) {
                        String dir_item = itemObj.getString("tooltipParams");

                        request = new HttpGet(heroes.get(k).getServer() + "/api/d3/data/" + dir_item);
                        request.setHeader("content-type", "application/json");

                        String resultado2 = httpclient.execute(request, handler).toString();

                        JSONObject jsObjectItem = new JSONObject(resultado2);

                        String idItem = jsObjectItem.optString("id");
                        if(idItem.equals("Unique_Ring_107_x1")){
                            setDiscount = 1;
                        }

                        item.setName(jsObjectItem.optString("name"));
                        item.setColor(jsObjectItem.optString("displayColor"));
                        item.setIcon(jsObjectItem.optString("icon"));
                        item.setTypeName(jsObjectItem.optJSONObject("type").optString("id"));

                        //Da�o del arma
                        if (i == 11 || i == 12) {
                            if (jsObjectItem.optJSONObject("minDamage") != null) {
                                item.stats[0] = (float) jsObjectItem.optJSONObject("minDamage").optDouble("min");
                            }
                            if (jsObjectItem.optJSONObject("maxDamage") != null) {
                                item.stats[1] = (float) jsObjectItem.optJSONObject("maxDamage").optDouble("min");
                            }
                            if (jsObjectItem.optJSONObject("attacksPerSecond") != null) {
                                item.stats[2] = (float) jsObjectItem.optJSONObject("attacksPerSecond").optDouble("min");
                            }
                        }

                        JSONObject jsObjectAttr = jsObjectItem.getJSONObject("attributesRaw");
                        getAttributes(item, jsObjectAttr);

                        //Gemas
                        JSONArray jsObjectGems = jsObjectItem.getJSONArray("gems");
                        for (int j = 0; j < jsObjectGems.length(); j++) {
                            JSONObject jsObjectGemsAttr = jsObjectGems.getJSONObject(j).getJSONObject("attributesRaw");

                            Iterator<?> keys2 = jsObjectGemsAttr.keys();

                            while (keys2.hasNext()) {
                                String key = (String) keys2.next();

                                if (Stats.properties.containsKey(key)) {
                                    int id = Stats.properties.get(key);

                                    double valor = jsObjectGemsAttr.getJSONObject(key).optDouble("min");
                                    float valor_final = 0;
                                    if (id == 9 || id == 11 || id == 21 || id == 22 || id == 23)
                                        valor_final = (int) Math.round(valor * 100);
                                    else if (id == 10 || id == 12)
                                        valor_final = (float) valor * 100;
                                    else
                                        valor_final = (int) Math.round(valor);

                                    //item.stats[id] += valor_final;

                                    item.gems[j] = new Gem(id, valor_final, jsObjectGems.getJSONObject(j).getJSONObject("item").optString("icon"));

                                }
                            }
                        }

                        //Set
                        JSONObject jsObjectSet = jsObjectItem.optJSONObject("set");
                        if (jsObjectSet != null) {
                            if (sets.containsKey(jsObjectSet.getString("slug"))) {
                                sets.put(jsObjectSet.getString("slug"), sets.get(jsObjectSet.getString("slug")) + 1);

                                /*JSONArray jsrequired = jsObjectSet.getJSONArray("ranks");
                                for (int j = 0; j < jsrequired.length(); j++) {
                                    if (jsrequired.getJSONObject(j).getInt("required") == sets.get(jsObjectSet.getString("slug"))) {
                                        JSONObject attr = jsrequired.getJSONObject(j).getJSONObject("attributesRaw");
                                        getAttributes(items.get(13), attr);
                                    }
                                }*/
                            } else {
                                sets.put(jsObjectSet.getString("slug"), 1);

                                JSONArray jsrequired = jsObjectSet.getJSONArray("ranks");
                                for (int j = 0; j < jsrequired.length(); j++) {
                                    setsValues.put(jsObjectSet.getString("slug")+"_"+
                                            jsrequired.getJSONObject(j).getInt("required"),
                                            jsrequired.getJSONObject(j).getJSONObject("attributesRaw"));
                                }
                            }
                        }
                    }

                    try {
                        itemDao.update(item);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    prodialog.incrementProgressBy(7/heroes.size());
                    i++;
                }

                for (Map.Entry<String, Integer> entry : sets.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();

                    if(value>1)
                        value += setDiscount;

                    for(int j=value; j>1; j--){
                        if(setsValues.get(key+"_"+j)!=null){
                            getAttributes(items.get(13), setsValues.get(key+"_"+j));
                        }
                    }
                }

                items.get(13).setName("");
                try {
                    itemDao.update(items.get(13));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //Pasivas
                int id_array = context.getResources().getIdentifier(heroes.get(k).getClase() + "_pasivasEn", "array", context.getPackageName());
                String[] pasivas = context.getResources().getStringArray(id_array);

                JSONArray jsObjectPass = jsObject.getJSONObject("skills").getJSONArray("passive");
                for (i = 0; i < jsObjectPass.length(); i++) {
                    JSONObject passive = jsObjectPass.getJSONObject(i).optJSONObject("skill");
                    if (passive != null) {
                        String nombre = passive.getString("slug").replace("-", "");
                        for (int j = 0; j < pasivas.length; j++) {
                            if (pasivas[j].equals(nombre)) {
                                heroes.get(k).setPasive(i, j);
                                break;
                            }
                        }
                    }

                }

                try {
                    heroDao.update(heroes.get(k));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                prodialog.incrementProgressBy(9/heroes.size());


            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    prodialog.dismiss();
                } catch (Exception ex) {

                }
                return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                try {
                    prodialog.dismiss();
                } catch (Exception ex) {

                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    prodialog.dismiss();
                } catch (Exception ex) {

                }
                return false;
            }

            httpclient.getConnectionManager().shutdown();
        }
    	
    	return true;
    }

    protected void onPostExecute(Boolean result) {
    	try{
			prodialog.dismiss();
		}
		catch(Exception ex){
			
		}
    	
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
    		
    		if(nuevo){
    			/*hdb.open();
    	        hdb.deleteHeroe(hero.getId());
    	        hdb.close();*/
    		}
    		
    	}
    	else{
            if(heroes.size()==1) {
                ((HeroesListFragment.HeroesListListener) context).onNewHero();
                ((HeroesListFragment.HeroesListListener) context).onChangeHero(heroes.get(0).getId());
            }
            else{
                ((HeroesListFragment.HeroesListListener) context).onNewHero();
                ((MainActivity) context).openPane();
            }
	    }

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
    
    /*void invocarMetodo(String nombre, int v){
    	java.lang.reflect.Method method = null;
    	 Class partypes[] = new Class[1];
         partypes[0] = Integer.TYPE;
         Object arglist[] = new Object[1];
         arglist[0] = new Integer(v);
    	
    	try {
    	  method = hero.getClass().getMethod(nombre, partypes);
    	} catch (SecurityException e) {
    	  // ...
    	} catch (NoSuchMethodException e) {
    	  // ...
    	}
    	
    	try {
    		method.invoke(hero, arglist);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
    }*/

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void getAttributes(Item item, JSONObject jsObjectAttr) throws JSONException {
        Iterator<?> keys = jsObjectAttr.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();

            if(Stats.properties.containsKey(key)){
                int id = Stats.properties.get(key);

                double valor = jsObjectAttr.getJSONObject(key).optDouble("min");
                float valor_final = 0;
                if(id==9 || id==11 || id==21 || id==22 || id==23 || (id>=24 && id<=30))
                    valor_final = (int) Math.round(valor * 100);
                else if(id==10 || id==12)
                    valor_final = (float)valor*100;
                else if(id==1){
                    if(jsObjectAttr.optJSONObject("Damage_Min#Physical")!=null){
                        valor_final = (float)jsObjectAttr.optJSONObject("Damage_Min#Physical").optDouble("min") + (float)valor;
                    }
                }
                else if(id==-1){
                    String pr = key.replace("Delta", "Min");
                    if(jsObjectAttr.optJSONObject(pr)!=null){
                        valor_final = (float)jsObjectAttr.optJSONObject(pr).optDouble("min") + (float)valor;
                        id = 1;
                    }
                }
                else
                    valor_final = (int) Math.round(valor);

                item.stats[id] += valor_final;
            }
        }
    }
}
