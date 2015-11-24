package me.ben.lucene.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.lucene.index.IndexableField;

/**
 * 本地文件信息
 * @author Administrator
 *
 */
public class LocalFile implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -8011588141371464037L;
private String name;
private String content;
private String path;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}

}
