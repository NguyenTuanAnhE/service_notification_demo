package com.example.administrator.broadcastreceiverservicenotificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    public static final String TAG = MusicService.class.getSimpleName();
    private final int NOTIFICATION_MEDIA = 21;
    private Notification noty;

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("play_song")) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                final Song song = intent.getParcelableExtra("selected_song");
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            startNotification(song);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void startNotification(Song song) {

        noty = new NotificationCompat.Builder(this)
                .setContentTitle(song.getName())
                .setContentText(song.getArtist())
                .setSmallIcon(R.drawable.icon_music)
//                .addAction(android.R.drawable.ic_media_previous, "previous", pendingIntentPrevious)
//                .addAction(mediaPlayer.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
//                        mediaPlayer.isPlaying() ? "play" : "pause", pendingIntentPlay)
//                .addAction(android.R.drawable.ic_media_next, "next", pendingIntentNext)
//                .setContentIntent(pendingIntentContent)
                .setChannelId("channel_1")
                .build();
        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_MEDIA, noty);
        //startForeground(NOTIFICATION_MEDIA, noty);
    }
}
