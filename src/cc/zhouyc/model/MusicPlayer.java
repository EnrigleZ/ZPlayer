package cc.zhouyc.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;

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
	
	// sync���߳��ź���
	private Object playerLock = new Object();
	
	// �ȴ�һ�׸������Ž������ź���
	private Object musicEndLock = new Object();
	
	private String strPlayOrder = "";
	
	private Status isPlaying; // Ӧ�ó�ʼ��Ϊ Status.NOT_STARTED;
	
	private Runnable runnableMusic;
	
	private Thread threadMusic;
	
	// ��������ʾ������
	
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

	// ˫������Label
	// ֮ǰ���߳�return�������µ��߳�
	public boolean play() {
		Music music = musicList.getCurrentMusic();
		//threadMusic.
		if (music == null) {
			System.out.println("No Music!");
			return false;
		}
		
		playMusic();
		
		// weird....���sync�����û��д�ÿ���Ҫ����...
//			synchronized (musicEndLock) {
//				while (getPlaying() != Status.FINISHED_AUTO) {
//					try {
//						System.out.println("play() wait for next music...");
//						//musicEndLock.wait();		// ԭ�����⣺��ʱ�����������̵߳��� wait()����
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			musicList.nextMusicIndex(getPlayOrder());
//			System.out.println("NextSong");
		return false;
	}
	
	
	// ��ͣ��ť
	public void pause() {
		synchronized (playerLock) {
			setPlaying(Status.PAUSED);
			System.out.println("Pause");
		}
	}
	
	// ��������
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
	
	// ��һ����ť
	public void playNext() {
		musicList.nextMusicIndex(getPlayOrder());
		play();
	}
	
	// ��һ����ť��������˳���򲥷ţ�
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
	// �������ݰ�
	public ObservableList<Music> getBindList() {
		return musicList.getBindList();
	}
	
	// ͨ��Controller ������潻��
	public void setWidgets(MainController controller) {
		this.controller = controller;
	}
	
	// �����̣߳���������
	private void playMusic() {
		
		synchronized (playerLock) {
			
			// �ж��Ƿ������ֻ�û����
			System.out.println("Before: getPl() == " + getPlaying());
			if (getPlaying() == Status.PLAYING || getPlaying() == Status.PAUSED)
				setPlaying(Status.FINISHED);
			else setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();	// ���ڲ��ŵ��̻߳���������Ϣ��Ȼ��״̬��ΪNOT_STARTED
			
			System.out.println(getPlaying());

			// �ȴ����жϣ�FINISHED���ĸ���ֹͣ��NOT_STARTED��
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
		}
		// Now: isPlaying: NOT_STARTED
		
		longCurrentMiliTime.set(0);
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
					
			        System.out.println(music.getDescription());
			        
					synchronized (playerLock) {
						setPlaying(Status.PLAYING);
					}
					
					playInternal();
					System.out.println("out next:" + getCurrentMusicIndex());
					if (getPlaying() == Status.FINISHED_AUTO) {
						System.out.println("FINISHED_AUTO  -- Preparing for next music...");
						// ��notifyAll()�ź����ķ�ʽ���� play()������һ��
//						
//						synchronized (musicEndLock) {
//							musicEndLock.notifyAll();
//						}
					}
					else return;
				} while (true);
			}
        };
        threadMusic = new Thread(runnableMusic);
        threadMusic.start();
        synchronized (playerLock) {
			setPlaying(Status.PLAYING);
		}
	}
	
	public void playInternal() {

		checkMusicDetailDisplay();
		longCurrentMiliTime.set(0);
		
		while (getPlaying() != Status.FINISHED) {//
    		try {
    			long startTime=System.currentTimeMillis();
    			if (!player.play(1)) {
    				synchronized (playerLock) {
    					if (getPlaying() == Status.FINISHED) {
    						// ����player.play()����ʱ�������˲�ͬ��
    						// �������������һ��ֹͣʱ���ж�
    						break;
    					}
    					
    					// ����������������ɵ�
    					setPlaying(Status.FINISHED_AUTO);	
    					// System.out.println("��Ȼ���" + getPlaying());
					}
    				//playNext();//���ܵ������playNext()�����ǵݹ� = = 
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
		// ����Ϊ�ⲿ�ֶ����裬�����Զ��������ѭ��ʱ���ᵽ���²��ִ���
		// �е���һ�׸�ʱ��ͨ���ı�FINISHEDΪNOT_STARTED
		// ��ʾ���̲߳����Ѿ�����
		// ���򲥷Ż������
		// ����Ҫ����set + notifyAll�ķ�ʽ��play()�е�wait()�����
		// ����ȷ�����ֵ�ȷ���ˣ�������ֻ���0.1s fractionƬ��û����������

		System.out.println("Interrupted");
		synchronized (playerLock) {
			setPlaying(Status.NOT_STARTED);
			playerLock.notifyAll();
		}
		
		return;
	}
	
	private void checkMusicDetailDisplay() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	controller.getLabelDescription().setText(musicList.getCurrentMusic().getDescription());
		    	controller.getTableMusic().getSelectionModel().select(getCurrentMusicIndex());
		    }
		});
		
	}
	
	public LongProperty getCurrentMiliTime() {
		return longCurrentMiliTime;
	}
}
