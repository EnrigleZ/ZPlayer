package cc.zhouyc.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;

import cc.zhouyc.tool.MusicImage;
import cc.zhouyc.view.Main;
import cc.zhouyc.view.MainController;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer {
	
	// Define player status by enum Status.
	static public enum Status {
		NOT_STARTED, PLAYING, PAUSED, FINISHED, FINISHED_AUTO
		};
	
	/*
	 * Member Variables
	 */
	private MusicList musicList;
	
	private Player player;
	
	private LongProperty longCurrentMiliTime = new SimpleLongProperty(0);
	
	// sync的线程信号量
	private Object playerLock = new Object();
	
	// 等待一首歌曲播放结束的信号量
	private Object musicEndLock = new Object();
	
	private String strPlayOrder = "";
	
	private Status isPlaying; // 应该初始化为 Status.NOT_STARTED;
	
	private Runnable runnableMusic;
	
	private Thread threadMusic;
	
	// 在这里显示音乐名
	
	private MainController controller;
	
	
	/*
	 * Member methods
	 */
	
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

	// 双击音乐Label
	// 之前的线程return，开启新的线程
	public boolean play() {
		Music music = musicList.getCurrentMusic();
		//threadMusic.
		if (music == null) {
			System.out.println("No Music!");
			return false;
		}
		
		playMusic();
		
		// weird....这个sync段如果没有写好可能要卡死...
//			synchronized (musicEndLock) {
//				while (getPlaying() != Status.FINISHED_AUTO) {
//					try {
//						System.out.println("play() wait for next music...");
//						//musicEndLock.wait();		// 原因在这：当时本函数在主线程调用 wait()阻塞
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			musicList.nextMusicIndex(getPlayOrder());
//			System.out.println("NextSong");
		return false;
	}
	
	
	// 暂停按钮
	public void pause() {
		synchronized (playerLock) {
			setPlaying(Status.PAUSED);
			System.out.println("Pause");
		}
	}
	
	// 继续播放
	
	// TODO：加个判断，是否当前不是暂停状态，用户点这个按键是希望播放选中歌曲
	//	可以在函数开始时判断一下
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
	
	// 下一曲按钮
	public void playNext() {
		musicList.nextMusicIndex(getPlayOrder());
		play();
	}
	
	// 上一曲按钮（均采用顺序倒序播放）
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
	
	// 通过Controller 来与界面交互
	public void setWidgets(MainController controller) {
		this.controller = controller;
	}
	
	// 开启线程，播放音乐
	private void playMusic() {

		// 判断是否有音乐还没结束，否则会出现严重混音错误
		synchronized (playerLock) {
			System.out.println("Before: getPl() == " + getPlaying());
			if (getPlaying() == Status.PLAYING || getPlaying() == Status.PAUSED)
				setPlaying(Status.FINISHED);
			else setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();	// 正在播放的线程会接受这个信息，然后将状态置为NOT_STARTED
			
			System.out.println(getPlaying());
			// 等待被中断（FINISHED）的歌曲停止（NOT_STARTED）
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
		} //synchronized(playerLock)
		// Now: isPlaying: NOT_STARTED
		
		longCurrentMiliTime.set(0);
		
		// 列表播放线程
		runnableMusic = new Runnable() {
			@Override
			public void run() {
				System.out.println("new run()");
				do {
					Music music = musicList.getCurrentMusic();
					try {
						FileInputStream musicInputStream = new FileInputStream(music.getFilepath());
						player = new Player(musicInputStream);
					} catch (Exception e) {
						System.err.println(e);
						return;
					}
					
					System.out.println("Current playing: ");
					if (music.getName() != null) System.out.println("Name: " + music.getName());
					else System.out.println("Name: null");
					if (music.getComposer() != null) System.out.println("Composer: " + music.getComposer());
					else System.out.println("Composer: null");
			        System.out.println("Desctription: " + music.getDescription());
			        System.out.println("Path: " + music.getFilepath());
			        System.out.println("Length: " + music.getMusicLength());
			        
					synchronized (playerLock) {
						setPlaying(Status.PLAYING);
					}
					
					playInternal();
					System.out.println("out next:" + getCurrentMusicIndex());
					if (getPlaying() == Status.FINISHED_AUTO) {
						System.out.println("FINISHED_AUTO  -- Preparing for next music...");
						// 用notifyAll()信号量的方式提醒 play()函数下一曲
//						
//						synchronized (musicEndLock) {
//							musicEndLock.notifyAll();
//						}
					}
					else return;	// 非正常停止（外部切歌）时return结束线程
				} while (true);
			}// run()
        };
        threadMusic = new Thread(runnableMusic);
        threadMusic.start();
        synchronized (playerLock) {
			setPlaying(Status.PLAYING);
		}
	}
	
	/**
	 * 本函数通过调用 Player类的play(1)方法播放音乐
	 * 首先调用checkMusicDetailDisplay()更新控件显示状态，并将播放时间置零
	 * 同时更新当前播放时间
	 * 通过getPlaying()检查当前播放状态是否被中断或暂停，
	 * 并通过线程同步对象 playerLock来同步线程
	 * 若因歌曲播放完毕自然结束，结束时isPlaying = FINISHED_AUTO
	 * 若因外部切歌结束，结束时isPlaying = FINISHED
	 * 
	 * @author ZhouYC
	 */
	public void playInternal() {

		checkMusicDetailDisplay();
		longCurrentMiliTime.set(0);
		
		while (getPlaying() != Status.FINISHED) {//
    		try {
    			long startTime=System.currentTimeMillis();
    			if (!player.play(1)) {
    				synchronized (playerLock) {
    					if (getPlaying() == Status.FINISHED) {
    						// 由于player.play()的延时，导致了不同步
    						// 所以在这里加上一个停止时的判断
    						break;
    					}
    					
    					// 这里是正常播放完成的
    					setPlaying(Status.FINISHED_AUTO);	
    					// System.out.println("自然完成" + getPlaying());
					}
    				//playNext();//不能调用这个playNext()否则是递归 = = 
    				System.out.println(getPlayOrder());
    				musicList.nextMusicIndex(getPlayOrder());
    				System.out.println(getCurrentMusicIndex());
    				return;
    			}
    			long deltaTime = System.currentTimeMillis() - startTime;
    			longCurrentMiliTime.set(longCurrentMiliTime.get() + deltaTime);
    			//System.out.println(longCurrentMiliTime.get());
    		} catch (final JavaLayerException e) {
    			System.err.println(e);
    			synchronized (playerLock) {
    				setPlaying(Status.FINISHED_AUTO);
				}
    			return;
    		}

    		// 暂停
    		synchronized (playerLock) {
    			// 当取消暂停或外部切歌中断时跳出等待
    			while (getPlaying() == Status.PAUSED) {
    				System.out.println("getPlaying() == Status.PAUSED");
    				try{
    					playerLock.wait();	//wait() 会暂时释放对playerLock的占有
    					
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

		System.out.println("Interrupted");
		synchronized (playerLock) {
			setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();
		}
		
		return;
	}
	
	/**
	 * 开始播放时调用本函数，更新控件显示的音乐信息
	 * 更新label显示的当前播放描述
	 * 更新tableview选中的音乐
	 * 
	 * @author ZhouYC
	 */
	private void checkMusicDetailDisplay() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	Music music = musicList.getCurrentMusic();
		    	controller.setMusicTime(music.getMusicLength());
		    	controller.getLabelDescription().setText(music.getDescription());
		    	controller.getTableMusic().getSelectionModel().select(getCurrentMusicIndex());
		    	int totalTime =music.getMusicLength();
		    	controller.getLabelTotal().setText(String.format("%02d:%02d", 
		    			totalTime / 60, 
		    			totalTime % 60
		    			));
		    	
		    	controller.getVBoxRight().setBackground(
		    			new MusicImage(music.getFilepath()).getBackground(
		    					controller.getVBoxRight().getWidth(), 
		    					controller.getVBoxRight().getHeight()
		    					));
		    	System.out.println(controller.getVBoxRight().styleProperty());//setStyle("-fx-background-size: 100%");
		    	
		    	System.out.println("Music information widgets updated.");
		    }
		});
		
	}
	
	public LongProperty getCurrentMiliTime() {
		return longCurrentMiliTime;
	}
}
