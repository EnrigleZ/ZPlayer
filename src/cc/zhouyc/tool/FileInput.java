package cc.zhouyc.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


import javafx.stage.DirectoryChooser;
/**
 * FileInput���ڴ��ļ����ļ���
 * 
 * @author ZhouYC
 */
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileInput {
	
	Stage stage;
	
	static File lastDir = new File(".");
	
	// �����������ֶ�����Ĭ�����ָ�ʽ
	static ArrayList<String> musicFormat = 
			new ArrayList<String>(Arrays.asList("wma","mp3","flac")); 
	
	
	public FileInput(Stage stage) {
		// ���� stage
		
		// Note: 
		// ��� showOpenDialogʹ�õ��ǲ������Ļ��� stage����ôֻ�ܴ�һ���ļ�ѡ�������ر��ļ�ѡ����֮ǰ���ܵ����������
		// ��������� new һ��stage��������ô���Դ򿪶����
		// ���˾��õ�һ�ֺã���ȻҪ���� stage�� controller...
		
		this.stage = stage;
		//this.stage = new Stage();
		
	}
	
	/**
	 * ѡȡ�����ļ�
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
	
	// ���� getMusicFromDir() �����ļ����е��ļ�
	public ArrayList<String> chooseDir() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File dir = directoryChooser.showDialog(stage);
		directoryChooser.setTitle("Ĭ�����mp3,wma��ʽ�����ļ�");
		directoryChooser.setInitialDirectory(lastDir);
		if (dir != null) {
			lastDir = dir;
			return getMusicFileFromDir(dir.getAbsolutePath());
		}
		return null;
	}
	
	// ���õݹ�ķ�ʽ�������ļ���
	private ArrayList<String> getMusicFileFromDir(String path) {
		ArrayList<String> ret = new ArrayList<String>();
		
		File dir = new File(path);
		if (!dir.isDirectory()) return null;
		
		String[] fileList = dir.list();
		for (String filename : fileList) {
			
			File file = new File(path + "\\"+  filename);
			if (file.isFile()) {
				String suffix = filename.substring(filename.lastIndexOf(".") + 1);
				
				// ����������Ĭ�ϸ�ʽ
				if (musicFormat.contains(suffix)) ret.add(path + "\\" + filename);
			}
			else {
				ret.addAll(getMusicFileFromDir(path + "\\"+  filename));
			}
		}
		
		return ret;
	}
}
