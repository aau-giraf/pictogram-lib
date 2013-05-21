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
 * PictoFactory is a factory class which turns OasisLib Media + submedia in to
 * pictograms which can be used as views.
 *
 * The current version requires the Oasis database to be on the tablet but in
 * the future it should be possible to make it work with the Wasteland model
 * database.
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

                if(subs.size() > 0){
                    String msg = "Found media %s(%d) of type %s";
                    boolean audioSet = false;

                    for(Media m : subs){
                        String subMediaType = m.getMType();
                        if(subMediaType.equalsIgnoreCase("word") ||
                           subMediaType.equalsIgnoreCase("sound")){
                            aud = m.getMPath();

                            msg = String.format(msg, m.getName(), m.getId(), subMediaType);
                            Log.d(TAG, msg);
                            audioSet = true;
                            break;
                        }
                    }

                    if(!audioSet){
                        msg = "No appropriate sub media found in %s(%d), using null.";
                        msg = String.format(msg, media.getName(), media.getId());
                        Log.d(TAG, msg);
                    }

                } else {
                    String msg = "Found no sub media in %s(%d), using null.";
                    msg = String.format(msg, media.getName(), media.getId());

                    Log.d(TAG, msg);
                }

                pictogram = new Pictogram(context,
                                          media.getMPath(),
                                          media.getName(),
                                          aud,
                                          media.getName(),
                                          media.isMPublic(),
                                          media.getId());

                return pictogram;
            } else {
                String msg = "Media id %m(%d) not found to be of type IMAGE.";
                msg = String.format(msg, media.getName(), media.getId());

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
     * @return a list of pictograms, can be null.
     */
    public static List<Pictogram> getPictogramsByTag(Context context, String tag){
        databaseHelper = new Helper(context);
        TagsHelper tagsHelper = databaseHelper.tagsHelper;
        MediaHelper mediaHelper = databaseHelper.mediaHelper;

        List<Pictogram> pictograms = new ArrayList();
        List<Tag> matchingTags = new ArrayList();
        List<Media> matchedMedias = new ArrayList();

        matchingTags = tagsHelper.getTagsByCaption(tag);

        if(matchingTags.isEmpty()){
            return null;
        }

        matchedMedias = mediaHelper.getMediaByTags(matchingTags);

        pictograms = convertMedias(context, matchedMedias);

        return pictograms;
    }

    /**
     * Gets a list of pictograms from a collection of tags.
     *
     * <p>Get all Pictograms that match a list of tags.
     * @param context the context in which the method is executed.
     * @param tags the tags which should be found. This can be any collection type.
     * @return a list of pictograms, this can be null.
     */
    public static List<Pictogram> getPictogramsByTags(Context context, Collection<String> tags){
        databaseHelper = new Helper(context);
        MediaHelper mediaHelper = databaseHelper.mediaHelper;
        TagsHelper tagsHelper = databaseHelper.tagsHelper;
        List<Tag> matchingTags = new ArrayList();
        List<Pictogram> pictograms = new ArrayList();
        List<Media> matchedMedias = new ArrayList();

        for(String t : tags){
            matchingTags = tagsHelper.getTagsByCaption(t);
        }

        if(matchingTags.isEmpty()){
            return null;
        }

        matchedMedias = mediaHelper.getMediaByTags(matchingTags);

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
