package com.rapida.messenger;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class NewGroup extends AppCompatActivity {

    ArrayList<Contact> contacts;
    TextView dialogText;
    RelativeLayout mainContainer;
    LinearLayout dialog;
    ArrayList<Contact> availableContacts;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        dialogText = findViewById(R.id.dialog_text);
        mainContainer = findViewById(R.id.new_group_container);
        dialog = findViewById(R.id.dialog);
        adView = findViewById(R.id.adView);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(getApplicationContext(),"ca-app-pub-3940256099942544~3347511713");
                AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                adView.loadAd(adRequest);
            }
        },2000);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},1);
        }

        else
        {
            showContacts();
        }
    }

    private void displayDialogBox(String str)
    {
        dialogText.setText(str);
        dialog.setVisibility(View.VISIBLE);
        mainContainer.setAlpha(0.2f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDialogBox()
    {
        dialog.setVisibility(View.INVISIBLE);
        mainContainer.setAlpha(1);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                showContacts();
            }
            else
            {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showContacts()
    {
        displayDialogBox("Loading");
        contacts = fetchContacts();
        displayContactsHavingAccount();
    }

    private void displayContactsHavingAccount()
    {
        final ArrayList<String> list = new ArrayList<>();
        availableContacts = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.exists())
                    {
                        list.add(snapshot.getKey());
                    }
                }

                int size = contacts.size();

                Contact found;
                for (int i = 0; i < size; i++)
                {
                    if(list.indexOf(contacts.get(i).getPhone()) != -1)
                    {
                        found=contacts.get(i);
                        availableContacts.add(found);
                    }
                }

                if(!availableContacts.isEmpty()) {
                    final RecyclerView recyclerView = findViewById(R.id.contacts);
                    Collections.sort(availableContacts);
                    final ContactAdapter adapter = new ContactAdapter(availableContacts);
                    adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int pos) {
                            Contact c = availableContacts.get(pos);
                            c.setChecked(!c.isChecked());
                            availableContacts.set(pos, c);
                            adapter.notifyItemChanged(pos);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(NewGroup.this));
                }
                hideDialogBox();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewGroup.this, "Database error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    public void onInviteClicked(View view)
    {
        displayDialogBox("Loading");
        Intent i = new AppInviteInvitation.IntentBuilder("Invite Your Friends!")
                .setMessage("Hey! Join my group conversation by downloading this App from the PlayStore.")
                .build();
        startActivityForResult(i,1);
    }

    private ArrayList<Contact> fetchContacts()
    {
        ArrayList<Contact> list = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};

        ContentResolver contentResolver = getContentResolver();
        Cursor c = contentResolver.query(uri,projection,null,null,null);
        String s;

        if(c!=null)
        {
            c.moveToFirst();
            while (c.moveToNext())
            {
                s = c.getString(1).replaceAll("[- ]","");
                list.add(new Contact(c.getString(0),s,false));
            }
            c.close();
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1)
        {
            hideDialogBox();
        }
    }

    public void makeGroupBtn(View view)
    {
        displayDialogBox("Creating group");
        TextInputLayout inputLayout = findViewById(R.id.textInputLayout4);
        final String gname = inputLayout.getEditText().getText().toString();

        if(TextUtils.isEmpty(gname))
        {
            inputLayout.setError("Group name is required");
            hideDialogBox();
            return;
        }
        int marked = 0;

        for (Contact c : availableContacts)
        {
            if(c.isChecked())
                marked++;
        }

        if(marked == 0)
        {
            Toast.makeText(this, "No contact selected", Toast.LENGTH_SHORT).show();
            hideDialogBox();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups");
        final String key = reference.push().getKey();
        reference = reference.child(key);
        /*
        Adding in the node of admin who created the group
         */
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("adminPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        hashMap.put("groupName",gname);
        final long time = new Date().getTime();
        hashMap.put("timeCreated", String.valueOf(time));
        reference.setValue(hashMap);

        /*
        Adding in the nodes of other members which admin selected
         */
        for (Contact c : availableContacts)
        {
            if(c.isChecked())
            {
                reference = FirebaseDatabase.getInstance().getReference().child("users").child(c.getPhone()).child("groups");
                reference = reference.child(key);
                reference.setValue(hashMap);
            }
        }
        Intent i = new Intent(this,ChatThread.class);
        i.putExtra("groupName",gname);
        i.putExtra("groupKey",key);
        startActivity(i);
        finish();
    }
}
