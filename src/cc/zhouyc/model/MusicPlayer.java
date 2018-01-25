package cc.zhouyc.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javazoom.jl.player.Player;

public class MusicPlayer extends Thread{
	
	private MusicList musicList;
	
	private Player player;
	
	private String strPlayOrder = "";
	
	private boolean isPlaying;
	
	// 在这里显示音乐名
	private Label labelDescription;
	
	public MusicPlayer() {
		setPlayOrder("sequence");
		player = null;
		musicList = new MusicList();
		setPlaying(false);
		this.labelDescription =labelDescription; 
	}

	public boolean addMusic(Music music) {
		return musicList.addMusic(music);
	}
	
	public Integer getMusicNumber() {
		return musicList.getMusicNumber();
	}
	
	// play()和pause() 应该注意是从断点开始继续播放
	public boolean play() {
		Music music = musicList.getCurrentMusic();
		if (music == null) {
			System.out.println("No Music!");
			return false;
		}
		labelDescription.setText(music.getDescription());
		System.out.println("Play: " + music.getFilepath());
		
		setPlaying(true);
		
		return true;
	}
	
	public void pause() {
		System.out.println("Pause");
	}
	
	public void playNext() {
		musicList.nextMusicIndex(getPlayOrder());
		play();
	}
	
	public void playPrev() {
		musicList.prevMusicIndex();
		play();
	}
	
	public void setCurrentMusicIndex(int index) {
		musicList.setCurrentMusicIndex(index);
	}
	
	public int getCurrentMusicIndex() {
		return musicList.getCurrentMusicIndex();
	}
	
	public void run() {
		try { 

			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
	
	}

	public boolean getPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
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
}
