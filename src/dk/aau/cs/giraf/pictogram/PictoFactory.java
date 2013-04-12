package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;

import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.models.*;
import dk.aau.cs.giraf.oasis.lib.controllers.*;


//TODO: Make this a service that applications can hook to
//TODO: If made local, set it to run in seperate thread (DBsync and traversing can be costly)

/**
 *
 * @author Croc
 *
 */
public enum PictoFactory {
    INSTANCE;
    private final static String TAG = "PictoFactory";

    private static Helper databaseHelper;


   /**
    * <b>Do not use this function for anything essential!</b>
    *
    * Gets every single pictogram available in the database currently.
    * @param context the context in which the method is executed.
    * @return all pictograms currently in the database.
    */
    public static List<Pictogram> getAllPictograms(Context context){
        databaseHelper = new Helper(context);
        MediaHelper mediaHelper = databaseHelper.mediaHelper;
        List<Media> allMedia = mediaHelper.getMedia();
        List<Pictogram> allPictograms = new ArrayList<Pictogram>();

        allPictograms = convertMedias(context, allMedia);

        return allPictograms;
    }

    /**
     * Takes any media from the oasis library and converts it to a pictogram.
     *
     * <p> Be warned that the first piece of submedia found will be set
     * as the audio for the pictogram, so if there is a mistake in the DB
     * it will live on by this function.
     *
     * @param context the context in which the method is executed.
     * @param media a media object to be converted to a pictogram
     * @return a pictogram that matches the media
     * @throws IllegalArgumentException if the media is not found to be of the
     *             correct type it will be rejected with this exception.
     */
    public static Pictogram convertMedia(Context context, Media media) throws IllegalArgumentException{
        try{
            if(media.getMType().equalsIgnoreCase("IMAGE")){
                databaseHelper = new Helper(context);
                List<Media> subs = databaseHelper.mediaHelper.getSubMediaByMedia(media);
                String aud = null;
                Pictogram pictogram;

                if(subs.size() == 1){
                    aud = subs.get(0).getMPath();
                } else if(subs.size() > 1){
                    String msg = "Found several sub medias in media id %d, using first sub.";
                    msg = String.format(msg, media.getId());

                    Log.d(TAG, msg);
                    aud = subs.get(0).getMPath();
                } else {
                    String msg = "Found no sub media in %d, using null.";
                    msg = String.format(msg, media.getId());

                    Log.d(TAG, msg);
                }

                pictogram = new Pictogram(context,
                                          media.getMPath(),
                                          media.getName(),
                                          aud,
                                          media.getId());

                return pictogram;
            } else {
                String msg = "Media id %d not found to be of type IMAGE.";
                msg = String.format(msg, media.getId());

                throw new IllegalArgumentException(msg);
            }
        } catch(NullPointerException e) {
            String msg = "Null object passed to convertMedia.";
            Log.e(TAG, msg);

            return null;
        }
    }


    /**
     * Takes a collection of media and converts it to a list of pictograms.
     *
     * <p> Be warned that the first piece of submedia found will be set
     * as the audio for the pictogram, so if there is a mistake in the DB
     * it will live on by this function.
     *
     * @param context the context in which the method is executed.
     * @param medias a collection of medias matching Oasis models.
     * @return a list of pictograms, converted from the medias
     * @throws IllegalArgumentException if the media is not found to be of the
     *             correct type it will be rejected with this exception.
     */
    public static List<Pictogram> convertMedias(Context context, Collection<Media> medias){
        try {
            List<Pictogram> pictograms = new ArrayList<Pictogram>();

            for(Media m : medias){
                try{
                    pictograms.add(convertMedia(context, m));
                } catch (IllegalArgumentException exc){
                    // we ignore this exception because there is no need to do
                    // anything about misses in the database.
                }
            }
            return pictograms;
        } catch(NullPointerException e) {
            String msg = "Null object passed to convertMedias.";
            Log.e(TAG, msg);
            return null;
        }
    }

    /**
     * Gets all pictograms owned by a specific profile.
     * @param context the context in which the method is executed.
     * @param profile the profile from which the medias will be lifted.
     * @return a {@link list} of pictograms.
     */
    public static List<Pictogram> getPictogramsByProfile(Context context, Profile profile){
        List<Pictogram> pictograms = new ArrayList<Pictogram>();
        List<Media> medias;
        databaseHelper = new Helper(context);

        medias = databaseHelper.mediaHelper.getMediaByProfile(profile);

        pictograms = convertMedias(context, medias);
        return pictograms;
    }


    /**
     * <p>Get all Pictograms that match a tag.
     * @param context the context in which the method is executed.
     * @param tag the tag which should be found.
     * @return a pictogram.
     */
    public static List<Pictogram> getPictogramsByTag(Context context, String tag){
        databaseHelper = new Helper(context);
        List<Tag> allTagsEver = databaseHelper.tagsHelper.getTags();
        List<Pictogram> pictograms = new ArrayList();
        ArrayList<Tag> matchingTag = new ArrayList();
        List<Media> matchedMedias = new ArrayList();

        for(Tag t : allTagsEver){
            if(t.getCaption().equals(tag)){
                matchingTag.add(t);
                break;
            }
        }

        if(matchingTag.size() == 0){
            return null;
        }

        matchedMedias = databaseHelper.mediaHelper.getMediaByTags(matchingTag);

        pictograms = convertMedias(context, matchedMedias);

        return pictograms;
    }

    /**
     * <b> Made of bad runtime.</b>
     *
     * <p>Get all Pictograms that match a list of tags.
     *
     * <p> This does not use the getPictogramsByTag method because of the way
     * tags are implemented in the database, every tag has to be checked against
     * the query...
     * @param context the context in which the method is executed.
     * @param tags the tags which should be found. This can be any collection type.
     * @return a list of pictograms.
     */
    public static List<Pictogram> getPictogramsByTags(Context context, Collection<String> tags){
        databaseHelper = new Helper(context);
        List<Tag> allTagsEver = databaseHelper.tagsHelper.getTags();
        ArrayList<Tag> matchingTag = null;
        List<Pictogram> pictograms = new ArrayList();
        List<Media> matchedMedias = new ArrayList();

        for(String tag : tags){
            for(Tag t : allTagsEver){
                if(t.getCaption().equals(tag)){
                    matchingTag.add(t);
                }
            }
        }

        matchedMedias = databaseHelper.mediaHelper.getMediaByTags(matchingTag);

        pictograms = convertMedias(context, matchedMedias);

        return pictograms;
    }
    /**
     * Gets a specific pictogram from the database.
     * @param context the context in which the method is executed.
     * @param pictogramId a number identifying the pictogram in the database.
     * @return a pictogram.
     */
    public static Pictogram getPictogram(Context context, long pictogramId){
        Pictogram pictogram;

        databaseHelper = new Helper(context);

        MediaHelper mediaHelper = databaseHelper.mediaHelper;

        Media media = mediaHelper.getMediaById(pictogramId);

        pictogram = convertMedia(context, media);

        return pictogram;
    }

}
