package zieit.kononenko.analyzer.analytics.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@ConfigurationProperties(prefix = "spark")
public class SparkSessionConfig {
    private final PostgresSparkConfiguration postgres = new PostgresSparkConfiguration();
}
