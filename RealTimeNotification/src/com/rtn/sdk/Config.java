package com.rtn.sdk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config{

	static Properties prop;
	

	public static Config getConfig(){
		Config config = new Config();
		config.prop = config.getPropertiesFromClasspath("Config.properties");
		return config;
	}
	

	private  Properties getPropertiesFromClasspath(String propFileName){
		Properties props = new Properties();
		InputStream inputStream;
		try {
			inputStream = getClass().getClassLoader().getResource(propFileName).openStream();
			if (inputStream == null){
				throw new FileNotFoundException("property file '" + propFileName
						+ "' not found in the classpath");
			}

			props.load(inputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return props;
	}

	public    String get(String Key){
		return  prop.getProperty(Key);
		
	}
	
	public  boolean put(String Key, String value){
		prop.setProperty(Key, value);
		return true;
	}

}