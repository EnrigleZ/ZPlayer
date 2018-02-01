package cc.zhouyc.view;

import java.awt.TextField;
import java.util.Optional;

/**
 * SubWindow类用于构造各种弹窗
 * @author ZhouYC
 */
import cc.zhouyc.model.Music;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SubWindow {
	
	// 显示音乐属性
	public void displayMusicInfo(Music music){
	    Stage window = new Stage();
	    window.setTitle("歌曲信息 - " + music.getName());
	    window.setMinWidth(400);
	    window.setMinHeight(168);
	    window.setResizable(false);
	    Label labelName = new Label("      标题：	" + music.getName());
	    Label labelAuthor = new Label("      歌手：	" + music.getComposer());
	    Label labelAlbum = new Label("      专辑：	" + music.getAlbum());
	    Label labelLength = new Label("      时长：	" + music.getStrLength());
	    Label labelPath = new Label("文件路径：	" + music.getFilepath());
	    labelPath.setPrefSize(400, 30);
	    labelPath.autosize();
	    VBox layout = new VBox(10);
	    layout.getChildren().addAll(labelName, labelAuthor, labelAlbum, labelLength, labelPath);
	    layout.setAlignment(Pos.BASELINE_LEFT);
	    Scene scene = new Scene(layout);
	    window.setScene(scene);
	    window.show();
	    
	    //使用showAndWait()的话先处理这个窗口，而如果不处理，main中的那个窗口不能响应
	    //window.showAndWait();
	}

	// 显示错误提示
	public void displayAlert(String warning) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert error = new Alert(Alert.AlertType.ERROR, warning);
				error.show();
			}
		});
	}
	
	// 显示确认窗口
	public boolean displayComfirm(String confirm) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, confirm);
	    Optional<ButtonType> result = confirmation.showAndWait();
	    return result.isPresent() && result.get() == ButtonType.OK;
	}
	
	public void displayNotice(String notice) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION, notice);
				alert.show();
			}
		});
	}
}
