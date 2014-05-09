package com.olmo.d3stats.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.olmo.d3stats.R;
import com.olmo.d3stats.models.Item;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ItemsListAdapter extends BaseAdapter {
    private Context context;
    private Item[] items;
    //private ArrayList<String> statsArray = new ArrayList<String>();
    private String[] statsArray;

    public ItemsListAdapter(Context context, Item[] items){
        this.context = context;
        this.items = items;

        statsArray = context.getResources().getStringArray(R.array.StatsArray);
    }

    public void setItems(Item[] items){
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderItem viewHolder;

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.list_item, null);

            viewHolder = new ViewHolderItem();

            viewHolder.textName = (TextView) view.findViewById(R.id.textView);
            viewHolder.imageBackground = (ImageView) view.findViewById(R.id.imageBackground);
            viewHolder.imageItem = (ImageView) view.findViewById(R.id.imageView);
            viewHolder.linearStats = (LinearLayout) view.findViewById(R.id.stats);

            viewHolder.textStats[0] = (TextView) view.findViewById(R.id.stat1);
            viewHolder.textStats[1] = (TextView) view.findViewById(R.id.stat2);
            viewHolder.textStats[2] = (TextView) view.findViewById(R.id.stat3);
            viewHolder.textStats[3] = (TextView) view.findViewById(R.id.stat4);
            viewHolder.textStats[4] = (TextView) view.findViewById(R.id.stat5);
            viewHolder.textStats[5] = (TextView) view.findViewById(R.id.stat6);
            viewHolder.textStats[6] = (TextView) view.findViewById(R.id.stat7);
            viewHolder.textStats[7] = (TextView) view.findViewById(R.id.stat8);
            viewHolder.textStats[8] = (TextView) view.findViewById(R.id.stat9);

            viewHolder.sockets[0] = (ImageView) view.findViewById(R.id.imageSocket);
            viewHolder.sockets[1] = (ImageView) view.findViewById(R.id.imageSocket2);
            viewHolder.sockets[2] = (ImageView) view.findViewById(R.id.imageSocket3);
            viewHolder.gems[0] = (ImageView) view.findViewById(R.id.imageGem);
            viewHolder.gems[1] = (ImageView) view.findViewById(R.id.imageGem2);
            viewHolder.gems[2] = (ImageView) view.findViewById(R.id.imageGem3);

            viewHolder.imageTextGems[0] = (ImageView) view.findViewById(R.id.imageTextGem1);
            viewHolder.imageTextGems[1] = (ImageView) view.findViewById(R.id.imageTextGem2);
            viewHolder.imageTextGems[2] = (ImageView) view.findViewById(R.id.imageTextGem3);
            viewHolder.linearGems[0] = (LinearLayout) view.findViewById(R.id.linearGem1);
            viewHolder.linearGems[1] = (LinearLayout) view.findViewById(R.id.linearGem2);
            viewHolder.linearGems[2] = (LinearLayout) view.findViewById(R.id.linearGem3);
            viewHolder.textGems[0] = (TextView) view.findViewById(R.id.textGem1);
            viewHolder.textGems[1] = (TextView) view.findViewById(R.id.textGem2);
            viewHolder.textGems[2] = (TextView) view.findViewById(R.id.textGem3);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderItem) view.getTag();
        }

        DecimalFormat df = new DecimalFormat("###,###.##");


        if(i!=13) {
            if(items[i].getName()!=null) {
                viewHolder.textName.setText(items[i].getName());
                viewHolder.imageBackground.setVisibility(View.VISIBLE);

                if (i == 5 || i == 8 || i == 9 || i == 10)
                    viewHolder.imageBackground.setImageResource(context.getResources().getIdentifier(items[i].getColor() + "_small", "drawable", context.getPackageName()));
                else
                    viewHolder.imageBackground.setImageResource(context.getResources().getIdentifier(items[i].getColor(), "drawable", context.getPackageName()));

                viewHolder.imageItem.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage("http://media.blizzard.com/d3/icons/items/large/" + items[i].getIcon() + ".png", viewHolder.imageItem);


                int cont = 0;
                for (int j = 0; j < 3; j++) {
                    if (items[i].gems[j] != null) {
                        cont++;
                        viewHolder.sockets[j].setVisibility(View.VISIBLE);
                        viewHolder.gems[j].setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage("http://media.blizzard.com/d3/icons/items/small/" + items[i].gems[j].getIcon() + ".png", viewHolder.gems[j]);
                        viewHolder.linearGems[j].setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage("http://media.blizzard.com/d3/icons/items/small/" + items[i].gems[j].getIcon() + ".png", viewHolder.imageTextGems[j]);

                        if (items[i].gems[j].getType() == 2 || items[i].gems[j].getType() == 10 || items[i].gems[j].getType() == 12)
                            viewHolder.textGems[j].setText(String.format(statsArray[items[i].gems[j].getType()], items[i].gems[j].getValue()));
                        else
                            viewHolder.textGems[j].setText(String.format(statsArray[items[i].gems[j].getType()], df.format(items[i].gems[j].getValue())));
                    } else {
                        viewHolder.sockets[j].setVisibility(View.INVISIBLE);
                        viewHolder.gems[j].setVisibility(View.INVISIBLE);
                        viewHolder.linearGems[j].setVisibility(View.GONE);
                    }
                }

                float d = context.getResources().getDisplayMetrics().density;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)(100 * d), RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (cont == 1 && (i == 8 || i == 9 || i == 10)) {
                    lp.setMargins(0, (int)(18 * d), 0, 0);
                    viewHolder.sockets[0].setLayoutParams(lp);
                } else if (cont == 1) {
                    lp.setMargins(0, (int)(55 * d), 0, 0);
                    viewHolder.sockets[0].setLayoutParams(lp);
                } else if (cont == 2) {
                    lp.setMargins(0, (int)(30 * d), 0, 0);
                    viewHolder.sockets[0].setLayoutParams(lp);
                } else if (cont == 3) {
                    lp.setMargins(0, (int)(18 * d), 0, 0);
                    viewHolder.sockets[0].setLayoutParams(lp);
                }

            }
            else{
                viewHolder.textName.setText("");
                for (int j = 0; j < 3; j++) {
                    viewHolder.sockets[j].setVisibility(View.INVISIBLE);
                    viewHolder.gems[j].setVisibility(View.INVISIBLE);
                    viewHolder.linearGems[j].setVisibility(View.GONE);
                }
                viewHolder.imageItem.setVisibility(View.INVISIBLE);
                viewHolder.imageBackground.setVisibility(View.INVISIBLE);
            }
        }
        else{
            viewHolder.textName.setText("Set Properties");
            viewHolder.imageBackground.setVisibility(View.VISIBLE);
            viewHolder.imageItem.setVisibility(View.INVISIBLE);
            viewHolder.imageBackground.setImageResource(context.getResources().getIdentifier("green", "drawable", context.getPackageName()));
            for (int j = 0; j < 3; j++) {
                viewHolder.sockets[j].setVisibility(View.INVISIBLE);
                viewHolder.gems[j].setVisibility(View.INVISIBLE);
                viewHolder.linearGems[j].setVisibility(View.GONE);
            }

            //mImageFetcher.loadImage("http://media.blizzard.com/d3/icons/items/large/" + items[i].getIcon() + ".png", viewHolder.imageItem);
        }
        //viewHolder.linearStats.removeAllViews();
        int cont=0;

        if(items[i].getName()!=null) {
            for (int j = 0; j < items[i].stats.length; j++) {
                if (items[i].stats[j] > 0) {
                    viewHolder.textStats[cont].setVisibility(View.VISIBLE);
                    if (j == 2 || j == 10 || j == 12)
                        viewHolder.textStats[cont].setText(String.format(statsArray[j], items[i].stats[j]));
                    else
                        viewHolder.textStats[cont].setText(String.format(statsArray[j], df.format(items[i].stats[j])));
                    cont++;
                }
            }

            if (cont < 9) {
                for (int j = cont; j < 9; j++)
                    viewHolder.textStats[j].setVisibility(View.GONE);
            }
        }
        else{
            for (int j = 0; j < 9; j++)
                viewHolder.textStats[j].setVisibility(View.GONE);
        }


        return view;
    }

    static class ViewHolderItem {
        TextView textName;
        ImageView imageBackground;
        ImageView imageItem;
        LinearLayout linearStats;
        TextView textStats[] = new TextView[9];
        ImageView sockets[] = new ImageView[3];
        ImageView gems[] = new ImageView[3];
        LinearLayout linearGems[] = new LinearLayout[3];
        ImageView imageTextGems[] = new ImageView[3];
        TextView textGems[] = new TextView[3];
    }
}
