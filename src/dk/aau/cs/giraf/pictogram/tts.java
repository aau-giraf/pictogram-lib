package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.*;

/**
 * Created by Christian on 28-04-14.
 */
public class tts implements Runnable{
    String soundURL;
    Context c;
    public byte[] SoundData = null;

    public tts (Context c)
    {
        this.c = c;
    }

    public void PlayText(String textToPlay)
    {
        soundURL = "http://www.translate.google.com/translate_tts?ie=UTF-8&q="+URLEncoder.encode(textToPlay)+"&tl=da_dk";
    }

    public boolean NoSound(dk.aau.cs.giraf.oasis.lib.models.Pictogram p)
    {
        if(isNetworkAvailable())
        {
            PlayText(p.getInlineText());
            Runnable task = this;
            Thread worker = new Thread(task);
            worker.start();
            try{
                worker.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            p.setSoundDataBytes(this.SoundData);
            PictogramController pictogramController = new PictogramController(c);
            pictogramController.modifyPictogram(p);
            return true;
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void run() {
        SoundData = DownloadFile();
    }

    public byte[] DownloadFile() {
        try{
            URL url = new URL(soundURL);

            long startTime = System.currentTimeMillis();
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1)
                baf.append((byte) current);

            return baf.toByteArray();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
