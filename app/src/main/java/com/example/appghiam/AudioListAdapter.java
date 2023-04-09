package com.example.appghiam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appghiam.AudioListFragment;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    private File[] allFiles;
    private TimeAgo timeAgo;
    private onItemListClick onItemListClick;

    public AudioListAdapter(File[] allFiles, AudioListFragment onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_list_item, parent,false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public interface onItemListClick {
        void onClickListener(File file, int position);

        void onDeleteClickListener(int position);

    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;
        private ImageButton delete;

        private ImageButton edit_name_file;

        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()],getAdapterPosition());
        }

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);


            delete = itemView.findViewById(R.id.delete_btn);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.SCREEN_STATE_ON)
                        onItemListClick.onDeleteClickListener(position);
                }
            });

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            list_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.SCREEN_STATE_ON) {
                        onItemListClick.onClickListener(allFiles[position], position);

                    }
                }
            });
        }


    }
}