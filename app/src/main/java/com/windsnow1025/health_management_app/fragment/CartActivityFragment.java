package com.windsnow1025.health_management_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.pojo.Product;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;
import com.windsnow1025.health_management_app.utils.ProductAdapter;

import java.util.List;

public class CartActivityFragment extends Fragment {

    private List<Product> goodsList;
    private ProductAdapter productAdapter;
    private UserLocalDao userLocalDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        userLocalDao = new UserLocalDao(getActivity());
        userLocalDao.open();

        // Initialize product list from SQLite database
        goodsList = userLocalDao.getAllGoods();

        // Set up the ListView and adapter
        ListView cartListView = view.findViewById(R.id.cartListView);
        productAdapter = new ProductAdapter(getActivity(), goodsList);
        cartListView.setAdapter(productAdapter);

        // Display the total price
        displayTotal(view);

        // Add any additional logic for the CartActivity
        return view;
    }

    private void displayTotal(View view) {
        double total = 0;
        for (Product goods : goodsList) {
            total += goods.getPrice();
        }

        TextView totalTextView = view.findViewById(R.id.totalTextView);
        totalTextView.setText("Total: $" + String.format("%.2f", total));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userLocalDao.close();
    }
}