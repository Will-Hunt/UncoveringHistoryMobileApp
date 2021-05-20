package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    String description, location, routeList;
    List<HistoricalSite> historicalSiteList;

    public ListAdapter(Activity context, List<HistoricalSite> historicalSiteList, String routeList) {
        super(context, R.layout.list_item, historicalSiteList);
        this.context = context;
        this.historicalSiteList = historicalSiteList;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"InflateParams", "ViewHolder"}) View listItemView = inflater.inflate(R.layout.list_item, null, true);
//      Gets the XML element that needs to be populated

        ImageView image = listItemView.findViewById(R.id.list_item_image);
        TextView name = listItemView.findViewById(R.id.list_item_name);
        Button viewSiteButton = listItemView.findViewById(R.id.selected_route_site);

        HistoricalSite site = historicalSiteList.get(position);

//          A Try Catch is required in case a temporary file cannot be made, as a result of the createTempFile function
        String imageName = site.getImageName();
        try {
            File file = File.createTempFile("image", "jpg");
            String imageFile = "gs://uncovering-history-mobile-app.appspot.com/images/" + imageName;
            imageToShow = FirebaseStorage.getInstance().getReferenceFromUrl(imageFile);
            imageToShow.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                image.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
//      Populates the element
        description = site.getDescription();
        name.setText(site.getName());
        location = site.getLocation();

//      The following waits for the user to click on the Add To Route button and a function is called to complete the task
        viewSiteButton.setOnClickListener(v -> {
//          An Intent is made, the Route List is added and the intent to the Routes is called
            Intent intent = new Intent(context.getApplicationContext(), SitePage.class);
            intent.putExtra("selectedSite", site.getName());
            intent.putExtra("routeList", routeList);
            context.startActivity(intent);
        });
        return listItemView;
    }
}
