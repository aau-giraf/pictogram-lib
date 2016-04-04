package dk.aau.cs.giraf.pictogram;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

/**
 * Created by kenneth on 3/1/16.
 */
public class PictoMediaPlayer extends Service implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Queue<dk.aau.cs.giraf.dblib.models.Pictogram> PictoQueue = new LinkedList<dk.aau.cs.giraf.dblib.models.Pictogram>();


    private final IBinder myBinder = new MyLocalBinder();


    /**
     Return the binder for the PictoMediaPlayer

     @return The binder for PictoMediaPlayer
     */
    public class MyLocalBinder extends Binder {
        public PictoMediaPlayer getService() {
            return PictoMediaPlayer.this;
        }
    }

    /**
     * Called when the service is bound, if there is not TextToSpeech it is created
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        if(tts == null)
        {
            tts = new TextToSpeech(this, this);
            tts.setSpeechRate(0.6f);
        }
        return myBinder;
    }

    /**
     *
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("PictoMediaPlayer", "This Language is not supported");
            }

        } else {
            Log.e("PictoMediaPlayer", "Initilization Failed!");
        }
    }

    /**
     * Make the TextToSpeech engine speak the string
     * @param play the string that have to be played
     */
    public void Play(String play) {
        if(tts != null) {
            if(play != null) {
                tts.speak(play, TextToSpeech.QUEUE_ADD, null);
            }
            else
            {
                Log.e("PictoMediaPlayer", "No String to play");
            }
        }
        else
        {
            Log.e("PictoMediaPlayer", "PictoMediaPlayer not Initilizatied");
        }
    }


    /**
     * Make the TextToSpeech engine speak the Pictogram sound
     * @param pictogram
     */
    public void Play(dk.aau.cs.giraf.pictogram.Pictogram pictogram)
    {
        if(pictogram.hasAudio())
        {
            //TODO play the pictograms audio
            //pictogram.playAudio();
        }
        else
        {
            Play(pictogram.getName());
        }
    }

    /**
     * Is any sound being played
     * @return
     */
    public boolean isPlaying()
    {
        return PictoQueue.size() > 0 || tts.isSpeaking();
    }

    /**
     * Make the TextToSpeech engine play a list of pictograms
     * @param PictogramList A ArrayList of Pictograms
     */
    public void playListOfPictograms(ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram> PictogramList)
    {
        for(int i = 0; i < PictogramList.size(); i++)
        {
            if(PictogramList.get(i) != null)
            {
                PictoQueue.add(PictogramList.get(i));
            }
        }
        while (PictoQueue.size() != 0)
        {
            if (!tts.isSpeaking())
            {
                Play(PictoQueue.poll().getName());
            }
            try {
                Thread.sleep(250);
            }
            catch (Exception e)
            {

            }
        }
    }

    /**̈́
     * Stops the TextToSpeech engine from playing the current sound
     */
    public void stopSound()
    {
        tts.stop();
    }
}
