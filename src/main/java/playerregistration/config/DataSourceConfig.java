package playerregistration.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {

    private static final String CONFIG_PATH_PROPERTY = "db.config.path";
    private static final String CONFIG_PATH_ENV = "DB_CONFIG_PATH";
    private static final Path DEFAULT_CONFIG_PATH = Path.of("database", "db.properties");

    @Bean
    public DataSource dataSource() {
        Properties properties = loadAndValidateProperties();
        String dbUrl = properties.getProperty("db.url").trim();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(properties.getProperty("db.user").trim());
        dataSource.setPassword(properties.getProperty("db.password"));

        if (dbUrl.startsWith("jdbc:oracle:")) {
            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        } else if (dbUrl.startsWith("jdbc:h2:")) {
            dataSource.setDriverClassName("org.h2.Driver");
        } else {
            throw new IllegalStateException("Unsupported JDBC URL in db.url: " + dbUrl);
        }

        return dataSource;
    }

    private Properties loadAndValidateProperties() {
        Path configPath = resolveConfigPath();
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Database configuration file was not found or could not be read: " + configPath,
                    e
            );
        }

        validateRequiredProperty(properties, "db.url", configPath);
        validateRequiredProperty(properties, "db.user", configPath);
        validateRequiredProperty(properties, "db.password", configPath);
        return properties;
    }

    private Path resolveConfigPath() {
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

    private void validateRequiredProperty(Properties properties, String key, Path configPath) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required database property '" + key + "' in " + configPath);
        }
    }
}
