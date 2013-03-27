package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer.OnCompletionListener;

import dk.aau.cs.giraf.R;

//TODO: Make custom ImageView and TextView with predefined "niceness"
//TODO: Implement a "onClick" play audio feature
//TODO: Handle empty paths (render empty(img?) if path not found)
public class Pictogram extends FrameLayout implements IPictogram{

        private String imagePath;
        private String textLabel;
        private String audioPath;
        private long pictogramID;
        private Gravity textGravity;

        //Main constructor (no XML)
        public Pictogram(Context context, String image, String text, String audio, long id) {
                super(context);
                this.imagePath = image;
                this.textLabel = text;
                this.audioPath = audio;
                this.pictogramID = id;
        }

        @Override
        public void renderAll() {
                renderImage();
                renderText();
        }

        @Override
        public void renderText() {
                TextView text = new TextView(getContext());
                text.setText(textLabel);
                text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                this.addView(text);
        }

        public void renderText(int gravity) {
                TextView text = new TextView(getContext());
                text.setText(textLabel);
                text.setPadding(10, 10, 10, 10);
                text.setGravity(Gravity.CENTER_HORIZONTAL | gravity);
                this.addView(text);
        }

        @Override
        public void renderImage() {
                Bitmap img = BitmapFactory.decodeFile(imagePath);
                ImageView image = new ImageView(getContext());
                image.setImageBitmap(img);
                this.addView(image);
        }


        @Override
        public void playAudio() {
            playAudio(null);
        }


        public void playAudio(final OnCompletionListener listener){
            new Thread(new Runnable(){
                    public void run(){
                        try{
                            AudioPlayer.INSTANCE.play(audioPath, listener);
                        } catch (Exception e){
                            //TODO Properly catch exceptions thrown by AudioPlayer and handle them.
                        }
                    }
                }).start();
                //TODO check that the thread is stopped again at some point. [OLD PARROT TODO]
        }

        @Override
        public String[] getTags() {
                return null;
        }

        @Override
        public String getImageData() {
                return null;
        }

        @Override
        public String getAudioData() {
                return null;
        }

        @Override
        public String getTextData() {
                return null;
        }

        public String getImagePath() {
                return this.imagePath;
        }

        public String getTextLabel() {
                return this.textLabel;
        }

        public String getAudioPath() {
                return this.audioPath;
        }

        public long getPictogramID() {
                return this.pictogramID;
        }

}
