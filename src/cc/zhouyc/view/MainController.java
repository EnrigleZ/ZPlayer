package cc.zhouyc.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class MainController implements Initializable{

	@FXML
	private Button buttonPlay;
	@FXML
	private Slider sliderTime;
	@FXML
	private VBox vBoxMusicList;
	
	private String playOrder = "顺序播放";
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 采用JavaFX事件响应编程模型，为button设置点击效果
		initAllButtonAction();
		sliderTime.setValue(0);
		System.out.println("Here I am in MainController "+Thread.currentThread().getName());
		
	}

	public void initAllButtonAction() {
		
		buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.println("音乐开始播放！");
				
			}
		});
		
		
	}
}
