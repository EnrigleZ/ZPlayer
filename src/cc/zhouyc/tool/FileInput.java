package cc.zhouyc.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


import javafx.stage.DirectoryChooser;
/**
 * FileInput用于打开文件、文件夹
 * 
 * @author ZhouYC
 */
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileInput {
	
	Stage stage;
	
	static File lastDir = new File(".");
	
	// 可以在这里手动设置默认音乐格式
	static ArrayList<String> musicFormat = 
			new ArrayList<String>(Arrays.asList("wma","mp3","flac")); 
	
	
	public FileInput(Stage stage) {
		// 传递 stage
		
		// Note: 
		// 如果 showOpenDialog使用的是播放器的基本 stage，那么只能打开一个文件选择器（关闭文件选择器之前不能点击播放器）
		// 而如果这里 new 一个stage出来，那么可以打开多个。
		// 个人觉得第一种好，虽然要传递 stage给 controller...
		
		this.stage = stage;
		//this.stage = new Stage();
		
	}
	
	/**
	 * 选取音乐文件
	 * @return File variable
	 */
	public File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.setInitialDirectory(lastDir);
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) lastDir = file.getParentFile();
		else System.out.println("No file selected.");
		
		return file;
	}
	
	// 调用 getMusicFromDir() 处理文件夹中的文件
	public ArrayList<String> chooseDir() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File dir = directoryChooser.showDialog(stage);
		directoryChooser.setTitle("默认添加mp3,wma格式音乐文件");
		directoryChooser.setInitialDirectory(lastDir);
		if (dir != null) {
			lastDir = dir;
			return getMusicFileFromDir(dir.getAbsolutePath());
		}
		return null;
	}
	
	// 采用递归的方式遍历子文件夹
	private ArrayList<String> getMusicFileFromDir(String path) {
		ArrayList<String> ret = new ArrayList<String>();
		
		File dir = new File(path);
		if (!dir.isDirectory()) return null;
		
		String[] fileList = dir.list();
		for (String filename : fileList) {
			
			File file = new File(path + "\\"+  filename);
			if (file.isFile()) {
				String suffix = filename.substring(filename.lastIndexOf(".") + 1);
				
				// 在这里设置默认格式
				if (musicFormat.contains(suffix)) ret.add(path + "\\" + filename);
			}
			else {
				ret.addAll(getMusicFileFromDir(path + "\\"+  filename));
			}
		}
		
		return ret;
	}
}
