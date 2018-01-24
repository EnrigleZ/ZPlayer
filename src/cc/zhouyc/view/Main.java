package cc.zhouyc.view;

/**
 *  Main.java ΪZPlayer����������ʾ���롣
 *  ͨ�������� SceneBuilder ���ɵ� FXML�ļ����й�����
 *  
 * @author ZhouYC
 */
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			FXMLLoader loader = new FXMLLoader(
					Main.class.getResource("Main.fxml")
					);
			AnchorPane root = (AnchorPane)loader.load();
			MainController controller = loader.getController();
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
