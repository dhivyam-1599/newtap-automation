package api.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {
    private static Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public static String getProductId(String tenant) {
        return props.getProperty("productId." + tenant);
    }

    public static String getTenant() {
        return props.getProperty("cash-Tenant");
    }
}