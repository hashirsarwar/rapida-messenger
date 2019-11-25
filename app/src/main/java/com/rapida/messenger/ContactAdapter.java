package com.rapida.messenger;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private ArrayList<Contact> listOfContacts;
    ContactAdapter(ArrayList<Contact> list)
    {
        listOfContacts = list;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view,int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameView;
        TextView phoneView;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.textView6);
            phoneView = itemView.findViewById(R.id.textView7);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.new_group_contact_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder viewHolder, int i) {
        Contact c = listOfContacts.get(i);

        viewHolder.checkBox.setChecked(c.isChecked());
        viewHolder.nameView.setText(c.getName());
        viewHolder.phoneView.setText(c.getPhone());
    }

    @Override
    public int getItemCount() {
        return listOfContacts.size();
    }
}
