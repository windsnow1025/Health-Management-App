package com.windsnow1025.health_management_app.fragment.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.model.Product;
import com.windsnow1025.health_management_app.database.UserLocalDao;
import com.windsnow1025.health_management_app.utils.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private UserLocalDao userLocalDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shopping_cart, container, false);
        productList = new ArrayList<>();
        productList.add(new Product("大力丸", 99.98, R.drawable.product1));
        productList.add(new Product("伸腿瞪眼丸", 999.98, R.drawable.product2));
        productList.add(new Product("肾宝", 9999.98, R.drawable.product3));

        userLocalDao = new UserLocalDao(getActivity());
        userLocalDao.open();

        // Initialize product list from SQLite database

        // Set up the ListView and adapter
        productAdapter = new ProductAdapter(getActivity(), productList);
        ListView productListView = view.findViewById(R.id.productListView);
        productListView.setAdapter(productAdapter);

        // Set item click listener to add product to cart
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addToCart(position);
            }
        });

        // Set up the "View Cart" button click listener
        Button cartButton = view.findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCart();
            }
        });

        return view;
    }

    private void addToCart(int position) {
        Product goods = productList.get(position);
        userLocalDao.insertGoods(goods);
        Toast.makeText(getActivity(), goods.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void viewCart() {
        // Get cart list from SQLite database
        List<Product> cartList = userLocalDao.getAllGoods();

        // Check if the cart is not empty before navigating to CartActivity
        if (!cartList.isEmpty()) {
            Fragment fragment = new CartFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Toast.makeText(getActivity(), "Cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userLocalDao.close();
    }
}