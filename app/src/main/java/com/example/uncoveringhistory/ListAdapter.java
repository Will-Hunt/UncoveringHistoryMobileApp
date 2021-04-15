package com.example.uncoveringhistory;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter {

    private Activity mContext;
    List<HistoricalSite> historicalSiteList;

    public ListAdapter(Activity mContext, List<HistoricalSite> historicalSiteList){
        super(mContext,R.layout.list_item,historicalSiteList);
        this.mContext = mContext;
        this.historicalSiteList = historicalSiteList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("UncoveringHistory", "getView: ");
        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.list_item,null,true);

        TextView tv_name = listItemView.findViewById(R.id.text_view_name);
        TextView tv_description = listItemView.findViewById(R.id.text_view_description);
        HistoricalSite site = historicalSiteList.get(position);

        tv_name.setText(site.getName());
        tv_description.setText(site.getDescription());

        return listItemView;
    }
}
