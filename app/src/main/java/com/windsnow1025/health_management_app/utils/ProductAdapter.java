package com.windsnow1025.health_management_app.utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.model.Product;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, List<Product> goods) {
        super(context, 0, goods);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Product goods = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        // Lookup view for data population
        TextView productName = convertView.findViewById(R.id.productName);
        TextView productPrice = convertView.findViewById(R.id.productPrice);
        ImageView productImage = convertView.findViewById(R.id.productImage);

        // Populate the data into the template view using the data object
        if (goods != null) {
            productName.setText(goods.getName());
            productPrice.setText("$" + String.format("%.2f", goods.getPrice()));
            productImage.setImageResource(goods.getImageResourceId());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
