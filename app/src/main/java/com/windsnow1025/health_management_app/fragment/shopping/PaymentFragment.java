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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.database.UserLocalDao;

public class PaymentFragment extends Fragment {

    private Spinner spinnerPaymentMethod;
    private EditText editTextCardNumber;
    private EditText editTextPassword;
    private UserLocalDao userLocalDao; // 创建 UserLocalDao 对象

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_payment, container, false);

        // 初始化 UserLocalDao 对象
        userLocalDao = new UserLocalDao(requireContext());
        userLocalDao.open();

        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod);
        editTextCardNumber = view.findViewById(R.id.editTextCardNumber);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (bundle != null) {
            double totalAmount = bundle.getDouble("TOTAL_AMOUNT", 0.0);

            // 找到 TextView 的引用
            TextView textViewAmount = view.findViewById(R.id.textViewAmount);

            // 格式化并设置支付金额的文本
            String formattedAmount = String.format("支付金额: $%.2f", totalAmount);
            textViewAmount.setText(formattedAmount);
        }

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

                    // 支付成功后，清空购物车
                    userLocalDao.deleteGoods();

                    navigateBackToShoppingFragment();
                } else {
                    Toast.makeText(requireContext(), "Please enter the password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void navigateBackToShoppingFragment() {
        Fragment fragment = new ShoppingFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userLocalDao.close();
    }
}