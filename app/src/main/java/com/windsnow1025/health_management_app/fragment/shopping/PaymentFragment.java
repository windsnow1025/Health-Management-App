package com.windsnow1025.health_management_app.fragment.shopping;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.windsnow1025.health_management_app.R;

public class PaymentFragment extends Fragment {

    private Spinner spinnerPaymentMethod;
    private EditText editTextCardNumber;
    private EditText editTextPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_payment, container, false);

        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod);
        editTextCardNumber = view.findViewById(R.id.editTextCardNumber);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedPaymentMethod = parentView.getItemAtPosition(position).toString();
                if ("信用卡支付".equals(selectedPaymentMethod)) {
                    editTextCardNumber.setVisibility(View.VISIBLE);
                } else {
                    editTextCardNumber.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optional action when nothing is selected
            }
        });

        Button payButton = view.findViewById(R.id.buttonPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
                String cardNumber = editTextCardNumber.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (!password.isEmpty()) {
                    String paymentMessage = "Payment Method: " + paymentMethod + "\n    Payment success";
                    Toast.makeText(requireContext(), paymentMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Please enter the password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // You can keep the onPayButtonClick method if needed
    // Remove it if not required
}
