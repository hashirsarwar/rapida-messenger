package com.rapida.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity
{
    List<HomeList> homeList;
    GroupMeDatabase db;
    HomeListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        db = GroupMeDatabase.getInstance(this);
        homeList = new ArrayList<>();

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            sendToStartPage();
        }

        else
        {
            addListeners();

            displayData();
        }

    }

    private void displayData()
    {
        new AsyncTask<Void,Void,List<HomeList>>()
        {
            @Override
            protected void onPostExecute(final List<HomeList> homeLists) {
                super.onPostExecute(homeLists);
                RecyclerView rv = findViewById(R.id.list_of_chats);
                adapter = new HomeListAdapter(homeLists);
                adapter.setOnItemClick(new HomeListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        Intent i = new Intent(getApplicationContext(), ChatThread.class);
                        i.putExtra("groupKey", homeLists.get(pos).getGroupID());
                        i.putExtra("groupName", homeLists.get(pos).getGroupName());
                        startActivity(i);
                    }
                });
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            protected List<HomeList> doInBackground(Void... voids) {
                List<Group> groups;
                GroupDao dao = db.groupDao();
                groups = dao.getAllGroups();
                MessageDao dao1 = db.messageDao();
                for (Group g : groups)
                {
                    MessageAndTime mt = dao1.getLatestMessage(g.getId());
                    if(mt == null) homeList.add(0,new HomeList(g.getId(),g.getName(),"You were added",g.getTimeCreated()));
                    else if (mt.messageType.equals("text")) homeList.add(0,new HomeList(g.getId(),g.getName(),mt.message,mt.messageTime));
                    else if (mt.messageType.equals("image"))homeList.add(0,new HomeList(g.getId(),g.getName(),"IMAGE",mt.messageTime));
                    else if (mt.messageType.equals("recording"))homeList.add(0,new HomeList(g.getId(),g.getName(),"VOICE NOTE",mt.messageTime));
                    else if (mt.messageType.equals("location"))homeList.add(0,new HomeList(g.getId(),g.getName(),"LOCATION",mt.messageTime));
                }
                Collections.sort(homeList,Collections.<HomeList>reverseOrder());
                return homeList;
            }
        }.execute();
    }

    private void addListeners()
    {
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupsFirebase g = dataSnapshot.getValue(GroupsFirebase.class);
                final Group g2 = new Group(dataSnapshot.getKey(),g.getGroupName(),g.getAdminPhone(),g.getTimeCreated());

                new AsyncTask<Void,Void,Boolean>()
                {
                    @Override
                    protected void onPostExecute(Boolean changed) {
                        super.onPostExecute(changed);
                        if (changed)
                            updateAdapter();
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        GroupDao dao = db.groupDao();
                        Group g = dao.checkIfExists(g2.getId());
                        if(g==null) {
                            dao.insertGroup(g2);
                            homeList.add(0,new HomeList(g2.getId(),g2.getName(),"You were added",g2.getTimeCreated()));
                            return true;
                        }
                        return false;
                    }
                }.execute();
                setListenersForMessages(g2);
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

    private void updateAdapter()
    {
//        Toast.makeText(this, "update adapter", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void removeFromList(String key)
    {
        int i = 0;
        for (HomeList h : homeList)
        {
            if (h.getGroupID().equals(key))
            {
                homeList.remove(i);
                return;
            }
            i++;
        }
    }

    private void setListenersForMessages(final Group group)
    {
        FirebaseDatabase.getInstance().getReference().child("messages").child(group.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                final MessagesFirebase m = dataSnapshot.getValue(MessagesFirebase.class);
                new AsyncTask<Void,Void,Boolean>()
                {
                    @Override
                    protected void onPostExecute(Boolean changed) {
                        super.onPostExecute(changed);
                        if (changed)
                            updateAdapter();
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        MessageDao dao = db.messageDao();
                        Message g = dao.checkIfExists(dataSnapshot.getKey());
                        if(g==null) {
                            removeFromList(group.getId());
                            dao.insertMessage(new Message(dataSnapshot.getKey(),m.getMessageType(),m.getMessage(),group.getId(),m.getSenderPhone(),m.getMessageTime()));

                            if (m.getMessageType().equals("text"))
                                homeList.add(0,new HomeList(group.getId(),group.getName(),m.getMessage(),m.getMessageTime()));
                            else if (m.getMessageType().equals("image"))
                                homeList.add(0,new HomeList(group.getId(),group.getName(),"IMAGE",m.getMessageTime()));
                            else if (m.getMessageType().equals("recording"))
                                homeList.add(0,new HomeList(group.getId(),group.getName(),"VOICE NOTE",m.getMessageTime()));
                            else if (m.getMessageType().equals("location"))
                                homeList.add(0,new HomeList(group.getId(),group.getName(),"LOCATION",m.getMessageTime()));
                            return true;
                        }
                        return false;
                    }
                }.execute();

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

    private void sendToStartPage() {
        Intent intent = new Intent(this, StartPage.class);
        startActivity(intent);
        finish();
    }

    public void newGroupBtn(View view)
    {
        Intent i = new Intent(this,NewGroup.class);
        startActivity(i);
    }
}
