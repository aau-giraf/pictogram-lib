package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;

import dk.aau.cs.giraf.dblib.Helper;
import dk.aau.cs.giraf.dblib.models.*;
import dk.aau.cs.giraf.dblib.controllers.*;


//TODO: Make this a service that applications can hook to
//TODO: If made local, set it to run in seperate thread (DBsync and traversing can be costly)

/**
 * PictoFactory is a factory class which turns OasisLib Pictograms + submedia in to
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
        if (context == null)
        {
            return null;
        }

        PictogramController pictogramController = new PictogramController(context);

        List<Pictogram> allPictograms = new ArrayList<Pictogram>();
        List<dk.aau.cs.giraf.dblib.models.Pictogram> allPictogramsOasis = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();

        try
        {
            allPictogramsOasis = pictogramController.getPictograms();
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }

        if (!allPictogramsOasis.isEmpty() && !allPictogramsOasis.contains(null))
        {
            for (dk.aau.cs.giraf.dblib.models.Pictogram pictogramOasis : allPictogramsOasis)
            {
                try
                {
                    Pictogram pictogramPictogramLib = convertPictogram(context, pictogramOasis);
                    allPictograms.add(pictogramPictogramLib);
                }
                catch (Exception e)
                {
                    e.getStackTrace();
                }
            }
        }
        else
        {
            return null;
        }

        return allPictograms;
    }

    /**
     * Takes any pictogram from the oasis library and converts it to a pictogram.
     *
     * <p> Be warned that the first piece of submedia found will be set
     * as the audio for the pictogram, so if there is a mistake in the DB
     * it will live on by this function.
     *
     * @param context the context in which the method is executed.
     * @param pictogramOasis a pictogram object from OasisLib to be converted to a pictogram
     * @return a pictogram that matches the Oasis pictogram
     * @throws IllegalArgumentException if the pictogram is not found to be of the
     *             correct type it will be rejected with this exception.
     */
    public static Pictogram convertPictogram(Context context, dk.aau.cs.giraf.dblib.models.Pictogram pictogramOasis) throws IllegalArgumentException{
        try
        {
                Pictogram pictogramPictogramLib = new Pictogram(pictogramOasis.getId(), pictogramOasis.getName(), pictogramOasis.getPub(),
                    pictogramOasis.getImageData(), pictogramOasis.getSoundData(),
                    pictogramOasis.getInlineText(), pictogramOasis.getAuthor(), context);

                return pictogramPictogramLib;
        }
        catch(NullPointerException e)
        {
            String msg = "Null object passed to convertMedia.";
            Log.e(TAG, msg);

            return null;
        }
    }


    /**
     * Takes a collection of pictograms and converts it to a list of pictograms.
     *
     * <p> Be warned that the first piece of submedia found will be set
     * as the audio for the pictogram, so if there is a mistake in the DB
     * it will live on by this function.
     *
     * @param context the context in which the method is executed.
     * @param pictogramsOasis a collection of pictograms matching Oasis models.
     * @return a list of pictograms, converted from the pictograms
     * @throws IllegalArgumentException if the pictogram is not found to be of the
     *             correct type it will be rejected with this exception.
     */
    public static List<Pictogram> convertPictograms(Context context, Collection<dk.aau.cs.giraf.dblib.models.Pictogram> pictogramsOasis)
    {
        try
        {
            List<Pictogram> pictograms = new ArrayList<Pictogram>();

            for(dk.aau.cs.giraf.dblib.models.Pictogram pictogramOasis : pictogramsOasis){
                try
                {
                    pictograms.add(convertPictogram(context, pictogramOasis));
                }
                catch (IllegalArgumentException exc){
                    // we ignore this exception because there is no need to do
                    // anything about misses in the database.
                }
            }
            return pictograms;
        }
        catch(NullPointerException e) {
            String msg = "Null object passed to convertMedias.";
            Log.e(TAG, msg);
            return null;
        }
    }

    /**
     * Gets all pictograms owned by a specific profile.
     * @param context the context in which the method is executed.
     * @param profile the profile from which the pictograms will be lifted.
     * @return a {@link } of pictograms.
     */
    public static List<Pictogram> getPictogramsByProfile(Context context, Profile profile){
        List<Pictogram> pictogramsPictogramLib = new ArrayList<Pictogram>();
        List<dk.aau.cs.giraf.dblib.models.Pictogram> pictogramsOasis;

        PictogramController pictogramController = new PictogramController(context);

        pictogramsOasis = pictogramController.getPictogramByProfile(profile);

        pictogramsPictogramLib = convertPictograms(context, pictogramsOasis);

        return pictogramsPictogramLib;
    }


    /**
     * <p>Get all Pictograms that match a tag.
     * @param context the context in which the method is executed.
     * @param tag the tag which should be found.
     * @return a list of pictograms, can be null.
     */
    public static List<Pictogram> getPictogramsByTag(Context context, Tag tag){
        PictogramController pictogramController = new PictogramController(context);
        List<Pictogram> pictogramsPictogramLib = new ArrayList<Pictogram>();
        List<dk.aau.cs.giraf.dblib.models.Pictogram> pictogramsOasis;


        pictogramsOasis = pictogramController.getPictogramsByTag(tag);

        pictogramsPictogramLib = convertPictograms(context, pictogramsOasis);

        return pictogramsPictogramLib;
    }

    /**
     * Gets a list of pictograms from a collection of tags.
     *
     * <p>Get all Pictograms that match a list of tags.
     * @param context the context in which the method is executed.
     * @param tags the tags which should be found. This can be any collection type.
     * @return a list of pictograms, this can be null.
     */
    public static List<Pictogram> getPictogramsByTags(Context context, Collection<Tag> tags){
        PictogramController pictogramController = new PictogramController(context);
        List<Pictogram> pictogramsPictogramLib = new ArrayList<Pictogram>();

        for (Tag tag : tags)
        {
            pictogramsPictogramLib.addAll(getPictogramsByTag(context, tag));
        }

        return pictogramsPictogramLib;
    }
    /**
     * Gets a specific pictogram from the database.
     * @param context the context in which the method is executed.
     * @param pictogramId a number identifying the pictogram in the database.
     * @return a pictogram.
     */
    public static Pictogram getPictogram(Context context, int pictogramId){
        PictogramController pictogramController = new PictogramController(context);
        Pictogram pictogram;
        pictogram = convertPictogram(context, pictogramController.getById(pictogramId));

        return pictogram;
    }

    /**
     * Takes any pictogram from the oasis library and converts it to a pictogram.
     *
     * <p> Be warned that the first piece of submedia found will be set
     * as the audio for the pictogram, so if there is a mistake in the DB
     * it will live on by this function.
     *
     * @param context the context in which the method is executed.
     * @param pictogramOasis a pictogram object from OasisLib to be converted to a pictogram
     * @return a pictogram that matches the Oasis pictogram
     * @throws IllegalArgumentException if the pictogram is not found to be of the
     *             correct type it will be rejected with this exception.
     */
    public static dk.aau.cs.giraf.dblib.models.Pictogram convertPictogramToOasis(Context context, Pictogram pictogramPictogramLib)
                                                                                    throws IllegalArgumentException{
        try
        {
            dk.aau.cs.giraf.dblib.models.Pictogram pictogramOasis = new dk.aau.cs.giraf.dblib.models.Pictogram();

            pictogramOasis.setId(pictogramPictogramLib.getPictogramID());
            pictogramOasis.setAuthor(pictogramPictogramLib.getAuthorID());
            pictogramOasis.setName(pictogramPictogramLib.getName());
            if (pictogramPictogramLib.getShareable() == true)
            {
                pictogramOasis.setPub(1);
            }
            else
            {
                pictogramOasis.setPub(0);
            }
            pictogramOasis.setImage(pictogramPictogramLib.getImageData());
            pictogramOasis.setSoundDataBytes(pictogramPictogramLib.getSoundData());
            pictogramOasis.setInlineText(pictogramPictogramLib.getTextLabel());


            return pictogramOasis;
        }
        catch(NullPointerException e)
        {
            String msg = "Null object passed to convert Pictogram.";
            Log.e(TAG, msg);

            return null;
        }
    }
}
