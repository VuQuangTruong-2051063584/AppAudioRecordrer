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
    private TimeAgo timeAgo;
    private  onItemListClick onItemListClick;
    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick){
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }
    ///////////////////////////////////////////////////HIẾU commit




}
