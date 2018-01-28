package cc.zhouyc.view;

/**
 * SubWindow类用于构造说明Music信息的弹窗
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
	    window.setTitle("歌曲信息 - " + music.getName());
	    window.setMinWidth(400);
	    window.setMinHeight(168);
	    
	    Label labelName = new Label("      标题：	" + music.getName());
	    Label labelAuthor = new Label("      歌手：	" + music.getComposer());
	    Label labelAlbum = new Label("      专辑：	" + music.getAlbum());
	    Label labelLength = new Label("      时长：	" + music.getStrLength());
	    Label labelPath = new Label("文件路径：	" + music.getFilepath());
	    VBox layout = new VBox(10);
	    layout.getChildren().addAll(labelName, labelAuthor, labelAlbum, labelLength, labelPath);
	    layout.setAlignment(Pos.BASELINE_LEFT);
	    Scene scene = new Scene(layout);
	    window.setScene(scene);
	    window.show();
	    
	    //使用showAndWait()的话先处理这个窗口，而如果不处理，main中的那个窗口不能响应
	    //window.showAndWait();
	}
}
