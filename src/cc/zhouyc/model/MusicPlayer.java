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
		NOT_STARTED, PLAYING, PAUSED, FINISHED, FINISHED_AUTO
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

	public boolean play() {
		Music music = musicList.getCurrentMusic();
		//threadMusic.
		if (music == null) {
			System.out.println("No Music!");
			return false;
		}
		
		synchronized (playerLock) {
			System.out.println("Before: getPl() == " + getPlaying());
			if (getPlaying() == Status.PLAYING || getPlaying() == Status.PAUSED)
				setPlaying(Status.FINISHED);
			else setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();	// 正在播放的线程会接受这个信息，然后将状态置为NOT_STARTED
			
			System.out.println(getPlaying());
			// 在没有写自动下一曲的代码时，这里会出现问题，一直卡在FINISHED
			
			if (getPlaying() != Status.FINISHED_AUTO) {
				while (getPlaying() == Status.FINISHED) {
					System.out.println("getPlaying() == Status.FINISHED");	
					try{
						playerLock.wait();
						
					} catch (final InterruptedException e) {
						break;
					}
				}
			}
    		// Now: isPlaying: NOT_STARTED

			System.out.println("2");
			
			
			// TODO: 自动切到下一首
			
			runnableMusic = new Runnable() {
				@Override
				public void run() {
					try {
						FileInputStream musicInputStream = new FileInputStream(music.getFilepath());
						player = new Player(musicInputStream);
					} catch (Exception e) {
						return;
					}
					playInternal();
				}
            };
            threadMusic = new Thread(runnableMusic);
            //playerStatus = PLAYING;
            threadMusic.start();
		
			labelDescription.setText(music.getDescription());
			System.out.println("Play: " + music.getFilepath());
			
			setPlaying(Status.PLAYING);
		}
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
    			if (!player.play(1)) {
    				synchronized (playerLock) {
    					if (getPlaying() == Status.FINISHED) {
    						// 由于player.play()的延时，导致了不同步
    						break;
    					}
    					setPlaying(Status.FINISHED_AUTO);	
    					// System.out.println("自然完成" + getPlaying());
					}
    				return;
    			}
    		} catch (final JavaLayerException e) {
    			synchronized (playerLock) {
    				setPlaying(Status.FINISHED_AUTO);
				}
    			return;
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
		// 当因为外部手动换歌，而非自动完成跳出循环时，会到以下部分代码
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
