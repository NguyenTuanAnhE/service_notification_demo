package com.example.administrator.broadcastreceiverservicenotificationdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements MusicAdapter.OnMusicClickListener {

    private SongDatabase songDatabase;
    private RecyclerView rcvMusic;
    private MusicAdapter musicAdapter;
    private ArrayList<Song> songs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        rcvMusic = findViewById(R.id.rcv_music);
        requestPermission();


    }

    @Override
    public void onSongClick(int position) {
        Song song = songs.get(position);
        Intent intent = new Intent(MusicActivity.this, MusicService.class);
        intent.setAction("play_song");
        intent.putExtra("selected_song", song);
        startService(intent);
    }

    public void requestPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Log.d("hi", "requestPermission: ");
            songDatabase = new SongDatabase();
            songs = songDatabase.getSongs(this);

            musicAdapter = new MusicAdapter(this, songs, this);
            rcvMusic.setAdapter(musicAdapter);
            rcvMusic.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("hi", "onRequestPermissionsResult: ");
                songDatabase = new SongDatabase();
                songs = songDatabase.getSongs(this);

                musicAdapter = new MusicAdapter(this, songs, this);
                rcvMusic.setAdapter(musicAdapter);
                rcvMusic.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Toast.makeText(this, "Permission deny", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(MusicActivity.this, MusicService.class);
        stopService(intent);
        super.onDestroy();
    }
}
