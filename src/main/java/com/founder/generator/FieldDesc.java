package com.founder.generator;

public class FieldDesc {
	
	private String fieldName;// 字段名

	private int fieldType;// 字段类型
	
	private int dataScale;// 字段精度

	private boolean nullable;// 字段是否可以为空

	private String columnClassName;
	
	private String comments;//字段描述

	public String getFieldName() {
		return this.fieldName;
	}

	public int getFieldType() {
		return this.fieldType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public int getDataScale() {
		return dataScale;
	}

	public void setDataScale(int dataScale) {
		this.dataScale = dataScale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getColumnClassName() {
		return columnClassName;
	}

	public void setColumnClassName(String columnClassName) {
		this.columnClassName = columnClassName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
