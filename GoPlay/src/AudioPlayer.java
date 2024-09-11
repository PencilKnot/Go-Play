import java.io.File; 
import javax.sound.sampled.*;

public class AudioPlayer {
    // Variable Initialization
    private Clip clip;

    // Given file path, open and play the audio file
    public void play(String filePath){
        try {
            File audio = new File(filePath);
            if(audio.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(audio);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // keep music looped
                clip.start();
            }
            else
                System.out.println("File not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        clip.stop();
    }
}
