package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

//TODO: Make this a service that applications can hook to
//TODO: If made local, set it to run in seperate thread (DBsync and traversing can be costly)
public enum PictoFactory {
    INSTANCE;

    private ArrayList<String> tempImageDatabase = new ArrayList<String>();
    private ArrayList<String> tempAudioDatabase = new ArrayList<String>();
    private ArrayList<String> tempTextDatabase = new ArrayList<String>();

    public void repopulateTemporaryDatabase(){
        String _tempAudioDatabase[] = {"/bade.wma",
                                       "/drikke.wma",
                                       "/du.wma",
                                       "/film.wma",
                                       "/ja.wma",
                                       "/lege.wma",
                                       "/mig.wma",
                                       "/nej.wma",
                                       "/se.wma",
                                       "/stop.wma",
                                       "/sulten.wma"};

        String _tempImageDatabase[] = {"/Bade.png",
                                       "/Drikke.png",
                                       "/Du.png",
                                       "/Film.png",
                                       "/Ja.png",
                                       "/Lege.png",
                                       "/Mig.png",
                                       "/Nej.png",
                                       "/Se.png",
                                       "/Stop.png",
                                       "/Sulten.png"};

        String _tempTextDatabase[] = {"Bade",
                                      "Drikke",
                                      "Du",
                                      "Film",
                                      "Ja",
                                      "Lege",
                                      "Mig",
                                      "Nej",
                                      "Se",
                                      "Stop",
                                      "Sulten"};

        // honestly I didn't check but I expect that they are exactly the same length.
        int length = _tempImageDatabase.length;
        String storagePath = Environment.getExternalStorageDirectory().getPath() + "/Pictogram";

        for(int i = 0; i < length; i++){

            tempImageDatabase.add(storagePath + _tempImageDatabase[i]);
            tempTextDatabase.add(_tempTextDatabase[i]);
            tempAudioDatabase.add(storagePath + _tempAudioDatabase[i]);
        }
    }

    /**
     *
     */
    public Pictogram getPictogram(Context context, long pictogramID) {

        // Imagine a database of pictograms with tags and
        // beautiful text for plastering on to them here.
        //
        // Imagine also that it was possible to load whole
        // collections of these things just by the switch
        // of a method.

        if(tempImageDatabase.isEmpty()){
            // completely arbirary, but hey!
            repopulateTemporaryDatabase();
        }

        String pic = tempImageDatabase.get((int)pictogramID);
        String aud = tempAudioDatabase.get((int)pictogramID);
        String text = tempTextDatabase.get((int)pictogramID);

        //TODO replace this when a new snappier version of
        // Pictogram gets implemented.
        Pictogram pictogram = new Pictogram(context, pic, text, aud, pictogramID);

        return pictogram;
    }
}
