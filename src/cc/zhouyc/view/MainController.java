package cc.zhouyc.view;

/**
 * MainController类实现MVC框架中的Controller
 * 实现界面与后端交互的沟通功能
 * 
 * @author ZhouYC
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler;

import cc.zhouyc.model.Music;
import cc.zhouyc.model.MusicPlayer;
import cc.zhouyc.model.MusicPlayer.Status;
import cc.zhouyc.tool.FileInput;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;

public class MainController implements Initializable{

	@FXML
	private Button buttonPlay, buttonPrev, buttonNext, buttonOrder, buttonInputFile, buttonInputDir, buttonRemove;
	@FXML
	private MenuItem action1, action2;
	
	@FXML
	private Slider sliderTime;
	@FXML
	private TableView<Music> tableMusic;
	@FXML
	private TableColumn<Music, String> columnName;
	@FXML
	private Label labelTime, labelTotal, labelDescription;
	@FXML
	private ProgressBar progressBarTime;
	@FXML
	private VBox vboxRight;
	
	private Stage stage;
	
	private MusicPlayer musicPlayer = new MusicPlayer();
	
	// 当前音乐总时长
	private int musicTime; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//progressBar.setProgress(0.5);
		sliderTime.setValue(0);
		sliderTime.setFocusTraversable(false);
		
		musicPlayer.setWidgets(this);
		
		// 采用JavaFX事件响应编程模型，为button设置点击效果
		initAllButtonAction();
		
		// 绑定 TableView中显示音乐详情列（只有一列）
		columnName.setCellValueFactory(new PropertyValueFactory<Music, String>("description"));

//		tableMusic.setOnMouseClicked(e->{
//			if (e.getClickCount() == 2) {
//				int index = tableMusic.getSelectionModel().getSelectedIndex();
//				System.out.println(index);
//			}
//		});
		//tableMusic.setOpacity(0.5);
		
		// 设置双击点击播放事件
		JavaFxObservable.eventsOf(tableMusic, MouseEvent.MOUSE_RELEASED).subscribe(s->{
			if (s.getClickCount() == 2) {
				int index = tableMusic.getSelectionModel().getSelectedIndex();
				if (index < 0) return;
				musicPlayer.setCurrentMusicIndex(index);
				musicPlayer.play();
				checkPlaying();
				System.out.println(index);
			}
		});
//		JavaFxObservable.eventsOf(tableMusic, MouseEvent.MOUSE_DRAGGED).subscribe(s->{
//			System.out.println(s.getPickResult());
//		});
//		JavaFxObservable.valuesOf(
//				tableMusic.getSelectionModel().selectedItemProperty()
//					).subscribe(v -> {
//					musicPlayer.
//				});
		//JavaFXObservable.valuesOf();
		
		// 在TableView中绑定列表
		tableMusic.setItems(musicPlayer.getBindList());
		
		// 实现音乐播放进度的绑定
		musicPlayer.getCurrentMiliTime().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				long currentTime = musicPlayer.getCurrentMiliTime().get();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						labelTime.setText(String.format("%02d:%02d", currentTime/60000, (currentTime%60000)/1000));
						double progress = new Double(currentTime / 1000)/musicTime;
						int colorPro = (int)((1 - progress) * 0x66)	;
						// 设置进度条颜色变化 #006666 -> 000066
						//System.out.println(progressBarTime.getStyleClass().indexOf("-fx-accent"));
						//progressBarTime.getStyleClass().set(1, "-fx-opacity: 0");
						String string = "-fx-accent: " + String.format("#EE%02x%02x", colorPro, 255 - colorPro);
						System.out.println(string);
						progressBarTime.getStyleClass().removeAll();
						progressBarTime.setStyle("-fx-accent: blue");
						System.out.println(progressBarTime.getStyleClass());
						//System.out.println(progress);
						//progressBarTime.set
						progressBarTime.setProgress(progress);
						sliderTime.setValue(progress * 100);
					}
				});
				
			}
		});
		System.out.println("Here I am in MainController "+Thread.currentThread().getName());


	}

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
		
		// 加入文件按钮 -> 文件选择窗口
		buttonInputFile.setOnAction(e -> {
			FileInput fileInput = new FileInput(stage);
			File file = fileInput.chooseFile();
			if (file != null) {
				Music music = new Music(file.getAbsolutePath());
				if (music.procDetail() != -1) {
					addMusicNode(music);
				}
			}
		});
		
		// 加入文件夹按钮-> 文件夹选择按钮
		buttonInputDir.setOnAction(e -> {
			FileInput fileInput = new FileInput(stage);
			ArrayList<String> dir = fileInput.chooseDir();
			if (dir == null) return;
			
			for (String path : dir) {
				Music music = new Music(path);
				if (music.procDetail() != -1) {
					musicPlayer.addMusic(music);
				}
			}
		});
		
		// 移除歌曲按钮 -> 移除歌曲 
		buttonRemove.setOnAction(e -> {
			if (musicPlayer.removeMusic(tableMusic.getSelectionModel().getSelectedItem())) 
				System.out.println("Remove sucessfully.");
			else System.out.println("Remove failed");
		});
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
