package com.example.groupme;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class RegisterUser extends AppCompatActivity {

//    private FirebaseAuth auth;
//    private LinearLayout dialog;
//    private TextView dialogText;
//    private ConstraintLayout mainContainer;

    private CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ccp = findViewById(R.id.ccp);
//        dialogText = findViewById(R.id.dialog_text);
//        dialog = findViewById(R.id.dialog);
//        mainContainer = findViewById(R.id.reg_main_container);
//        auth = FirebaseAuth.getInstance();

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

//
//        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
//        {
//            dialogText.setText("Creating account...");
//            dialog.setVisibility(View.VISIBLE);
//            mainContainer.setAlpha(0.2f);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            registerUser(name,email,password);
//        }
    }

//    private void registerUser(String name, String email, String pass)
//    {
//        auth.createUserWithEmailAndPassword(email,pass)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful())
//                        {
//                            Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getApplicationContext(),Home.class);
//                            startActivity(intent);
//                            finishAffinity();
//                        }
//                        else
//                        {
//                            dialog.setVisibility(View.INVISIBLE);
//                            mainContainer.setAlpha(1);
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            Toast.makeText(getApplicationContext(),"Registration unsuccessful",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
}
