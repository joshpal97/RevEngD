package csse374.revengd.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class SettingsFileLoader {
	
	@SuppressWarnings("resource")
	public void loadSettings(Map<String, String> configMap) {
		String rootPath = Paths.get(".").toString();
		rootPath = "";
		 
		String defaultConfigPath = rootPath + "default.properties";
		Properties defaultProps = new Properties();
		InputStream fin;
		try {
			fin = new FileInputStream(defaultConfigPath);
			defaultProps.load(fin);
			fin.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
		String userPath = null;
		if (configMap.containsKey("settings")) {
			userPath = configMap.get("settings");
		}
		if (null != userPath) {
			String appConfigPath = rootPath + userPath;
			Properties appProps = new Properties(defaultProps);
			
			try {
				fin = new FileInputStream(appConfigPath);
				appProps.load(fin);
				fin.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			SettingsFileLoader.loadFromProp(appProps, configMap);
		} else {
			SettingsFileLoader.loadFromProp(defaultProps, configMap);
		}
	}
	
	private static void loadFromProp(Properties prop, Map<String, String> configMap) {
		prop.stringPropertyNames().forEach(name -> {
			if (!configMap.containsKey(name)) {
				configMap.put(name, prop.getProperty(name));
			}
		});
	}
	
}
