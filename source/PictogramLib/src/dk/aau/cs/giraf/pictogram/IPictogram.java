package dk.aau.cs.giraf.pictogram;

public interface IPictogram {

        public void renderAll();
        public void renderText();
        public void renderImage();
        public void playAudio();
        public String[] getTags();
        public String getImageData();
        public String getAudioData();
        public String getTextData();

}
