package com.rapida.messenger;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ConfirmPhone extends AppCompatActivity {

    private TextInputLayout codeField;
    private TextView dialogText;
    private ConstraintLayout mainContainer;
    private LinearLayout dialog;
    private String verificationID;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone);

        String ph = getIntent().getStringExtra("phone");

        codeField = findViewById(R.id.textInputLayout3);
        dialogText = findViewById(R.id.dialog_text);
        mainContainer = findViewById(R.id.confirm_phone);
        dialog = findViewById(R.id.dialog);
        auth = FirebaseAuth.getInstance();
        sendVerificationCode(ph);
    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void verifyBtn(View view)
    {
        String code = codeField.getEditText().getText().toString();

        if (TextUtils.isEmpty(code) || code.length() < 6)
        {
            codeField.setError("Enter a valid verification code");
            return;
        }
        verifyCode(code);
    }

    private void verifyCode(String code)
    {
        if(verificationID!=null) {
            hideKeyboard();
            dialogText.setText("Verifying...");
            dialog.setVisibility(View.VISIBLE);
            mainContainer.setAlpha(0.2f);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("phone"));
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            DatabaseReference ref = reference.child("displayName");
                                            ref.setValue(getIntent().getStringExtra("name"));
                                        }
                                        else
                                        {
                                            HashMap<String,String> hashMap = new HashMap<>();
                                            hashMap.put("displayName",getIntent().getStringExtra("name"));
                                            reference.setValue(hashMap);
                                        }
                                        Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Home.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                dialog.setVisibility(View.INVISIBLE);
                                mainContainer.setAlpha(1);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getApplicationContext(), "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show();
    }

    private void sendVerificationCode(String phone)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                callBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if(code != null)
            {
                codeField.getEditText().setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
        }
    };
}