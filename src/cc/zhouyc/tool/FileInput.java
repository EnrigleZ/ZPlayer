package cc.zhouyc.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


import javafx.stage.DirectoryChooser;
/**
 * FileInput用于打开文件、文件夹，并选取音乐文件，或选取列表存档文件
 * public methods: 
 * 		chooseFile()
 * 		chooseDir()
 * 分别用于选取音乐文件和从文件夹（及子文件夹）中选取音乐文件
 * @author ZhouYC
 */
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileInput {
	
	Stage stage;
	
	/**
	 * STATIC
	 */
	// 默认打开文件选择器的路径，每次选择后置为本次打开的文件夹
	private static String lastDir = "E:\\Music";
	
	// 可以在这里手动设置默认音乐格式
	private static ArrayList<String> musicFormat = 
			new ArrayList<String>(Arrays.asList(
					"wma", 
					"mp3", 
					"wmv"
				)); 
	
	// 为了让这个类复用，加入手动指定路径及格式
	private String[] chosenFormat = null;
	private String chosenDir = null;
	// 用于判断是否是导出列表
	private int isImport = 0;	// 1, -1
	
	// constructor
	public FileInput(Stage stage) {
		// 传递 stage
		
		// Note: 
		// 如果 showOpenDialog使用的是播放器的基本 stage，那么只能打开一个文件选择器（关闭文件选择器之前不能点击播放器）
		// 而如果这里 new 一个stage出来，那么可以打开多个。
		// 个人觉得第一种好，虽然要传递 stage给 controller...
		
		this.stage = stage;
		//this.stage = new Stage();
		
		this.isImport = 0;
		
		try {
			new File(lastDir);
		} catch (Exception e) {
			lastDir = "";
			System.out.println("Default directory is invalid");
		}
	}
	public FileInput(Stage stage, String format[], String dir, int isImport) {
		this.stage = stage;
		this.isImport = isImport;
		chosenFormat = format;
		chosenDir = dir;
		try {
			new File(chosenDir);
		} catch (Exception e) {
			dir = "./data";
		}
	}
	
	/**
	 * 选取音乐文件或列表记录（需调用第二种构造方式），记录上次加入文件的路径lastDir，下次默认从此处打开
	 * @return File variable of selected music file.
	 */
	public File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("可根据扩展名筛选");
		if (isImport == 0) fileChooser.setInitialDirectory(new File(lastDir));
		else fileChooser.setInitialDirectory(new File(chosenDir));
		
		// 设置扩展名 (全部 + musicFormat)
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("All Files", "*.*")
		);
		if (chosenFormat == null ) {
			for (String type : musicFormat) {
				fileChooser.getExtensionFilters().add(
					new FileChooser.ExtensionFilter(type.toUpperCase(), "*."+type)
					);
			}
		}
		else {
			for (String format : chosenFormat) {
				fileChooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter(format.toUpperCase(), "*."+format)
						);
			}
		}
		File file;
		if (isImport == -1) file = fileChooser.showSaveDialog(stage);	//导出 
		else file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			if (this.isImport == 0) lastDir = file.getParent();
		}
		else System.out.println("No file selected.");
		
		return file;
	}
	
	/**
	 *	从选中文件夹中选取全部音乐格式文件（参考musicFormat数组）
	 *  调用 getMusicFromDir() 递归处理文件夹中的文件
	 * @return ArrayList<code>String</code> An ArrayList containing all the paths in the directory.
	 */
	public ArrayList<String> chooseDir() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File dir = directoryChooser.showDialog(stage);
//		directoryChooser.setTitle("默认添加mp3,wma格式音乐文件");
		directoryChooser.setInitialDirectory(new File("."));
		if (dir != null) {
			lastDir = dir.getPath();
			return getMusicFileFromDir(dir.getAbsolutePath());
		}
		return null;
	}
	
	// 采用递归的方式遍历子文件夹
	/**
	 * Use this function to search all the music files and return their paths.
	 * 
	 * @param String path: Path of the directory.
	 * @return An ArrayList/<Music/> containing all the music file paths in the directory and sub-directories.
	 * @author ZhouYC
	 */
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
