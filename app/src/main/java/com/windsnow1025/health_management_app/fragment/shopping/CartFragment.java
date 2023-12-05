package com.windsnow1025.health_management_app.fragment.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.model.Product;
import com.windsnow1025.health_management_app.database.UserLocalDao;
import com.windsnow1025.health_management_app.utils.ProductAdapter;

import java.util.List;

public class CartFragment extends Fragment {

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

        Button proceedToPaymentButton = view.findViewById(R.id.buttonProceedToPayment);
        proceedToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the total amount
                double total = calculateTotal();

                // Navigate to the PaymentFragment and pass the total amount
                navigateToPaymentFragment(total);
            }
        });

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
        TextView textViewAmount=view.findViewById(R.id.textViewAmount);
        textViewAmount.setText("Total: $" + String.format("%.2f", total));

    }

    private double calculateTotal() {
        double total = 0;
        for (Product goods : goodsList) {
            total += goods.getPrice();
        }
        return total;
    }

    private void navigateToPaymentFragment(double totalAmount) {
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("TOTAL_AMOUNT", totalAmount);
        paymentFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        userLocalDao.close();
    }
}