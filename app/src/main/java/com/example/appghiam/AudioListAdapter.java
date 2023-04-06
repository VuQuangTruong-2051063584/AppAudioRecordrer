package com.example.appghiam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    ///////////////////////////////////////////////////trường commit
    private File[] allFiles;

    //private TimeAgo timeAgo;
    private onItemListClick onItemListClick;

    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    ///////////////////////////////////////////////////HIẾU commit
    @NonNull
    @Override
    public AudioListAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioListAdapter.AudioViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface onItemListClick {

    }


    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageButton delete;
        private ImageButton edit_name_file;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            delete = itemView.findViewById(R.id.delete_btn);
            edit_name_file = itemView.findViewById(R.id.edit_btn);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}