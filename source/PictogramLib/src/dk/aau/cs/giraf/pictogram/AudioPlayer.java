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

/**
 * @author Croc
 * @author digiPECS
 */
public enum AudioPlayer{
    INSTANCE;
    private static MediaPlayer mediaPlayer;
    private final static String TAG = "AudioPlayer";

    /**
     * Opens the {@link MediaPlayer} this is called as soon as the object
     * appears in memory.
     */
    private AudioPlayer(){
        open();
    }

    /**
     * Creates the {@link MediaPlayer} object, this object is used for playing
     * audio.
     */
    public static void open(){
        mediaPlayer = new MediaPlayer();
    }

    /**
     * Releases the {@link MediaPlayer}, making it a null object.
     */
    public static void close(){
        mediaPlayer.release();
    }
    
    /**
     * This method tries to reset the {@link MediaPlayer}. If it did not
     * succeed it will retry, closing and opening it.
     */
    public static void reset(){
        try{
            Log.i(TAG,"Resetting AudioPlayer.");
            mediaPlayer.reset();
        } catch (IllegalStateException e){
            Log.e(TAG,"Could not reset AudioPlayer.");
            Log.i(TAG, "Attempting to reopen AudioPlayer.");
            close();
            open();
        }
    }

    /**
     * Plays a specific piece of audio.
     * @param path the path to a piece of audio.
     */
    public void play(final String path){
        play(path, null);
    }

    /**
     * Plays a specific piece of audio. Taking a listener for use when the
     * audio has finished playing.
     * @param path the path to a piece of audio.
     * @param listener the callback that will be run
     */
    public void play(final String path, final OnCompletionListener listener){
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
            //TODO make this exception more informative.
            Log.e(TAG, "play throws an IOException.");
            e.printStackTrace();
        }
    }
}
