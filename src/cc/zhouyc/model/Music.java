package cc.zhouyc.model;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v1Tag;

/**
 * Music
 * 
 * ��������model���������ֵ���Ϣ
 * ������
 * 		filepath		�ļ�·��
 * 		name			������
 * 		composer		������
 * 		musicLength		ʱ��
 * 		album			����ר��
 * 	��getter & setter
 * 
 * @author ZhouYC
 *
 */
public class Music {
	
	// Seem to be abandoned...
	private Integer id;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	// File path in file system.
	private String filepath = null;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	// Music title
	private String name = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ר����
	private String album = null;
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}
	
	// ������
	private String composer = null;
	
	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	private int musicLength = 0xffffffff;
	
	public int getMusicLength() {
		return musicLength;
	}
	public String getStrLength() {
		if (musicLength == 0xffffffff) return "-";
		else return String.format("%02d:%02d", musicLength/60, musicLength%60);
	}
	public void setMusicLength(int length) {
		this.musicLength = length;
	}
	
	public String getDescription() {
		if (name == null || name.isEmpty()) return getFileName();
		if (composer == null || composer.isEmpty()) return name;
		return name + " - " + composer;
	}
	
	/**
	 * ��·���л�ȡ�����ļ�����
	 * @return file name without suffix
	 * @author ZhouYC
	 */
	public String getFileName() {
		int start1 = filepath.lastIndexOf("\\"), start2 = filepath.lastIndexOf("/"), start = -1;
		if (start1 > start2) start = start1;
		else start = start2;
		if (start == -1) start = -1;
        int end = filepath.lastIndexOf(".");  
        
        if (end == -1) return filepath.substring(start + 1);
        if (end <= start) return filepath;
        else return filepath.substring(start + 1, end);  
	}
	/**
	 * equals() & hashCode()
	 * Override methods.
	 */
	@Override
	public boolean equals(Object arg) {
	// ѧϰһ�������дequals....�������дMusic�Ǵ��...
		
		if (arg != null && arg instanceof Music) {
			return filepath.equals(((Music)arg).filepath);
			// Using equals() instead of '==' .....
		}
		else return false;
	}
	
	@Override
	public int hashCode() {
		return filepath.hashCode();
	}
	
	public Music(String path) {
		filepath = path;
	}
	
	/**
	 *	procDetail()
	 *	���� name, composer, album, time������
	 *	�ڼ���ʱִ��procDetail����ʼ����������ʱ�����Ϣ
	 *	Initialize music title, during-time, etc.
	 *	@return int status(-1, 0, 1)
	 *	@author ZhouYC
	 */
	public int procDetail() {
		MP3File file = null;
		try {
			file = new MP3File(getFilepath());
		} catch (Exception e) {
			// �޷�ʶ����Ƶ
			System.out.println("File '" + getFilepath() + "' cannot be recognized as audio file.");
			return -1;
		}
        int length=file.getAudioHeader().getTrackLength(); 
        setMusicLength(length);
		//MP3File file = new MP3File(filename);
		ID3v1Tag tag1 = file.getID3v1Tag();
		if (tag1 != null) {
			try {
				String songName = tag1.getFirstTitle();
				setName(new String(songName.getBytes("ISO-8859-1"),"GB2312"));
		        String artist = tag1.getFirstArtist();
		        setComposer(new String(artist.getBytes("ISO-8859-1"),"GB2312"));
		        String album = tag1.getFirstAlbum();
		        setAlbum(new String(album.getBytes("ISO-8859-1"),"GB2312"));
			} catch(Exception e) {
				System.out.println("Tag exists, but not completed");
				return 0;
			}
			return 1;
		}
		
		//System.out.println("ID3v1Tag is null");
		return 0;
	}

}
