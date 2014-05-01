package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Christian on 28-04-14.
 */
public class tts implements Runnable{
    String imageURL;
    String fileName;
    Context c;
    public byte[] SoundData = null;

    public tts (Context c)
    {
        this.c = c;
    }

    public void PlayText(String textToPlay)
    {
        imageURL = "http://www.translate.google.com/translate_tts?ie=UTF-8&q="+URLEncoder.encode(textToPlay)+"&tl=da_dk";
        fileName = "test.mp3";
    }

    @Override
    public void run() {
        SoundData = DownloadFile(imageURL, fileName);
    }

    public byte[] DownloadFile(String imageURL, String fileName) {
        try{
            URL url = new URL(imageURL);

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
