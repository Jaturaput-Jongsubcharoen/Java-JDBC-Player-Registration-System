package playerregistration.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnectionManager {

    private static final String CONFIG_PATH_PROPERTY = "db.config.path";
    private static final String CONFIG_PATH_ENV = "DB_CONFIG_PATH";
    private static final Path DEFAULT_CONFIG_PATH = Path.of("database", "db.properties");

    private static volatile Properties dbProperties;

    private DatabaseConnectionManager() {
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = getDbProperties();
        String dbUrl = properties.getProperty("db.url");
        String dbUser = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.password");

        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static void initialize() {
        Properties properties = getDbProperties();
        initializeDriverForUrl(properties.getProperty("db.url"));
    }

    private static Properties getDbProperties() {
        if (dbProperties == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (dbProperties == null) {
                    dbProperties = loadAndValidateProperties();
                }
            }
        }
        return dbProperties;
    }

    private static Properties loadAndValidateProperties() {
        Path configPath = resolveConfigPath();
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Database configuration file was not found or could not be read: " + configPath, e);
        }

        validateRequiredProperty(properties, "db.url", configPath);
        validateRequiredProperty(properties, "db.user", configPath);
        validateRequiredProperty(properties, "db.password", configPath);

        return properties;
    }

    private static Path resolveConfigPath() {
        String fromProperty = System.getProperty(CONFIG_PATH_PROPERTY);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return Path.of(fromProperty.trim());
        }

        String fromEnv = System.getenv(CONFIG_PATH_ENV);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return Path.of(fromEnv.trim());
        }

        return DEFAULT_CONFIG_PATH;
    }

    private static void validateRequiredProperty(Properties properties, String key, Path configPath) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required database property '" + key + "' in " + configPath);
        }
    }

    private static void loadOracleDriver() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException primary) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException secondary) {
                throw new IllegalStateException("Oracle JDBC driver is not available on the classpath.", secondary);
            }
        }
    }

    private static void initializeDriverForUrl(String dbUrl) {
        String trimmedUrl = dbUrl == null ? "" : dbUrl.trim();

        if (trimmedUrl.startsWith("jdbc:oracle:")) {
            loadOracleDriver();
            return;
        }

        if (trimmedUrl.startsWith("jdbc:h2:")) {
            return;
        }

        throw new IllegalStateException("Unsupported JDBC URL in db.url: " + trimmedUrl);
    }
}
