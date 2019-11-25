package com.rapida.messenger;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hbb20.CountryCodePicker;

public class RegisterUser extends AppCompatActivity {

    private CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ccp = findViewById(R.id.ccp);
        TextInputLayout phn = findViewById(R.id.textInputLayout2);
        ccp.registerCarrierNumberEditText(phn.getEditText());
    }

    public void reg_user(View view)
    {
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        String name = textInputLayout.getEditText().getText().toString();

        if(TextUtils.isEmpty(name))
        {
            textInputLayout.setError("Display name is required");
            return;
        }
        if(!ccp.isValidFullNumber())
        {
            textInputLayout = findViewById(R.id.textInputLayout2);
            textInputLayout.setError("Not a valid phone number");
            return;
        }
        String ph = ccp.getFullNumberWithPlus();

        Intent i = new Intent(this,ConfirmPhone.class);
        i.putExtra("name",name);
        i.putExtra("phone",ph);
        startActivity(i);
        finish();
    }
}
