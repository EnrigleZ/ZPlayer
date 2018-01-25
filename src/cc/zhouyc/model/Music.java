package cc.zhouyc.model;

/**
 * ��������model���������ֵ���Ϣ
 * ������
 * 		filepath	�ļ�·��
 * 		name		������
 * 
 * @author ZhouYC
 *
 */
public class Music {
	
	private Integer id;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	private String filepath;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return name + " - " + filepath;
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
	
	@Override
	public int hashCode() {
		return filepath.hashCode();
	}
	
	public Music(String path) {
		filepath = path;
	}
}
