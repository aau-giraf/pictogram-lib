package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dk.aau.cs.giraf.gui.GToast;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.*;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;

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

    public PictoMediaPlayer (Context activity, String path)
    {
        this.activity = activity;
        assignMediaPlayer();
        setDataSource(path);
    }

    public PictoMediaPlayer (Context activity, dk.aau.cs.giraf.oasis.lib.models.Pictogram pictogram)
    {
        this.activity = activity;
        assignMediaPlayer();

        setDataSource(pictogram);
    }

    public PictoMediaPlayer (Context activity, byte[] byteArray)
    {
        this.activity = activity;
        assignMediaPlayer();

        setDataSource(byteArray);
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
            if(path != null)
            {
                FileInputStream fileInputStream = new FileInputStream(path);

                mediaPlayer.setDataSource(fileInputStream.getFD());
                hasSound = true;
            }
            else
            {
                hasSound = false;
                String text = "Lyden kunne ikke afspilles, da du ikke er på nettet.";
                GToast toast = GToast.makeText(activity, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        catch (IOException e)
        {
            e.getStackTrace();
        }
    }

    public void setDataSource(Pictogram pictogram){
        if(pictogram != null)
        {
            try{
                File picFile = pictogram.getAudioFile(activity);
                if(picFile == null)
                {
                    boolean check = NoSound(pictogram);
                    if(check)
                    {
                        setDataSource(pictogram.getAudioFile(activity).getPath());
                    }
                    else
                    {
                        setDataSource((String)null);
                    }
                }
                else
                {
                    setDataSource(pictogram.getAudioFile(activity).getPath());
                }
            }
            catch (IOException e)
            {
                e.getStackTrace();
            }
        }
        else
        {
            hasSound = false;
        }
    }

    public void setDataSource(byte[] byteArray){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String fileName = dateFormat.format(new Date());

        File newFile = new File(activity.getCacheDir().getPath() + File.separator + fileName);

        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(newFile.getPath());

            fileOutputStream.write(byteArray);
            fileOutputStream.close();

            setDataSource(newFile.getPath());
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

    private boolean NoSound(Pictogram p)
    {
        if(isNetworkAvailable())
        {
            tts t = new tts(activity);
            t.PlayText(p.getInlineText());
            Runnable task = t;
            Thread worker = new Thread(task);
            worker.start();
            try{
                worker.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            PictogramController pictogramController = new PictogramController(activity);
            pictogramController.modifyPictogram(p);
            return true;
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


