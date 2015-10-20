package me.ben.lucene.bean;

import java.util.List;

import org.apache.lucene.index.IndexableField;

public class FileBean {
private String name;
private String content;
private String path;
private List<IndexableField> fields;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public List<IndexableField> getFields() {
	return fields;
}
public void setFields(List<IndexableField> fields) {
	this.fields = fields;
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
