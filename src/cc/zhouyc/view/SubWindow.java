package cc.zhouyc.view;

/**
 * SubWindow�����ڹ���˵��Music��Ϣ�ĵ���
 * @author ZhouYC
 */
import cc.zhouyc.model.Music;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SubWindow {
	public void display(Music music){
	    Stage window = new Stage();
	    window.setTitle("������Ϣ - " + music.getName());
	    window.setMinWidth(400);
	    window.setMinHeight(168);
	    
	    Label labelName = new Label("      ���⣺	" + music.getName());
	    Label labelAuthor = new Label("      ���֣�	" + music.getComposer());
	    Label labelAlbum = new Label("      ר����	" + music.getAlbum());
	    Label labelLength = new Label("      ʱ����	" + music.getStrLength());
	    Label labelPath = new Label("�ļ�·����	" + music.getFilepath());
	    VBox layout = new VBox(10);
	    layout.getChildren().addAll(labelName, labelAuthor, labelAlbum, labelLength, labelPath);
	    layout.setAlignment(Pos.BASELINE_LEFT);
	    Scene scene = new Scene(layout);
	    window.setScene(scene);
	    window.show();
	    
	    //ʹ��showAndWait()�Ļ��ȴ���������ڣ������������main�е��Ǹ����ڲ�����Ӧ
	    //window.showAndWait();
	}
}
