package com.rapida.messenger;

import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ChatThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> listOfMessages;

    ChatThreadAdapter(List<Message> l)
    {
        this.listOfMessages = l;
    }

    class ViewHolderForText extends RecyclerView.ViewHolder
    {
        TextView displayName;
        TextView messageText;
        TextView time;
        public ViewHolderForText(@NonNull View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.textView11);
            messageText = itemView.findViewById(R.id.textView12);
            time = itemView.findViewById(R.id.textView13);
        }
    }


    class ViewHolderForImage extends RecyclerView.ViewHolder
    {

        TextView displayName;
        ImageView image;
        TextView time;

        public ViewHolderForImage(@NonNull View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.textView14);
            image = itemView.findViewById(R.id.imageView);
            time = itemView.findViewById(R.id.textView15);
        }
    }

    class ViewHolderForVoiceNotes extends RecyclerView.ViewHolder
    {

        TextView displayName;
        Button button;
        TextView time;

        public ViewHolderForVoiceNotes(@NonNull View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.display_name);
            button = itemView.findViewById(R.id.button4);
            time = itemView.findViewById(R.id.time);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater  = LayoutInflater.from(viewGroup.getContext());

        switch (i)
        {
            case 4:
            case 1:
                view = inflater.inflate(R.layout.chat_thread_item, viewGroup, false);
                return new ViewHolderForText(view);
            case 2:
                view = inflater.inflate(R.layout.chat_thread_image, viewGroup, false);
                return new ViewHolderForImage(view);
            case 3:
                view = inflater.inflate(R.layout.chat_thread_voice, viewGroup, false);
                return new ViewHolderForVoiceNotes(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Message m = listOfMessages.get(i);

        switch (viewHolder.getItemViewType())
        {
            case 1:
                ViewHolderForText holder1 = (ViewHolderForText) viewHolder;
                holder1.displayName.setText(m.getSenderPhone());
                holder1.messageText.setText(m.getMessage());
                holder1.time.setText(DateFormat.format("hh:mm",Long.parseLong(m.getMessageTime())));
                break;
            case 2:
                ViewHolderForImage holder2 = (ViewHolderForImage) viewHolder;
                holder2.displayName.setText(m.getSenderPhone());
                Picasso.get().load(m.getMessage()).into(holder2.image);
                holder2.time.setText(DateFormat.format("hh:mm",Long.parseLong(m.getMessageTime())));
                break;
            case 3:
                ViewHolderForVoiceNotes holder3 = (ViewHolderForVoiceNotes) viewHolder;
                holder3.displayName.setText(m.getSenderPhone());
                final Message mm = m;
                holder3.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new playAudioInAsync().execute(mm.getMessage());
                    }
                });
                holder3.time.setText(DateFormat.format("hh:mm",Long.parseLong(m.getMessageTime())));
                break;
            case 4:
                final ViewHolderForText holder4 = (ViewHolderForText) viewHolder;
                holder4.displayName.setText(m.getSenderPhone());
                holder4.messageText.setText(m.getMessage().split("###")[2]);
                holder4.messageText.setPaintFlags(holder4.messageText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder4.messageText.setTextColor(0xFF3F51B5);
                final Message mmm = m;
                holder4.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(holder4.messageText.getContext(),ViewLocation.class);
                        i.putExtra("latitude",mmm.getMessage().split("###")[0]);
                        i.putExtra("longitude",mmm.getMessage().split("###")[1]);
                        holder4.messageText.getContext().startActivity(i);
                    }
                });
                holder4.time.setText(DateFormat.format("hh:mm",Long.parseLong(m.getMessageTime())));
                break;
        }
    }

    private static class playAudioInAsync extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            MediaPlayer player = new MediaPlayer();
            try {
                player.setDataSource(strings[0]);
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.e("Audio Playing Error", "prepare() failed");
            }
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch (listOfMessages.get(position).getMessageType())
        {
            case "text":
                return 1;
            case "image":
                return 2;
            case "recording":
                return 3;
            case "location":
                return 4;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return listOfMessages.size();
    }
}
