package zieit.kononenko.analyzer.analytics.spark;

import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;
import zieit.kononenko.analyzer.analytics.config.SparkSessionConfig;

@Component
@RequiredArgsConstructor
public class SessionProvider {
    private final SparkSessionConfig sessionConfig;

    public SparkSession getSession(String taskName) {
        return SparkSession.builder()
                .appName(taskName)
                .config(sessionConfig.getPostgres().getConfigs())
                .getOrCreate();

    }
}
