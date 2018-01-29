package cc.zhouyc.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


import javafx.stage.DirectoryChooser;
/**
 * FileInput���ڴ��ļ����ļ��У���ѡȡ�����ļ�����ѡȡ�б�浵�ļ�
 * public methods: 
 * 		chooseFile()
 * 		chooseDir()
 * �ֱ�����ѡȡ�����ļ��ʹ��ļ��У������ļ��У���ѡȡ�����ļ�
 * @author ZhouYC
 */
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileInput {
	
	Stage stage;
	
	/**
	 * STATIC
	 */
	// Ĭ�ϴ��ļ�ѡ������·����ÿ��ѡ�����Ϊ���δ򿪵��ļ���
	private static String lastDir = "E:\\Music";
	
	// �����������ֶ�����Ĭ�����ָ�ʽ
	private static ArrayList<String> musicFormat = 
			new ArrayList<String>(Arrays.asList(
					"wma", 
					"mp3", 
					"wmv"
				)); 
	
	// Ϊ��������ิ�ã������ֶ�ָ��·������ʽ
	private String[] chosenFormat = null;
	private String chosenDir = null;
	// �����ж��Ƿ��ǵ����б�
	private int isImport = 0;	// 1, -1
	
	// constructor
	public FileInput(Stage stage) {
		// ���� stage
		
		// Note: 
		// ��� showOpenDialogʹ�õ��ǲ������Ļ��� stage����ôֻ�ܴ�һ���ļ�ѡ�������ر��ļ�ѡ����֮ǰ���ܵ����������
		// ��������� new һ��stage��������ô���Դ򿪶����
		// ���˾��õ�һ�ֺã���ȻҪ���� stage�� controller...
		
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
	 * ѡȡ�����ļ����б��¼������õڶ��ֹ��췽ʽ������¼�ϴμ����ļ���·��lastDir���´�Ĭ�ϴӴ˴���
	 * @return File variable of selected music file.
	 */
	public File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("�ɸ�����չ��ɸѡ");
		if (isImport == 0) fileChooser.setInitialDirectory(new File(lastDir));
		else fileChooser.setInitialDirectory(new File(chosenDir));
		
		// ������չ�� (ȫ�� + musicFormat)
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
		if (isImport == -1) file = fileChooser.showSaveDialog(stage);	//���� 
		else file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			if (this.isImport == 0) lastDir = file.getParent();
		}
		else System.out.println("No file selected.");
		
		return file;
	}
	
	/**
	 *	��ѡ���ļ�����ѡȡȫ�����ָ�ʽ�ļ����ο�musicFormat���飩
	 *  ���� getMusicFromDir() �ݹ鴦���ļ����е��ļ�
	 * @return ArrayList<code>String</code> An ArrayList containing all the paths in the directory.
	 */
	public ArrayList<String> chooseDir() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File dir = directoryChooser.showDialog(stage);
//		directoryChooser.setTitle("Ĭ�����mp3,wma��ʽ�����ļ�");
		directoryChooser.setInitialDirectory(new File("."));
		if (dir != null) {
			lastDir = dir.getPath();
			return getMusicFileFromDir(dir.getAbsolutePath());
		}
		return null;
	}
	
	// ���õݹ�ķ�ʽ�������ļ���
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
