package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is == null) throw new RuntimeException("config.properties not found on classpath.");
            properties.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties.", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String getOrDefault(String key, String def) {
        String v = properties.getProperty(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}





