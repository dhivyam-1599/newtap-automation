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
    public static String getPolicyId(String tenant){return props.getProperty("policyName." + tenant);}

    public static String getPolicyIdentifier(String tenant){return props.getProperty("policyIdentifier." +tenant);}

    public static String getProductId(String tenant) {
        return props.getProperty("productId." + tenant);
    }

    public static String getTenant() {
        return System.getProperty(
                "cash-Tenant",
                props.getProperty("cash-Tenant")
        );
    }

}