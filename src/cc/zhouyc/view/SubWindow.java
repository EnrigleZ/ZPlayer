package cc.zhouyc.view;

import java.awt.TextField;
import java.util.Optional;

/**
 * SubWindow�����ڹ�����ֵ���
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
	
	// ��ʾ��������
	public void displayMusicInfo(Music music){
	    Stage window = new Stage();
	    window.setTitle("������Ϣ - " + music.getName());
	    window.setMinWidth(400);
	    window.setMinHeight(168);
	    window.setResizable(false);
	    Label labelName = new Label("      ���⣺	" + music.getName());
	    Label labelAuthor = new Label("      ���֣�	" + music.getComposer());
	    Label labelAlbum = new Label("      ר����	" + music.getAlbum());
	    Label labelLength = new Label("      ʱ����	" + music.getStrLength());
	    Label labelPath = new Label("�ļ�·����	" + music.getFilepath());
	    labelPath.setPrefSize(400, 30);
	    labelPath.autosize();
	    VBox layout = new VBox(10);
	    layout.getChildren().addAll(labelName, labelAuthor, labelAlbum, labelLength, labelPath);
	    layout.setAlignment(Pos.BASELINE_LEFT);
	    Scene scene = new Scene(layout);
	    window.setScene(scene);
	    window.show();
	    
	    //ʹ��showAndWait()�Ļ��ȴ���������ڣ������������main�е��Ǹ����ڲ�����Ӧ
	    //window.showAndWait();
	}

	// ��ʾ������ʾ
	public void displayAlert(String warning) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert error = new Alert(Alert.AlertType.ERROR, warning);
				error.show();
			}
		});
	}
	
	// ��ʾȷ�ϴ���
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
