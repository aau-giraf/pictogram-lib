/*
 * Copyright (C) 2011 Mette Bank, Rikke Jensen, Kenneth Brodersen, Thomas Pedersen
 *
 * This file is part of digiPECS.
 *
 * digiPECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * digiPECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with digiPECS.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.aau.cs.giraf.pictogram;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;


/*
 * File kindly borrowed from Parrot, which previously borrowed it from digiPECS.
 */
public enum AudioPlayer{
    INSTANCE;
    private static MediaPlayer mediaPlayer;
    private final static String TAG = "GIRAF_Pictogram_AudioPlayer";

    private AudioPlayer(){
        // not entierly sure what this is supposed to accomplish.
        // Log.v(TAG, "index=" + i);

        open();
    }

    // Commented because of a disagreement with people of the greater republic of republics.
    // private static AudioPlayer getInstance(){
    //     if(mInstance==null){
    //         mInstance = new AudioPlayer();
    //     }
    //     return mInstance;
    // }

    /**
     *
     */
    public static void open(){
        mediaPlayer = new MediaPlayer();
    }

    public static void close(){
        mediaPlayer.release();
    }

    public static void reset(){
        try{
            Log.i(TAG,"Resetting AudioPlayer.");
            mediaPlayer.reset();
        } catch (IllegalStateException e){
            // when is an object really a mediaplayer, the state says "When playback!", the communists say "Every object is a mediaplayer!", the religious says "mediaplayer belongs to the JVM!". I say a mediaplayer chooses, an object obeys!
            Log.e(TAG,"Could not reset AudioPlayer.");
            Log.i(TAG, "Attempting to reopen AudioPlayer.");
            close();
            open();
        }
    }

    public static void play(final String path){
        play(path, null);
    }

    public static void play(final String path, final OnCompletionListener listener){
        //TODO find out if we should stop any ongoing playback or not, current implementation stops playback. Making it an user defined option might be too much?
        //TODO play is blocking, make this not true by implementing a seperate thread?
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();

            if (listener != null){
                mediaPlayer.setOnCompletionListener(listener);
            }

            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Illegal argument exception, please check that all arguments were correctly formatted.");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, "AudioPlayer was in the wrong state, try reset.");
            e.printStackTrace();
        } catch (IOException e) {
            //TODO make this exception take proper action instead of just failing and shutting up.
            Log.e(TAG, "play thres an IOException, please check if your path is correct.");
            e.printStackTrace();
        }
    }
}
