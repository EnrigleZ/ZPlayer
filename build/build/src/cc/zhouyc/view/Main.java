package cc.zhouyc.view;

import java.io.File;
import java.net.MalformedURLException;

import cc.zhouyc.controller.MainController;
/**
 *  Main.java 为ZPlayer的主界面显示代码。
 *  通过加载由 SceneBuilder 生成的 FXML文件进行构建。
 *  
 * @author ZhouYC
 * 
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;


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
			controller.setStage(primaryStage);
			
			Scene scene = new Scene(root);
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setWidth(676);
			primaryStage.setHeight(507);
			primaryStage.setResizable(false);
			primaryStage.setTitle("ZPlayer");
			primaryStage.getIcons().add(new Image(new File("./img/icon.png").toURI().toURL().toString()));
			
			//primaryStage.setOpacity(0.95);
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
