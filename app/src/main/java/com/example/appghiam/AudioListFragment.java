package com.example.appghiam;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appghiam.AudioListAdapter;
import com.example.appghiam.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick{

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView audioList;
    private File[] allFiles;
    private AudioListAdapter audioListAdapter;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay;

    private ImageButton playBtn;
    private ImageButton nextRightplayBtn;

    private int forwardClickCount = 0;
    private ImageButton nextLeftplayBtn;
    private TextView playerHeader;
    private TextView playFilename;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private int currentFileIndex = -1;

    private long DOUBLE_CLICK_TIME_DELTA = 300; // milliseconds
    private long[] lastClickTime = {0, 0};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.audio_list_view);

        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playFilename = view.findViewById(R.id.player_filename);

        playerSeekbar = view.findViewById(R.id.player_seekbar);


        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    pauseAudio();
                } else {
                    if (fileToPlay == null) {
                        resumeAudio();
                    }
                }

            }
        });


        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

        nextRightplayBtn = view.findViewById(R.id.forwardIB);
        nextLeftplayBtn = view.findViewById(R.id.backwardIB);

        // tiến 1s
//        nextRightplayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    int duration = mediaPlayer.getDuration();
//                    if (currentPosition + 1000 < duration) {
//                        mediaPlayer.seekTo(currentPosition + 1000);
//                    } else {
//                        mediaPlayer.seekTo(duration);
//                    }
//                } else {
//
//                }
//            }
//        });



        GestureDetector gestureDetectorNextRight = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                handleDoubleClick(true);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                handleSingleClick(true);
                return true;
            }
        });

        // lùi 1s
//        nextLeftplayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    int p = currentPosition - 1000;
//                    if (p < 0) {
//                        p = 0;
//                    }
//                    mediaPlayer.seekTo(p);
//                }
//            }
//        });
        nextRightplayBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetectorNextRight.onTouchEvent(event);
            }
        });

        GestureDetector gestureDetectorNextLeft = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                handleDoubleClick(false);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                handleSingleClick(false);
                return true;
            }
        });

        nextLeftplayBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetectorNextLeft.onTouchEvent(event);
                }
            });



    }

    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if (isPlaying){
            stopAudio();
            playAudio(fileToPlay);

        }else {
            playAudio(fileToPlay);
        }

    }



    private void pauseAudio(){
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.playbutton, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    private void resumeAudio(){
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_btn, null));
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private Activity parentActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (Activity) context;
    }

    private void stopAudio() {
        forwardClickCount = 0;
        if (mediaPlayer != null && parentActivity != null) {
            //dung audio
            playBtn.setImageDrawable(parentActivity.getResources().getDrawable(R.drawable.playbutton, null));
            playerHeader.setText("Stopped");
            isPlaying = false;
            mediaPlayer.stop();
            seekbarHandler.removeCallbacks(updateSeekbar);
        }
    }


    private void playAudio(File fileToPlay) {

        int index = -1;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].equals(fileToPlay)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            currentFileIndex = index;
        }

        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Activity activity = getActivity();
        if (activity != null) {
            playBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.pause_btn, null));
        }
        playFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        //Play the audio
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 0);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAudio();
    }

    // xoa file
    @Override
    public void onDeleteClickListener(int position) {

        onStop();
        File fileToRename = allFiles[position];

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn xóa file " + fileToRename.getName() +"?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (allFiles.length > 0) {
                            File fileToDelete = allFiles[position];
                            if (fileToDelete.exists()) {
                                boolean isDeleted = fileToDelete.delete();
                                if (isDeleted) {
                                    File[] newFiles = new File[allFiles.length - 1];
                                    int j = 0;
                                    for (int i = 0; i < allFiles.length; i++) {
                                        if (i != position) {
                                            newFiles[j++] = allFiles[i];
                                        }
                                    }
                                    allFiles = newFiles;
                                    // Sử dụng runOnUiThread để cập nhật danh sách trên giao diện
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            audioListAdapter = new AudioListAdapter(newFiles, AudioListFragment.this);
                                            audioList.setAdapter(audioListAdapter);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }).start();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // edit name file
    @Override
    public void onEditNameFileClickListener(int position) {
        // Show a dialog to edit file name
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa tên file");

        final EditText input = new EditText(getContext());
        input.setText(allFiles[position].getName());

        builder.setView(input);

        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    //Lưu tên mới và update list
                    saveNewFileName(position, newName);
                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void saveNewFileName(int position, String newName) {
        File fileToRename = allFiles[position];
        File newFile = new File(fileToRename.getParent(), newName);
        boolean isRenamed = fileToRename.renameTo(newFile);
        if (isRenamed) {
            allFiles[position] = newFile;
            audioListAdapter.notifyItemChanged(position);
        } else {
            Toast.makeText(getContext(), "Đổi tên file thất bại", Toast.LENGTH_SHORT).show();
        }
    }

//    private void handleDoubleClick(Boolean b) {
//        if (b == true){
//            if (currentFileIndex + 1 >= allFiles.length) {
//                currentFileIndex = 0;
//            } else {
//                currentFileIndex++;
//            }
//        } else {
//        }
//        playAudio(allFiles[currentFileIndex]);
//
//    }

    private void handleDoubleClick(Boolean b) {
        int nextIndex;
        if (b == true) {
            nextIndex = currentFileIndex + 1;
            if (nextIndex >= allFiles.length) {
                nextIndex = 0;
            }
        } else {
            nextIndex = currentFileIndex - 1;
            if (nextIndex < 0) {
                nextIndex = allFiles.length - 1;
            }
        }
        playAudio(allFiles[nextIndex]);
        currentFileIndex = nextIndex;
    }

    private void handleSingleClick(Boolean b) {
        if (b == true) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (currentPosition + 1000 < duration) {
                    mediaPlayer.seekTo(currentPosition + 1000);
                } else {
                    mediaPlayer.seekTo(duration);
                }
            }
        } else {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int p = currentPosition - 1000;
                    if (p < 0) {
                        p = 0;
                    }
                    mediaPlayer.seekTo(p);
                }
        }

    }

}

