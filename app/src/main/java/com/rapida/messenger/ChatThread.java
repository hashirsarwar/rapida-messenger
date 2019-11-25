package com.rapida.messenger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatThread extends AppCompatActivity {
    GroupMeDatabase database;
    ChatThreadAdapter adapter;
    private List<Message> Messages;
    private EditText input;
    String userPhone;
    RecyclerView rv;
    String fileName;
    TextView dialogText;
    LinearLayout dialog;
    RelativeLayout mainContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);
        setTitle(getIntent().getStringExtra("groupName"));

        Messages = new ArrayList<>();
        database = GroupMeDatabase.getInstance(getApplicationContext());
        input = findViewById(R.id.chatEnter);
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recorded_audio.3gp";
        rv = findViewById(R.id.list_of_messages);
        dialogText = findViewById(R.id.dialog_text);
        mainContainer = findViewById(R.id.chat_thread_container);
        dialog = findViewById(R.id.dialog);
        setRV();
        //displayChat();
        setListenerForNewMessages();

        setListenerForVoiceMsgs();
    }

    private static long mDeBounce = 0;

    private void setListenerForVoiceMsgs()
    {
        FloatingActionButton fab = findViewById(R.id.voiceBtn);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if ( Math.abs(mDeBounce - event.getEventTime()) < 500) {
                    Log.wtf("ask","permissions");
                    getPermissionsAndRecord();
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    startRecording();
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    stopRecording();
                    return true;
                }
                return false;
            }
        });
    }

    private void setRV()
    {
        adapter = new ChatThreadAdapter(Messages);
        rv.setAdapter(adapter);
//        rv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
//            {
//                rv.scrollToPosition(rv.getAdapter().getItemCount()-1);
//            }
//        });
        LinearLayoutManager l = new LinearLayoutManager(getApplicationContext());
        l.setStackFromEnd(true);
        rv.setLayoutManager(l);
    }

//    private void displayChat()
//    {
//        new AsyncTask<Void,Void,Void>()
//        {
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                setRV();
//                setListenerForNewMessages();
//            }
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//                MessageDao dao = database.messageDao();
//                Messages = dao.getAllMessages(getIntent().getStringExtra("groupKey"));
//                return null;
//            }
//        }.execute();
//    }

    private void setListenerForNewMessages()
    {
        FirebaseDatabase.getInstance().getReference().child("messages").child(getIntent().getStringExtra("groupKey")).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagesFirebase m = dataSnapshot.getValue(MessagesFirebase.class);
                Message msg = new Message(dataSnapshot.getKey(),m.getMessageType(),m.getMessage(),
                        getIntent().getStringExtra("groupKey"),m.getSenderPhone(),m.getMessageTime());
                Messages.add(msg);
                adapter.notifyItemInserted(Messages.size()-1);
                rv.scrollToPosition(rv.getAdapter().getItemCount()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sendBtn(View view)
    {
        if(!TextUtils.isEmpty(input.getText().toString()))
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages").child(getIntent().getStringExtra("groupKey"));
            String key = reference.push().getKey();
            reference = reference.child(key);
            final HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("messageType","text");
            hashMap.put("message",input.getText().toString());
            hashMap.put("messageTime",String.valueOf(new Date().getTime()));
            hashMap.put("senderPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            reference.setValue(hashMap);
            input.setText("");
        }
    }


    public void sendPhotoFromGalleryBtn(View v)
    {
        Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i , 4);
    }

    public void sendPhotoBtn(View v)
    {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }

        else
        {
            showCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                showCamera();
            }
            else
            {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 33)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startRecording();
            }
            else
            {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCamera()
    {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 3);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == 88)
        {
            hideDialogBox();
        }

        if (requestCode == 3)
        {
            if (resultCode == RESULT_OK)
            {
                Bitmap image = data.getParcelableExtra("data");
                if (image != null)
                {
                    Uri uri = getImageUri(this,image);
                    if (uri != null)
                    {
                        final StorageReference imageref = FirebaseStorage.getInstance().getReference().child("photos").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(new Date().getTime()));
                        imageref.putFile(uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages").child(getIntent().getStringExtra("groupKey"));
                                        String key = reference.push().getKey();
                                        reference = reference.child(key);
                                        Log.wtf("REF",reference.toString());
                                        final HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("messageType", "image");
                                        hashMap.put("message", uri.toString());
                                        hashMap.put("messageTime", String.valueOf(new Date().getTime()));
                                        hashMap.put("senderPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                        reference.setValue(hashMap);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        }

        if (requestCode == 4)
        {
            if (resultCode == RESULT_OK) {
                if (data != null)
                {
                    Uri uri = data.getData();
                    final StorageReference imageref = FirebaseStorage.getInstance().getReference().child("photos").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(new Date().getTime()));
                    imageref.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages").child(getIntent().getStringExtra("groupKey"));
                                            String key = reference.push().getKey();
                                            reference = reference.child(key);
                                            Log.wtf("REF",reference.toString());
                                            final HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("messageType", "image");
                                            hashMap.put("message", uri.toString());
                                            hashMap.put("messageTime", String.valueOf(new Date().getTime()));
                                            hashMap.put("senderPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                            reference.setValue(hashMap);
                                        }
                                    });
                                }
                            });
                }
            }
        }
    }

    public void getPermissionsAndRecord()
    {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},33);
        }
    }


    MediaRecorder recorder;
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Recording Log", "prepare() failed");
        }

        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        recorder.start();
    }

    private void stopRecording() {
        Toast.makeText(ChatThread.this, "Recording stopped", Toast.LENGTH_SHORT).show();

        try
        {
            recorder.stop();
        }
        catch (RuntimeException re)
        {
            re.printStackTrace();
        }
        recorder.release();
        recorder = null;

        sendRecording();
    }

    private void sendRecording()
    {
        Uri uri = Uri.fromFile(new File(fileName));
        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("Audio").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(new Date().getTime()));;
        storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages").child(getIntent().getStringExtra("groupKey"));
                        String key = reference.push().getKey();
                        reference = reference.child(key);
                        Log.wtf("REF",reference.toString());
                        final HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("messageType", "recording");
                        hashMap.put("message", uri.toString());
                        hashMap.put("messageTime", String.valueOf(new Date().getTime()));
                        hashMap.put("senderPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                        reference.setValue(hashMap);
                    }
                });
            }
        });
    }

    private void displayDialogBox(String str) {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        dialogText.setText(str);
        dialog.setVisibility(View.VISIBLE);
        mainContainer.setAlpha(0.2f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDialogBox() {
        dialog.setVisibility(View.INVISIBLE);
        mainContainer.setAlpha(1);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void sendLocation(View view)
    {
        displayDialogBox("Loading");
        Intent i = new Intent(this,SendLocation.class);
        i.putExtra("groupID",getIntent().getStringExtra("groupKey"));
        startActivityForResult(i,88);
    }
}
