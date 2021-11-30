package com.founder.generator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyHelper {
	public static Map<String, Properties> propertyHm = new HashMap<String, Properties>();

	public static Properties getJdbcProperty() {
		return getProperty("jdbc.properties");
	}

	public static Properties getProperty(String fileName) {
		try {
			Properties property = propertyHm.get(fileName);
			if (property == null) {
				InputStream stream = null;
				try {
					URL fileUrl = PropertyHelper.class.getResource("/"
							+ fileName);
					String filePath = URLDecoder.decode(fileUrl.getFile(),
							"UTF-8");
					stream = new java.io.FileInputStream(filePath);
					property = new Properties();
					property.load(stream);
				} catch (IOException ex) {
					return null;
				} finally {
					if (stream != null) {
						stream.close();
						stream = null;
					}
				}
				propertyHm.put(fileName, property);
			}
			return property;
		} catch (IOException ex) {
			return null;
		}
	}
	public static Properties getOtherProperty(String propertyName){
		return getProperty(propertyName);
	}
}
