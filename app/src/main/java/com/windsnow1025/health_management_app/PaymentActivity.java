package com.windsnow1025.health_management_app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private Spinner spinnerPaymentMethod;
    private EditText editTextCardNumber;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Set options for the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        // Set Spinner selection listener
        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Show or hide credit card number EditText based on selected payment method
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

        Button payButton = findViewById(R.id.buttonPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
                String cardNumber = editTextCardNumber.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim(); // Get password

                // Add payment logic here (Example: password input validation)
                if (!password.isEmpty()) {
                    // Password is not empty, proceed with payment logic
                    String paymentMessage = "Payment Method: " + paymentMethod + "\nCard Number: " + cardNumber + "\nPassword: " + password;
                    Toast.makeText(PaymentActivity.this, paymentMessage, Toast.LENGTH_SHORT).show();
                } else {
                    // Password is empty, display a prompt
                    Toast.makeText(PaymentActivity.this, "Please enter the password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Button click event specified in XML
    public void onPayButtonClick(View view) {
        // Additional logic for WeChat Pay can be added here
        // This is just an example, displaying a toast for successful WeChat payment
        Toast.makeText(this, "WeChat Payment Successful!", Toast.LENGTH_SHORT).show();
    }
}
