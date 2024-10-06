package com.example.poscanteen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class transaction extends AppCompatActivity {

    EditText paymentValueInput;
    TextView totalText, changeText;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnEnter, btnBackspace, btnCancel, btnOk;
    double totalAmount = 0.00; // Set this based on your application logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction); // Use the correct layout file

        // Initialize views
        totalText = findViewById(R.id.total_value);
        paymentValueInput = findViewById(R.id.payment_value);
        changeText = findViewById(R.id.change_value);

        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn8 = findViewById(R.id.btn_8);
        btn9 = findViewById(R.id.btn_9);
        btn0 = findViewById(R.id.btn_0);
        btnEnter = findViewById(R.id.btn_enter);
        btnBackspace = findViewById(R.id.btn_backspace);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOk = findViewById(R.id.btn_ok);

        // Set up button click listeners
        setNumberPadListeners();
        btnEnter.setOnClickListener(v -> calculateChange());
        btnCancel.setOnClickListener(v -> clearInputs());
        btnOk.setOnClickListener(v -> finish()); // Finish activity or any other logic
    }

    private void setNumberPadListeners() {
        btn1.setOnClickListener(v -> appendToPaymentInput("1"));
        btn2.setOnClickListener(v -> appendToPaymentInput("2"));
        btn3.setOnClickListener(v -> appendToPaymentInput("3"));
        btn4.setOnClickListener(v -> appendToPaymentInput("4"));
        btn5.setOnClickListener(v -> appendToPaymentInput("5"));
        btn6.setOnClickListener(v -> appendToPaymentInput("6"));
        btn7.setOnClickListener(v -> appendToPaymentInput("7"));
        btn8.setOnClickListener(v -> appendToPaymentInput("8"));
        btn9.setOnClickListener(v -> appendToPaymentInput("9"));
        btn0.setOnClickListener(v -> appendToPaymentInput("0"));
        btnBackspace.setOnClickListener(v -> removeLastChar());
    }

    private void appendToPaymentInput(String value) {
        paymentValueInput.append(value);
    }

    private void removeLastChar() {
        String currentText = paymentValueInput.getText().toString();
        if (currentText.length() > 0) {
            paymentValueInput.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void calculateChange() {
        String paymentText = paymentValueInput.getText().toString();
        double paymentAmount = paymentText.isEmpty() ? 0.00 : Double.parseDouble(paymentText);
        double change = paymentAmount - totalAmount;
        changeText.setText("₱" + String.format("%.2f", change));
    }

    private void clearInputs() {
        paymentValueInput.setText("");
        changeText.setText("₱0.00");
    }
}
