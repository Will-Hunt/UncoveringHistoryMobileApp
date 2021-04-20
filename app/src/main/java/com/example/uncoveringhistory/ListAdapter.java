package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ListAdapter extends ArrayAdapter {

    private final Activity context;
    StorageReference imageToShow;
    List<HistoricalSite> historicalSiteList;

    public ListAdapter(Activity context, List<HistoricalSite> historicalSiteList) {
        super(context, R.layout.list_item, historicalSiteList);
        this.context = context;
        this.historicalSiteList = historicalSiteList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"InflateParams", "ViewHolder"}) View listItemView = inflater.inflate(R.layout.list_item, null, true);

        ImageView tv_image = listItemView.findViewById(R.id.text_view_image);
        TextView tv_name = listItemView.findViewById(R.id.text_view_name);
        TextView tv_type = listItemView.findViewById(R.id.text_view_type);
        HistoricalSite site = historicalSiteList.get(position);

        try {
            File file = File.createTempFile("image", "jpg");
            String image = "gs://uncovering-history-mobile-app.appspot.com/images/" + site.getImageName();
            imageToShow = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            imageToShow.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                tv_image.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv_name.setText(site.getName());
        tv_type.setText(site.getType());

        return listItemView;
    }
}
