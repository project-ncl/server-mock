package org.jboss.pnc.servermock;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Configuration {

    /**
     * @return Environment variable or System property for given key where Environment variable overrides the System property.
     */
    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value != null) {
            return value;
        }
        value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
