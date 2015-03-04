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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.media.AudioTrack;

import java.io.IOException;



/**
 * @author Croc
 * @author digiPECS
 */
public enum AudioPlayer{
    INSTANCE;
    private static AudioTrack audioTrack;
    private final static String TAG = "AudioPlayer";
    private boolean stillPlaying = true;
    private int soundFrequency = 44100;
    private Thread soundThread;

    /**
     * Opens the {@link MediaPlayer} this is called as soon as the object
     * appears in memory.
     */
    private AudioPlayer(){
    }





    
    /**
     * This method tries to reset the {@link MediaPlayer}. If it did not
     * succeed it will retry, closing and opening it.
     */
    public static void reset(){
        try{
            Log.i(TAG, "Resetting AudioPlayer.");


        } catch (IllegalStateException e){
            Log.e(TAG,"Could not reset AudioPlayer.");
            Log.i(TAG, "Attempting to reopen AudioPlayer.");
        }
    }

    /**
     * Plays a specific piece of audio.
     * @param path the path to a piece of audio.
     */
    public void play(final byte[] sound_data){//Blob type should be changed to whatever OasisLib returns as sound.
        play(sound_data, null);
    }

    /**
     * Plays a specific piece of audio. Taking a listener for use when the
     * audio has finished playing.
     * @param path the path to a piece of audio.
     * @param listener the callback that will be run
     */
    public void play(final byte[] sound_data, final OnCompletionListener listener){
        //TODO find out if we should stop any ongoing playback or not, current implementation stops playback. Making it an user defined option might be too much?
        //TODO play is blocking, make this not true by implementing a seperate thread?
        try {
            //http://audioprograming.wordpress.com/2012/10/18/a-simple-synth-in-android-step-by-step-guide-using-the-java-sdk/
            soundThread = new Thread()
            {
                public void run()
                {
                    //Set priority, should maybe not be Max, but not sure.
                    setPriority(Thread.MAX_PRIORITY);

                    int minimumBufferSize = AudioTrack.getMinBufferSize(soundFrequency, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);

                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, soundFrequency, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, minimumBufferSize, AudioTrack.MODE_STREAM);

                    int amp = 10000;
                    double twoPi = 8.* Math.tan(1.);
                    double fr = 440.f;
                    double ph = 0.0;

                    audioTrack.play();

                    while (stillPlaying == true)
                    {
                        for (int i=0; i < minimumBufferSize; i++)
                        {
                            sound_data[i] = (byte) (amp * Math.sin(ph));
                            ph += twoPi * fr / soundFrequency;
                        }
                        audioTrack.write(sound_data, 0, minimumBufferSize);
                    }
                    audioTrack.stop();
                    audioTrack.release();
                }
            };
            soundThread.start();

            soundThread.destroy();

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Illegal argument exception, please check that all arguments were correctly formatted.");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, "AudioPlayer was in the   wrong state, try reset.");
            e.printStackTrace();
        } catch (Exception e) {
            //TODO make this exception take proper action instead of just failing and shutting up.
            //TODO make this exception more informative.
            Log.e(TAG, "play throws an Exception.");
            e.printStackTrace();
        }

    }




}
