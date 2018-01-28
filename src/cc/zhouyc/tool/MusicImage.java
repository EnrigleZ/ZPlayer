package cc.zhouyc.tool;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import cc.zhouyc.model.Music;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class MusicImage {
	
	final static String defaultBackgroundImagePath = "./img/bg.jpeg";
	public Image image = null;
	/**
	 * Get music picture from music file.
	 * @param filename
	 */
	public MusicImage(String filename) {
		image = getMusicPicFromPath(filename);
		if (image == null) {
			try {
				System.out.println(new File(defaultBackgroundImagePath).getAbsolutePath());
				image = new Image(new File(defaultBackgroundImagePath).toURI().toURL().toString());
			} catch (Exception e) {}
		}
	}
	
	public Background getBackground(double width, double height) {
		 final ImageView imv = new ImageView();
	        imv.setImage(image);
	       // imv.resize(width, height);
//	        imv.setOpacity(0.2);
//	        System.out.println("set Opacity 0.5");
	        BackgroundImage backgroundImage = new BackgroundImage(image, 
	        		BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
	        		BackgroundPosition.CENTER, new BackgroundSize(width, height, false, false, true, true)
	        		);
	        return new Background(backgroundImage);
	}
	
	private static Image getMusicPicFromPath(String filepath) {
		try {
			System.out.println("Getting pic from '" + filepath + "'");
			File sourceFile = new File(filepath);  
	        MP3File mp3file = new MP3File(sourceFile);  
	          
	        AbstractID3v2Tag tag = mp3file.getID3v2Tag();  
	        AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");  
	        FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();  
	        byte[] imageData = body.getImageData();
	        
	        Image image = new Image(new ByteArrayInputStream(imageData));
	        return image;
	    	
		} catch (Exception e) {
			System.out.println("cannot get pic from music file.");
			return null;
		}
	}
	
	public static void main(String[] args) {
		String path = "E:\\FormerCourses\\interpretation\\09_6.mp3";
		MusicImage musicImage = new MusicImage(path);
		
	}
}
