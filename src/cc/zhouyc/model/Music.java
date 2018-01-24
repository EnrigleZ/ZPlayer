package cc.zhouyc.model;

/**
 * ��������model���������ֵ���Ϣ
 * ������
 * 		filepath	�ļ�·��
 * 		name		������
 * 
 * @author Fred
 *
 */
public class Music {
	private String filepath;

	public String getFilepath() {
		return filepath;
	}

	private void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	private String name;
	
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object arg) {
	// ѧϰһ�������дequals....�������дMusic�Ǵ��...
		
		if (arg != null && arg instanceof Music) {
			return filepath.equals(((Music)arg).filepath);
			// Using equals() instead of '==' .....
		}
		else return false;
	}
	
	public Music(String path) {
		filepath = path;
	}
}
