package cc.zhouyc.model;

import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MusicList {
	private ObservableList<Music> list;
	private int intCurrentMusicIndex = 0;
	
	private Random randomGenerator = new Random(System.currentTimeMillis());
	
//	private List<Integer> listHistory;
//	private int intHistoryIndex;
	
	// Constructor
	public MusicList() {
		list = FXCollections.observableArrayList();
		intCurrentMusicIndex = 0;
		// 研究一下List和ArrayList之间的多态关系，以及类型安全性
		
//		listHistory = new ArrayList<Integer>();
//		intHistoryIndex = 0;
	}

	public int getIndex(Music music) {
		return list.indexOf(music);
	}
	
	public boolean addMusic(Music music) {
		//System.out.println(music.getDescription());
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
	
	public void setCurrentMusicIndex(int index) {
		intCurrentMusicIndex = index;
	}
	
	public int getCurrentMusicIndex() {
		return intCurrentMusicIndex;
	}
	
	public Music getCurrentMusic() {
		if (getMusicNumber() == 0) return null;
		return list.get(intCurrentMusicIndex);
	}
	
	public int getMusicNumber() {
		return list.size();
	}
	
	// gextMusicIndex() 在返回的同时也修改 intCurrentMusicIndex
	public int nextMusicIndex(String order) {
		// 顺序播放 随机播放 单曲循环
		
		int intMusicNumber = getMusicNumber();
		if (intMusicNumber == 0) return -1;
		
		if (order == "sequence")  intCurrentMusicIndex = (intCurrentMusicIndex + 1) % intMusicNumber;
		else if (order == "random") {
			int tmp;
			do {
				tmp = randomGenerator.nextInt(intMusicNumber);
			} while (tmp == intCurrentMusicIndex);
			intCurrentMusicIndex = tmp;
		}
		else if (order == "single") intCurrentMusicIndex = intCurrentMusicIndex;
		
		// 更新历史记录
//		if (listHistory.isEmpty() || listHistory.get(intHistoryIndex-1) != intCurrentMusicIndex) {
//			listHistory.add(intCurrentMusicIndex);
//			intHistoryIndex++;
//		}
		return intCurrentMusicIndex;
	}
	
	// prevMusicIndex() 在返回的同时也修改 intCurrentMusicIndex
	public int prevMusicIndex() {
		if (getMusicNumber() == 0) 
		{
			System.out.println("上一曲err：列表为空");
			return -1;
		}
		
		intCurrentMusicIndex =  (intCurrentMusicIndex + getMusicNumber() - 1) % getMusicNumber();
		return intCurrentMusicIndex; 
	}
	
	public ObservableList<Music> getBindList() {
		return list;
	}
	
	public static void main(String[] args) {
		Music[] ml = new Music[5];
		MusicList musicList = new MusicList();
		for (int i = 1; i < ml.length; i++) {
			ml[i] = new Music(""+i);
		}
		ml[0] = new Music("1");
		
//		for (int i = 0; i < ml.length; i++) {
//			musicList.addMusic(ml[i]);
//		}
		Music testMusic = new Music("E:\\Music\\1973.mp3");
		musicList.addMusic(testMusic);
		testMusic = new Music("BAGEDA");
		musicList.addMusic(testMusic);
		testMusic = new Music("七里香");
		musicList.addMusic(testMusic);
		
		System.out.println("______");
		Music fordel = new Music("BAGEDA");
		musicList.removeMusic(fordel);
		
		for (int i = 0; i < musicList.getMusicNumber(); i++) {
			System.out.println(musicList.getCurrentMusic().getFilepath());
			musicList.nextMusicIndex("sequence");
		}
		
	}	
}
