package com.rapida.messenger;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.format.DateFormat;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    List<HomeList> list;

    OnItemClickListener listener;

    public interface OnItemClickListener
    {
        void onItemClick(View view, int pos);
    }

    public void setOnItemClick(OnItemClickListener listener)
    {
        this.listener = listener;
    }
    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView groupName;
        TextView messageText;
        TextView messageTime;
        ViewHolder(View view)
        {
            super(view);
            groupName = view.findViewById(R.id.textView3);
            messageText = view.findViewById(R.id.textView9);
            messageTime = view.findViewById(R.id.textView10);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getAdapterPosition());
                }
            });
        }
    }

    public HomeListAdapter(List<HomeList> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.home_list_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HomeList obj = list.get(i);
        viewHolder.groupName.setText(obj.getGroupName());
        viewHolder.messageText.setText(obj.getMessage());
        viewHolder.messageTime.setText(DateFormat.format("hh:mm",Long.parseLong(obj.getMessageTime())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
