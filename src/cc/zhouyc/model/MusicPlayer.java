package cc.zhouyc.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class MusicPlayer extends Thread{
	
	MusicList musicList;
	
	Player player;
	
	public void run() {
		try { 
			player = null;
			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		MusicPlayer musicPlayer = new MusicPlayer();
		musicPlayer.start();
	}
}
