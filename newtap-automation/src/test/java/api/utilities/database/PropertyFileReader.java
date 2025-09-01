package api.utilities.database;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

    public class PropertyFileReader {

        private final Properties properties = new Properties();

        public PropertyFileReader(String fileName) throws IOException {
            loadProperties(fileName);
        }

        private void loadProperties(String fileName) throws IOException {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {

                if (input == null) {
                    throw new IOException("Unable to find configuration file: " + fileName);
                }
                properties.load(input);
            }
        }
        public String getProperty(String key) {
            return properties.getProperty(key);
        }

    }

