package com.founder.generator;

/**
 * 保存JAVA对象中的属性信息
 * 
 */
public class FieldJavaDesc {
	
	private String fieldName;

	private String fieldType;
	
	private String fieldComment;

	public String getFieldName() {
		return this.fieldName;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldComment() {
		return fieldComment;
	}

	public void setFieldComment(String fieldComment) {
		this.fieldComment = fieldComment;
	}
}