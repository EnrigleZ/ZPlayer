package cc.zhouyc.view;

/**
 * MainController类实现MVC框架中的Controller
 * 实现界面与后端交互的沟通功能
 * 
 * @author ZhouYC
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.accessibility.internal.resources.accessibility;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler;

import cc.zhouyc.model.Music;
import cc.zhouyc.model.MusicPlayer;
import cc.zhouyc.model.MusicPlayer.Status;
import cc.zhouyc.tool.FileInput;
import cc.zhouyc.tool.SaveList;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;

public class MainController implements Initializable{

	@FXML
	private Button buttonPlay, buttonPrev, buttonNext, buttonOrder;//, buttonInputFile, buttonInputDir;
	@FXML
	private MenuItem buttonInputFile, buttonInputDir, buttonRemove, buttonClear, buttonExport, buttonImport;
	@FXML
	private Slider sliderTime;	// abandoned.
	@FXML
	private TableView<Music> tableMusic;
	@FXML
	private TableColumn<Music, String> columnName, columnLength;
	@FXML
	private Label labelTime, labelTotal, labelDescription;
	@FXML
	private ProgressBar progressBarTime;
	@FXML
	private VBox vboxRight;
	
	private Stage stage;
	
	// 右键点击的菜单
	private ContextMenu  contextRightMenu = new ContextMenu();
	private MenuItem 	rightProperties = new MenuItem("查看详情"), 
						rightRemove = new MenuItem("删除"), 
						rightSetNext = new MenuItem("设为下一首");
	
	private MusicPlayer musicPlayer = new MusicPlayer();
	
	// 当前音乐总时长
	private int musicTime; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		progressBarTime.setProgress(0);
		sliderTime.setValue(0);
		//sliderTime.setFocusTraversable(false);
		
		musicPlayer.setWidgets(this);
		
		// 采用JavaFX事件响应编程模型，为button设置点击效果
		initAllButtonAction();
		
		// 绑定 TableView中显示音乐详情列（只有一列）
		columnName.setCellValueFactory(new PropertyValueFactory<Music, String>("description"));
		columnLength.setCellValueFactory(new PropertyValueFactory<Music, String>("strLength"));
		
		// 在TableView中绑定列表
		tableMusic.setItems(musicPlayer.getBindList());
		
		// 实现音乐播放进度的绑定
		//	1. 播放进度条长度、时间标签数字更新
		//	2. 播放进度条颜色渐变特效
		musicPlayer.getCurrentMiliTime().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				long currentTime = musicPlayer.getCurrentMiliTime().get();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// 设置进度条显示，时间数字
						labelTime.setText(String.format("%02d:%02d", currentTime/60000, (currentTime%60000)/1000));
						double progress = new Double(currentTime / 1000)/musicTime;
						progressBarTime.setProgress(progress);
						sliderTime.setValue(progress * 100);
						
						// 通过setStyle()的方式改变进度条颜色样式
						// Note: setStyle()可以修改css中已定义的样式，removeAll()不会清除css的样式
						// 设置进度条颜色变化 #006666 -> #000066
						
						String strStyle = "#660000";	// init: red
						if (progress < 0.2) {			// #660000 -> #666600
							strStyle = String.format("#66%02X00", (int)(0x1FE*progress));
						} else if (progress < 0.4) {	// #666600 -> #006600
							strStyle = String.format("#%02X6600", (int)(0x1FE*(0.4-progress)));
						} else if (progress < 0.6) {	// #006600 -> #006666
							strStyle = String.format("#0066%02X", (int)(0x1FE*(progress-0.4)));
						} else if (progress < 0.8) {	// #006666 -> #000066
							strStyle = String.format("#00%02X66", (int)(0x1FE*(0.8-progress)));
						} else {						// #000066 -> #660066
							strStyle = String.format("#%02X0066", (int)(0x1FE*(progress-0.8)));
						}
						//System.out.println(strStyle);
						progressBarTime.setStyle(
								"-fx-accent: "+strStyle
								// 第二个特效...进度条随时间产生脉动效果
								+";-fx-opacity: "+(1-(double)(currentTime%1000)/2000));
						//System.out.println((1-(double)(currentTime%1000)/2000));
						//System.out.println(progressBarTime.getStyle());
					}
				});
				
			}
		});
		System.out.println("Here I am in MainController "+Thread.currentThread().getName());


	}	// initialize ends

	private void checkPlaying() {
		if (musicPlayer.getPlaying() == Status.PLAYING) buttonPlay.setText("暂停");
		else buttonPlay.setText("继续");
		tableMusic.getSelectionModel().select(musicPlayer.getCurrentMusicIndex());
	}
	
	// 设置按钮的点击事件
	public void initAllButtonAction() {
		
		// 重新查一下lambda表达式的写法
		// lambda表达式写起来比下面的普通写法简洁....
		
		// 下一曲按钮 -> 下一曲
		buttonNext.setOnAction(e -> {
			try {
				musicPlayer.playNext();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			checkPlaying();
			});
		
		// 上一曲按钮 -> 上一曲
		buttonPrev.setOnAction(e -> {
			try {
				musicPlayer.playPrev();
			} catch (FileNotFoundException | JavaLayerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			checkPlaying();
			});
		
		// 播放/暂停按钮
		buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if (musicPlayer.getPlaying() != Status.PLAYING) {
					// 未播放
					// 注意一下 FINISHED的状况
					
					musicPlayer.conti();
				}
				else {
					musicPlayer.pause();
				}
				checkPlaying();
			}
		});
		
		// 播放顺序按钮 -> 切换播放顺序
		buttonOrder.setOnAction(e -> {
			String currentOrder = musicPlayer.getPlayOrder();
			if (currentOrder.equals("sequence")) {
				musicPlayer.setPlayOrder("single");
				buttonOrder.setText("单曲循环");
			} else if (currentOrder.equals("single")) {
				musicPlayer.setPlayOrder("random");
				buttonOrder.setText("随机播放");
			} else if (currentOrder.equals("random")) {
				musicPlayer.setPlayOrder("sequence");
				buttonOrder.setText("顺序播放");
			}
			System.out.println(buttonOrder.getText() + ": " + musicPlayer.getPlayOrder());
		});
		
		// 右键菜单的两个选项 属性 & 删除
		rightProperties.setOnAction(e -> {
			Music music = tableMusic.getSelectionModel().getSelectedItem();
			if (music != null) {
				System.out.println(music.getDescription());
				showMusicInfo(music);
			}
		});
		rightRemove.setOnAction(e -> {
			if (musicPlayer.removeMusic(tableMusic.getSelectionModel().getSelectedItem())) 
				System.out.println("Remove sucessfully.");
			else System.out.println("Remove failed");
		});
		rightSetNext.setOnAction(e -> {
			Music music = tableMusic.getSelectionModel().getSelectedItem();
			if (musicPlayer.setNextManually(music)) {
				System.out.println("设为下一曲：" + music.getFilepath());
			}
			
		});
		contextRightMenu.getItems().addAll(rightProperties, rightRemove, rightSetNext);
		
		// subscribe方式 
		// 响应式设置音乐标签绑定事件
		JavaFxObservable.eventsOf(tableMusic, MouseEvent.MOUSE_RELEASED).subscribe(s->{
			if (musicPlayer.getMusicNumber() == 0) return;
			
			// 设置右键菜单呼出
			if (s.getButton() == MouseButton.SECONDARY) {
				if (tableMusic.getSelectionModel().getSelectedIndex() == -1) return;
				contextRightMenu.show(tableMusic, s.getScreenX(), s.getScreenY());
				return;
			}
			contextRightMenu.hide();
			// 设置双击点击播放事件
			if (s.getClickCount() == 2) {
				int index = tableMusic.getSelectionModel().getSelectedIndex();
				if (index < 0) return;
				musicPlayer.setCurrentMusicIndex(index);
				musicPlayer.play();
				checkPlaying();
				System.out.println(index);
			}
		});
		// 加入文件按钮 -> 文件选择窗口
		buttonInputFile.setOnAction(e -> {
			FileInput fileInput = new FileInput(stage);
			File file = fileInput.chooseFile();
			if (file != null) {
				String title = stage.getTitle();
				stage.setTitle(title);
				Music music = new Music(file.getAbsolutePath());
				if (music.procDetail() != -1) {
					addMusicNode(music);
				}
				else {
					new SubWindow().displayAlert("无法添加'" + file.getAbsolutePath() + "'");
				}
				stage.setTitle(title);
			}
		});
		
		 
		// 加入文件夹按钮-> 文件夹选择按钮
		buttonInputDir.setOnAction(e -> {
			FileInput fileInput = new FileInput(stage);
			ArrayList<String> dir = fileInput.chooseDir();
			if (dir == null) return;
			
			String title = stage.getTitle();
			stage.setTitle("正在处理歌曲信息...");

			// 开辟新线程加入歌曲
			// 可以不阻塞主进程
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					int insertNum = 0;
					ArrayList<String> unavailable = new ArrayList<String>();
					for (String path : dir) {
						Music music = new Music(path);
						if (music.procDetail() != -1) {
							if (musicPlayer.addMusic(music)) insertNum++;
						}
						else {
							//new SubWindow().displayAlert("无法添加'" + path + "'");
							unavailable.add(path);
						}
					}
					if (!unavailable.isEmpty()) {
						new SubWindow().displayAlert("无法添加'" + unavailable.get(0)+"'等"+unavailable.size()+"首歌曲");
					}
					new SubWindow().displayNotice("导入文件夹完毕，共新增"+insertNum+"首歌曲");
					Platform.runLater(new Runnable() {
						@Override
						public void run() { stage.setTitle(""); }
					});
				}
			});
			thread.start();
		});
		
		// 移除歌曲按钮 -> 移除歌曲 
		buttonRemove.setOnAction(e -> {
			if (musicPlayer.removeMusic(tableMusic.getSelectionModel().getSelectedItem())) 
				System.out.println("Remove sucessfully.");
			else System.out.println("Remove failed");
		});
		
		// 清空列表按钮 -> 清空列表
		buttonClear.setOnAction(e -> {
			if (musicPlayer.getMusicNumber() == 0) {
				System.out.println("no music to remove");
				return;
			}

			
		    if (new SubWindow().displayComfirm("清空列表吗")){
		    	musicPlayer.getBindList().clear();
		    } 
		    else System.out.println("not remove all");
		
		});
		
		// 从 .DB or .LIST 文件中导入列表
		buttonImport.setOnAction(e->{
			FileInput fileInput = new FileInput(stage, new String[] {"db", "list"}, "./data", 1) ;
			File file = fileInput.chooseFile();
			
			if (file == null) return;
			String title = stage.getTitle();
			try {
				SaveList saveList = null;
				saveList = new SaveList(file.getAbsolutePath());
				if (saveList.isTableMusicListExists() == false) {
					System.out.println("TABLE MUSICLIST 不存在");
					return;
				}
				boolean clear = true;
				if (musicPlayer.getMusicNumber() != 0) {
					clear = new SubWindow().displayComfirm("导入前是否保留当前列表？");
					if (clear == false) musicPlayer.getBindList().clear();
				}
				stage.setTitle("正在导入歌曲");
				ArrayList<Music> musics = saveList.load();
				// 除重
				musics.removeAll(musicPlayer.getBindList());
				int importNum = musics.size();
				// merge
				musicPlayer.getBindList().addAll(musics);
				stage.setTitle(title);
				new SubWindow().displayNotice("导入列表完毕，共新导入"+importNum+"首歌曲");
			} catch (Exception e1) {
				System.out.println(e1);
				stage.setTitle(title);
				new SubWindow().displayAlert("不支持该文件导入");
				return;
			}
		});
		
		// 将列表导出到.DB or .LIST文件
		buttonExport.setOnAction(e->{
			FileInput fileInput = new FileInput(stage, new String[] {"db", "list"}, "./data", -1) ;
			File file = fileInput.chooseFile();
			if (file == null) return;
			
			try {
				final String title = stage.getTitle();
				stage.setTitle("正在导出列表");
				SaveList saveList = new SaveList(file.getAbsolutePath());
				if (saveList.isTableMusicListExists()) {
					if (new SubWindow().displayComfirm("当前文件已记录歌曲信息，是否保留文件内列表？")
							== false) {
						saveList.deleteList();
					}
				}

				// 用多线程导出列表，要不然主界面会卡住
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							int exportNum = musicPlayer.getBindList().size();
							saveList.save(musicPlayer.getBindList());
							Platform.runLater(new Runnable() {
								@Override
								public void run() {stage.setTitle(title);}
							});
							new SubWindow().displayNotice("导出列表完毕，列表内" + exportNum + "首歌曲全部导出");
						} catch (SQLException e) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {stage.setTitle(title);}
							});
							System.out.println(e);
						}
					}
				});
				thread.start();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}	// initAllButton ends
	
	// 响应属性按钮，弹窗显示
	public void showMusicInfo(Music music) {
		new SubWindow().displayMusicInfo(music);
	}
	
	// 将一个 Music转换为界面显示中的一个点击控件，添加到ScrollPane中的VBox
	// 设置数据绑定，只需在musicList中添加对应的音乐即可
	public void addMusicNode(Music music) {
		if (musicPlayer.addMusic(music))
			System.out.println("添加成功");
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setOnCloseRequest(e->{System.exit(0);});
	}
	
	public Label getLabelDescription() {
		return labelDescription;
	}
	
	public Label getLabelTotal() {
		return labelTotal;
	}
	
	public TableView<Music> getTableMusic() {
		return tableMusic;
	}
	
	public VBox getVBoxRight() {
		return vboxRight;
	}

	public void setMusicTime(int musicLength) {
		this.musicTime = musicLength;
	}
}
