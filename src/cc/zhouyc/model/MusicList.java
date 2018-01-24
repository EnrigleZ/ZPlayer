package cc.zhouyc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicList {
	private List<Music> list;
	private int intCurrentMusicIndex = -1;
	private Random randomGenerator = new Random(System.currentTimeMillis());
	
	// Constructor
	public MusicList() {
		list = new ArrayList<Music>();
		intCurrentMusicIndex = 0;
		
		// 研究一下List和ArrayList之间的多态关系，以及类型安全性
	}

	public boolean addMusic(Music music) {
		if (list.contains(music)) {
			System.out.println("Music " + music.getFilepath() + " has already existed in the list...");
			return false;
		}
		list.add(music);
		return true;
	}
	
	public boolean removeMusic(Music music) {
		return list.remove(music);
	}
	
	public Music getCurrentMusic() {
		return list.get(intCurrentMusicIndex);
	}
	
	private int getMusicNumber() {
		return list.size();
	}
	
	public Music getNextMusic(String order) {
		// 顺序播放 随机播放 单曲循环
		
		if (order == "sequence")  intCurrentMusicIndex = (intCurrentMusicIndex + 1) % getMusicNumber();
		else if (order == "random") {
			int tmp;
			do {
				tmp = randomGenerator.nextInt(getMusicNumber());
			} while (tmp == intCurrentMusicIndex);
			intCurrentMusicIndex = tmp;
		}
		else if (order == "single") intCurrentMusicIndex = intCurrentMusicIndex;
		return list.get(intCurrentMusicIndex);
	}
	
	
	public static void main(String[] args) {
		Music[] ml = new Music[5];
		MusicList musicList = new MusicList();
		for (int i = 1; i < ml.length; i++) {
			ml[i] = new Music(""+i);
		}
		ml[0] = new Music("1");
		
		for (int i = 0; i < ml.length; i++) {
			musicList.addMusic(ml[i]);
		}
		Music testMusic = new Music("E:\\Music\\1973.mp3");
		musicList.addMusic(testMusic);
		testMusic = new Music("3");
		musicList.addMusic(testMusic);
		
		for (int i = 0; i < 10; i++) {
			System.out.println(musicList.getNextMusic("random").getFilepath());
		}
	}	
}
