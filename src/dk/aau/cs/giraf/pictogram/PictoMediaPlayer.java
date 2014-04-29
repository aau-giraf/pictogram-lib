package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

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

    private float getVolume(){
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }

    public void setDataSource(String path) throws IOException{
        if(hasSound)
        {
            mediaPlayer.release();
            assignMediaPlayer();
        }

        FileInputStream fileInputStream = new FileInputStream(path);

        mediaPlayer.setDataSource(fileInputStream.getFD());
        hasSound = true;
    }

    public void setCustomListener(CompleteListener completeListener){
        this.customListener = completeListener;
    }

    public PictoMediaPlayer(Context activity){
        this.activity = activity;
        assignMediaPlayer();
    }


    public void stopMusic(){
        mediaPlayer.stop();
    }

    public void playSound(){
        if(isPlaying)
            stopMusic();
        if(hasSound)
            mediaPlayer.prepareAsync();
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
            isPlaying = true;
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
