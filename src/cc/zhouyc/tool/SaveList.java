package cc.zhouyc.tool;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import cc.zhouyc.model.Music;
import cc.zhouyc.model.MusicList;
import javafx.collections.ObservableList;

/**
 *	SaveList
 *	使用SQLite 保存 musicList中的文件路径
 *	提供导入import、导出export
 * 
 *	Define [TABLE MUSICLIST] as:
 * 		PATH VARCHAR(255) UNIQUE NOT NULL PRIMARY KEY
 * 
 * 	EXPORT:
 * 		1. Verify if selected database file contains such a TABLE MUSICLIST.
 * 		2. If exists, DROP it.
 * 		3. CREATE TABLE MUSICLIST.
 * 		4. For each [music] in [musicList], INSERT INTO MUSICLIST VALUES [music.getFilePath].
 * 		5. Close.
 * 
 *	IMPORT:
 *		1. Verify if selected database file contains such a TABLE MUSICLIST.
 *		2. If not, throws an error,  
 * @author ZhouYC
 *
 */
public class SaveList {
	private String databasePath = "";
	Connection connection = null;
	
	public SaveList(String databasePath) throws SQLException {
		this.databasePath = databasePath;
		connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
		System.out.println("Create conn with " + "jdbc:sqlite:" + databasePath);
	}
	
	/**
	 *	判断 TABLE MUSICLIST 是否存在
	 *	@return exists
	 *	@author ZhouYC
	 */
	public boolean isTableMusicListExists() {
		String sql = "select count(*) as cnt from sqlite_master where type='table' and name='MUSICLIST'";

	    Statement statement = null;
	    try {
	    	System.out.println("jdbc:sqlite:" + databasePath);
	    	
	    	statement = connection.createStatement();
	    	ResultSet resultSet = statement.executeQuery(sql);

	    	resultSet.next();
	    	
	    	if (resultSet.getInt("cnt") == 0) {
	    		statement.close();
	    		return false;
	    	}
	    	else {
	    		statement.close();
	    		return true;
	    	}
  
	    } catch(Exception e) {return false;}
	}

	public boolean createTable() throws SQLException {
		String sqlCreateTable = "CREATE TABLE MUSICLIST"
				+ "(PATH VARCHAR UNIQUE NOT NULL PRIMARY KEY)";
		Statement statement = connection.createStatement();
		int resultSet = statement.executeUpdate(sqlCreateTable);
		System.out.println(resultSet);
		return isTableMusicListExists();
	}
	
//	
//	public void save(String music) throws SQLException{
//		String sqlInsert = "INSERT INTO MUSICLIST VALUES('" + music + "');";
//		System.out.println(sqlInsert);
//		Statement statement = connection.createStatement();
//		statement.executeUpdate(sqlInsert);
//	}

	public void save(ObservableList<Music> musicList) throws SQLException {
		Statement statement = connection.createStatement();
		if (!isTableMusicListExists()) createTable();
		
		for (Music music : musicList) {
			try {
				String sqlInsert = "INSERT INTO MUSICLIST VALUES('" + music.getFilepath().replaceAll("[']", "''") + "');";
				System.out.println(sqlInsert);
				statement.executeUpdate(sqlInsert);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		statement.close();
	}
	
	public ArrayList<Music> load() throws SQLException {
		Statement statement = connection.createStatement();
		if (isTableMusicListExists() == false) return null;
		
		String sqlSelect = "SELECT PATH FROM MUSICLIST";
		ResultSet resultSet = statement.executeQuery(sqlSelect);
		ArrayList<Music> ret = new ArrayList<Music>();
		while (resultSet.next()) {
			Music music = new Music(resultSet.getString("PATH"));
			if (music.procDetail() != -1) {
				ret.add(music);
			}
		}
		return ret;
	}
	
	public void deleteList() {
		if (isTableMusicListExists() == false) return;
		try {
		Statement statement = connection.createStatement();
		statement.executeUpdate("DELETE FROM MUSICLIST;");
		statement.close();
		} catch(Exception e) {}
	}
	
	public static void main(String[] args) {
		SaveList saveList;
		try {
			saveList = new SaveList("./data/test.db");
			if (!saveList.isTableMusicListExists()) {
				saveList.createTable();
			}
			//saveList.save("as");
			
		} catch (SQLException e) {
			System.out.println("failed");
			e.printStackTrace();
		}
	}
}
