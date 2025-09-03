package api.utilities;

import api.utilities.database.PropertyFileReader;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class HeaderUtil {

    private static final PropertyFileReader reader;

    static {
        try {
            reader = new PropertyFileReader("config.properties");
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config.properties", e);
        }
    }

    public static RequestSpecification getHeaders(String serviceType) {
        Map<String, String> headers = new HashMap<>();

        switch (serviceType.toUpperCase()) {
            case "LAS":
                headers.put("X-Tenant", reader.getProperty("las-Tenant"));
                headers.put("X-Lob", "LAS");
                headers.put("X-LSP", "CRED");
                headers.put("X-LSP-User-Id", reader.getProperty("las-LSP-User-Id"));
                break;

            case "CASH":
                headers.put("X-Tenant", reader.getProperty("cash-Tenant"));
                headers.put("X-LSP", "CASH");
                headers.put("X-Lob", "CASH");
                headers.put("X-LSP-User-Id", reader.getProperty("cash-LSP-User-Id"));
                break;

            case "NEWTAP100%":
                headers.put("X-Tenant", reader.getProperty("newtap-Tenant"));
                headers.put("X-LSP", "CASH");
                headers.put("X-Lob", "CASH");
                headers.put("X-LSP-User-Id", reader.getProperty("newtap-LSP-User-Id"));
                break;

            default:
                throw new IllegalArgumentException("Unknown service type: " + serviceType);
        }

        headers.put("X-App-Token", reader.getProperty("app-token"));
        headers.put("X-User-Id", reader.getProperty("user-id"));
        headers.put("Content-Type", "application/json");

        RequestSpecification request = given();
        headers.forEach(request::header);
        return request;
    }
}
