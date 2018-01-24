package cc.zhouyc.model;

/**
 * 定义音乐model，描述音乐的信息
 * 包含：
 * 		filepath	文件路径
 * 		name		音乐名
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
	// 学习一下如何重写equals....参数如果写Music是错滴...
		
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
