package zieit.kononenko.analyzer.analytics.config;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.spark.SparkConf;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostgresSparkConfiguration extends SparkConnectionSource {
    @Value(value = "${spring.datasource.url}")
    private String url;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;

    @Override
    public SparkConf getConfigs() {
        return buildDefaultConfiguration();
    }

    public Map<String, String> getOptions() {
        return ImmutableMap.<String, String>builder()
                .put("url", url)
                .put("user", username)
                .put("password", password)
                .put("driver", "org.postgresql.Driver")
                .build();
    }
}
