package com.rapida.messenger;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }

    public void registerBtn(View view)
    {
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }
}
