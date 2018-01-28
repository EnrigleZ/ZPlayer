package cc.zhouyc.view;

/**
 * MainController��ʵ��MVC����е�Controller
 * ʵ�ֽ������˽����Ĺ�ͨ����
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
	private Slider sliderTime;	// abandoned.
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
	
	// ��ǰ������ʱ��
	private int musicTime; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//progressBar.setProgress(0.5);
		sliderTime.setValue(0);
		sliderTime.setFocusTraversable(false);
		
		musicPlayer.setWidgets(this);
		
		// ����JavaFX�¼���Ӧ���ģ�ͣ�Ϊbutton���õ��Ч��
		initAllButtonAction();
		
		// �� TableView����ʾ���������У�ֻ��һ�У�
		columnName.setCellValueFactory(new PropertyValueFactory<Music, String>("description"));

//		tableMusic.setOnMouseClicked(e->{
//			if (e.getClickCount() == 2) {
//				int index = tableMusic.getSelectionModel().getSelectedIndex();
//				System.out.println(index);
//			}
//		});
		//tableMusic.setOpacity(0.5);
		
		// ����˫����������¼�
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
		
		// ��TableView�а��б�
		tableMusic.setItems(musicPlayer.getBindList());
		
		// ʵ�����ֲ��Ž��ȵİ�
		//	1. ���Ž��������ȡ�ʱ���ǩ���ָ���
		//	2. ���Ž�������ɫ������Ч
		musicPlayer.getCurrentMiliTime().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				long currentTime = musicPlayer.getCurrentMiliTime().get();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// ���ý�������ʾ��ʱ������
						labelTime.setText(String.format("%02d:%02d", currentTime/60000, (currentTime%60000)/1000));
						double progress = new Double(currentTime / 1000)/musicTime;
						progressBarTime.setProgress(progress);
						sliderTime.setValue(progress * 100);
						
						// ͨ��setStyle()�ķ�ʽ�ı��������ɫ��ʽ
						// Note: setStyle()�����޸�css����ʽ��removeAll()�������css����ʽ
						// ���ý�������ɫ�仯 #006666 -> 000066
						
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
						progressBarTime.setStyle("-fx-accent: "+strStyle);
						//System.out.println(progressBarTime.getStyle());
					}
				});
				
			}
		});
		System.out.println("Here I am in MainController "+Thread.currentThread().getName());


	}

	private void checkPlaying() {
		if (musicPlayer.getPlaying() == Status.PLAYING) buttonPlay.setText("��ͣ");
		else buttonPlay.setText("����");
		tableMusic.getSelectionModel().select(musicPlayer.getCurrentMusicIndex());
	}
	
	// ���ð�ť�ĵ���¼�
	public void initAllButtonAction() {
		
		// ���²�һ��lambda���ʽ��д��
		// lambda���ʽд�������������ͨд�����....
		
		// ��һ����ť -> ��һ��
		buttonNext.setOnAction(e -> {
			try {
				musicPlayer.playNext();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			checkPlaying();
			});
		
		// ��һ����ť -> ��һ��
		buttonPrev.setOnAction(e -> {
			try {
				musicPlayer.playPrev();
			} catch (FileNotFoundException | JavaLayerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			checkPlaying();
			});
		
		// ����/��ͣ��ť
		buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if (musicPlayer.getPlaying() != Status.PLAYING) {
					// δ����
					// ע��һ�� FINISHED��״��
					
					musicPlayer.conti();
				}
				else {
					musicPlayer.pause();
				}
				checkPlaying();
			}
		});
		
		// ����˳��ť -> �л�����˳��
		buttonOrder.setOnAction(e -> {
			String currentOrder = musicPlayer.getPlayOrder();
			if (currentOrder.equals("sequence")) {
				musicPlayer.setPlayOrder("single");
				buttonOrder.setText("����ѭ��");
			} else if (currentOrder.equals("single")) {
				musicPlayer.setPlayOrder("random");
				buttonOrder.setText("�������");
			} else if (currentOrder.equals("random")) {
				musicPlayer.setPlayOrder("sequence");
				buttonOrder.setText("˳�򲥷�");
			}
			System.out.println(buttonOrder.getText() + ": " + musicPlayer.getPlayOrder());
		});
		
		// �����ļ���ť -> �ļ�ѡ�񴰿�
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
		

		// �����ļ��а�ť-> �ļ���ѡ��ť
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
		
		// �Ƴ�������ť -> �Ƴ����� 
		buttonRemove.setOnAction(e -> {
			if (musicPlayer.removeMusic(tableMusic.getSelectionModel().getSelectedItem())) 
				System.out.println("Remove sucessfully.");
			else System.out.println("Remove failed");
		});
	}
	
	// ��һ�� Musicת��Ϊ������ʾ�е�һ������ؼ�����ӵ�ScrollPane�е�VBox
	// �������ݰ󶨣�ֻ����musicList����Ӷ�Ӧ�����ּ���
	public void addMusicNode(Music music) {
		if (musicPlayer.addMusic(music))
			System.out.println("��ӳɹ�");
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
