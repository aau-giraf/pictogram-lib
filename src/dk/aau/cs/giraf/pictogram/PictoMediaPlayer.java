package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.FileInputStream;
import java.io.IOException;

import dk.aau.cs.giraf.oasis.lib.models.Tag;

/**
 * Created by Praetorian on 28-04-14.
 */
public class PictoMediaPlayer {
    private MediaPlayer mediaPlayer;
    private Context activity;
    private boolean hasSound;
    private boolean isPlaying;
    private CompleteListener customListener;

    public boolean isPlaying(){
        return isPlaying;
    }

    public  PictoMediaPlayer (Context activity, String path)
    {
        this.activity = activity;
        assignMediaPlayer();
        setDataSource(path);
    }

    public PictoMediaPlayer(Context activity){
        this.activity = activity;
        assignMediaPlayer();
    }


    private float getVolume(){
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }

    public void setDataSource(String path){
        if(hasSound)
        {
            mediaPlayer.release();
            assignMediaPlayer();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(path);

            mediaPlayer.setDataSource(fileInputStream.getFD());
            hasSound = true;
        }
        catch (IOException e)
        {
            e.getStackTrace();
        }
    }

    public void setCustomListener(CompleteListener completeListener){
        this.customListener = completeListener;
    }




    public void stopSound(){
        isPlaying = false;
        mediaPlayer.stop();
    }

    public void playSound(){
        if(isPlaying)
        {
            stopSound();
        }
        if(hasSound)
        {
            isPlaying = true;
            mediaPlayer.prepareAsync();
        }
    }

    private void assignMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    private final MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.setVolume(getVolume(), getVolume());
            mediaPlayer.start();
        }
    };

    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            isPlaying = false;
            if(customListener != null){
                customListener.soundDonePlaying();
            }
        }
    };

    public interface CompleteListener{
        public void soundDonePlaying();
    }

}
