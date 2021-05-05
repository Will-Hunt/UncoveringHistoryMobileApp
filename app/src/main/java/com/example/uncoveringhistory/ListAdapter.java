package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListAdapter extends ArrayAdapter {
    private static final String TAG = "UncoveringHistory";

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

        CheckBox checkBox = listItemView.findViewById(R.id.list_item_checkBox);
        ImageView image = listItemView.findViewById(R.id.list_item_image);
        TextView name = listItemView.findViewById(R.id.list_item_name);
        TextView type = listItemView.findViewById(R.id.list_item_type);
        Button button = listItemView.findViewById(R.id.selected_route_site);

        HistoricalSite site = historicalSiteList.get(position);

        try {
            File file = File.createTempFile("image", "jpg");
            String imageFile = "gs://uncovering-history-mobile-app.appspot.com/images/" + site.getImageName();
            imageToShow = FirebaseStorage.getInstance().getReferenceFromUrl(imageFile);
            imageToShow.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                image.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkBox.setChecked(site.getChecked());
        name.setText(site.getName());
        type.setText(site.getType());
        Log.d(TAG, "" + checkBox.isChecked());

        checkBox.setOnClickListener(v -> {
//            DatabaseReference siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");
//            siteDbRef.child("checked").setValue(checkBox.isChecked());
            Log.d(TAG, "getView: " + checkBox.isChecked());
        });
        button.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), SitePage.class);
            intent.putExtra("selectedSite", site.getName());
            context.startActivity(intent);
        });
        return listItemView;
    }
}
