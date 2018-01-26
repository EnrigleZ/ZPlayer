package cc.zhouyc.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer {
	
	// Define player status by enum Status.
	static public enum Status {
		NOT_STARTED, PLAYING, PAUSED, FINISHED
		};
	
	private MusicList musicList;
	
	private Player player;
	
	// sync的线程信号量
	private Object playerLock = new Object();
	
	private String strPlayOrder = "";
	
	private Status isPlaying; // 应该初始化为 Status.NOT_STARTED;
	
	private Runnable runnableMusic;
	
	private Thread threadMusic;
	
	// 在这里显示音乐名
	private Label labelDescription;
	
	public MusicPlayer() {
		setPlayOrder("sequence");
		player = null;
		musicList = new MusicList();
		setPlaying(Status.NOT_STARTED);
}

	public boolean addMusic(Music music) {
		return musicList.addMusic(music);
	}
	
	public Integer getMusicNumber() {
		return musicList.getMusicNumber();
	}
	
	// play()和pause() 应该注意是从断点开始继续播放
	public boolean play() throws FileNotFoundException, JavaLayerException {
		Music music = musicList.getCurrentMusic();
		//threadMusic.
		if (music == null) {
			System.out.println("No Music!");
			return false;
		}
		
		synchronized (playerLock) {
			if (getPlaying() == Status.PLAYING || getPlaying() == Status.PAUSED)
				setPlaying(Status.FINISHED);
			else setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();
			
			System.out.println(getPlaying());
			
			while (getPlaying() == Status.FINISHED) {
				System.out.println("getPlaying() == Status.FINISHED");	
				try{
					playerLock.wait();
					
				} catch (final InterruptedException e) {
					break;
				}
			}
    		

			System.out.println("2");
			
			FileInputStream musicInputStream = new FileInputStream(music.getFilepath());
			player = new Player(musicInputStream);
			
			runnableMusic = new Runnable()
			{
				@Override
				public void run() {
					 playInternal();
				}
            };
            threadMusic = new Thread(runnableMusic);
            //playerStatus = PLAYING;
            threadMusic.start();
		}  
		labelDescription.setText(music.getDescription());
		System.out.println("Play: " + music.getFilepath());
		
		setPlaying(Status.PLAYING);
		
		return true;
	}
	
	public void pause() {
		synchronized (playerLock) {
			setPlaying(Status.PAUSED);
			System.out.println("Pause");
		}
	}
	
	public boolean conti() {
		
		System.out.println("Resume");
		synchronized (playerLock) {
			if (getPlaying() == Status.PAUSED) {
				setPlaying(Status.PLAYING);
				playerLock.notifyAll();
			}
		}
		return getPlaying() == Status.PLAYING;
	}
	public void playNext() throws FileNotFoundException, JavaLayerException {
		musicList.nextMusicIndex(getPlayOrder());
		play();
	}
	
	public void playPrev() throws FileNotFoundException, JavaLayerException {
		musicList.prevMusicIndex();
		play();
	}
	
	public void setCurrentMusicIndex(int index) {
		musicList.setCurrentMusicIndex(index);
	}
	
	public int getCurrentMusicIndex() {
		return musicList.getCurrentMusicIndex();
	}

	public Status getPlaying() {
		return isPlaying;
	}

	public void setPlaying(Status isPlaying) {
		this.isPlaying = isPlaying;
	}

	public String getPlayOrder() {
		return strPlayOrder;
	}

	public void setPlayOrder(String strPlayOrder) {
		this.strPlayOrder = strPlayOrder;
	}
	
	public boolean removeMusic(Music music) {
		return musicList.removeMusic(music);
	}
	// 用于数据绑定
	public ObservableList<Music> getBindList() {
		return musicList.getBindList();
	}
	
	public void setLabelDescription(Label labelDescription) {
		this.labelDescription = labelDescription;
	}
	
	public void playInternal() {

		while (getPlaying() != Status.FINISHED) {//
    		try {
    			if (!player.play(1)) 
    				break;
    		} catch (final JavaLayerException e) {
    			break;
    		}
    		//System.out.println("player.play(1);");
    		synchronized (playerLock) {
    			while (getPlaying() == Status.PAUSED) {
    				System.out.println("getPlaying() == Status.PAUSED");
    				try{
    					playerLock.wait();
    					
    				} catch (final InterruptedException e) {
    					break;
    				}
    			}
    		}
    	}
		
		// NOTE:
		// 切到下一首歌时，通过改变FINISHED为NOT_STARTED
		// 表示本线程播放已经结束
		// 否则播放会出问题
		// 这里要采用set + notifyAll的方式和play()中的wait()打配合
		// 才能确保音乐的确关了，不会出现还有0.1s fraction片段没播放完的情况
		
		synchronized (playerLock) {
			setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();
		}
		
		
		System.out.println("done");
		return;
	}
	

}
