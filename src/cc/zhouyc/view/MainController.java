package cc.zhouyc.view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import cc.zhouyc.model.Music;
import cc.zhouyc.model.MusicPlayer;
import cc.zhouyc.tool.FileInput;

import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainController implements Initializable{

	@FXML
	private Button buttonPlay, buttonPrev, buttonNext, buttonOrder, buttonInputFile, buttonInputDir, buttonRemove;
	@FXML
	private Slider sliderTime;
	@FXML
	private TableView<Music> tableMusic;
	@FXML
	private TableColumn<Music, String> columnName, columnPath;
	@FXML
	private Label labelTime, labelDescription;
	private Stage stage;
	
	private MusicPlayer musicPlayer = new MusicPlayer();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// ����JavaFX�¼���Ӧ���ģ�ͣ�Ϊbutton���õ��Ч��
		initAllButtonAction();
		sliderTime.setValue(0);

		musicPlayer.setLabelDescription(labelDescription);
		//columnID.setCellValueFactory(new PropertyValueFactory<Music, String>("id"));
		columnName.setCellValueFactory(new PropertyValueFactory<Music, String>("description"));
		columnPath.setCellValueFactory(new PropertyValueFactory<Music, String>("filepath"));

//		tableMusic.setOnMouseClicked(e->{
//			if (e.getClickCount() == 2) {
//				int index = tableMusic.getSelectionModel().getSelectedIndex();
//				System.out.println(index);
//			}
//		});
		
		JavaFxObservable.eventsOf(tableMusic, MouseEvent.MOUSE_RELEASED).subscribe(s->{
			if (s.getClickCount() == 2) {
				int index = tableMusic.getSelectionModel().getSelectedIndex();
				if (index < 0) return;
				musicPlayer.setCurrentMusicIndex(index);
				musicPlayer.play();
				if (musicPlayer.getPlaying()) buttonPlay.setText("��ͣ");
				else buttonPlay.setText("����");
				System.out.println(index);
			}
		});
		JavaFxObservable.eventsOf(tableMusic, MouseEvent.MOUSE_DRAGGED).subscribe(s->{
			System.out.println(s.getPickResult());
		});
//		JavaFxObservable.valuesOf(
//				tableMusic.getSelectionModel().selectedItemProperty()
//					).subscribe(v -> {
//					musicPlayer.
//				});
		//JavaFXObservable.valuesOf();
		tableMusic.setItems(musicPlayer.getBindList());
		
//		for (int i = 0; i < 50; i++) {
//            Button button = new Button();
//            vboxMusicList.getChildren().add(button);
//        }
		
//		scrollMusicList.setContent(vboxMusicList);
		System.out.println("Here I am in MainController "+Thread.currentThread().getName());


	}

	private void checkPlaying() {
		if (musicPlayer.getPlaying()) buttonPlay.setText("��ͣ");
		else buttonPlay.setText("����");
		tableMusic.getSelectionModel().select(musicPlayer.getCurrentMusicIndex());
	}
	
	// ���ð�ť�ĵ���¼�
	public void initAllButtonAction() {
		
		// ���²�һ��lambda���ʽ��д��
		// lambda���ʽд�������������ͨд�����....
		buttonNext.setOnAction(e -> {
			musicPlayer.playNext();
			checkPlaying();
			});
		
		buttonPrev.setOnAction(e -> {
			musicPlayer.playPrev();
			checkPlaying();
			});
		
		buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if (musicPlayer.getPlaying() == false) {
					// δ����
					if (musicPlayer.play() == true) {
						musicPlayer.setPlaying(true);
					}
				}
				else {
					musicPlayer.pause();
					musicPlayer.setPlaying(false);
				}
				checkPlaying();
			}
		});
		
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
			
//			Music test = new Music(musicPlayer.getMusicNumber().toString());
//			test.setName("name");
//			addMusicNode(test);
		});
		
		buttonInputFile.setOnAction(e -> {
			FileInput fileInput = new FileInput(stage);
			File file = fileInput.chooseFile();
			if (file != null) addMusicNode(new Music(file.getAbsolutePath()));
		});
		
		buttonInputDir.setOnAction(e -> {
			FileInput fileInput = new FileInput(stage);
			ArrayList<String> dir = fileInput.chooseDir();
			if (dir == null) return;
			
			for (String path : dir) {
				musicPlayer.addMusic(new Music(path));
			}
		});
		buttonRemove.setOnAction(e -> {
			if (musicPlayer.removeMusic(tableMusic.getSelectionModel().getSelectedItem())) 
				System.out.println("Remove sucessfully.");
			else System.out.println("Remove failed");
		});
	}
	
	// ��һ�� Musicת��Ϊ������ʾ�е�һ������ؼ�����ӵ�ScrollPane�е�VBox
	public void addMusicNode(Music music) {
//		Button buttonMusic = new Button(music.getName());
//		buttonMusic.setPrefSize(208, 60);
//		buttonMusic.setOnAction(e->{buttonMusic.setText("click");});
//		vboxMusicList.getChildren().add(buttonMusic);
		
		if (musicPlayer.addMusic(music))
			System.out.println("��ӳɹ�");
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
