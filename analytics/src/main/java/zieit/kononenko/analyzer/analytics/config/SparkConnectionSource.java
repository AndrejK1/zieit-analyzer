package zieit.kononenko.analyzer.analytics.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.SparkConf;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SparkConnectionSource {
    //See: https://spoddutur.github.io/spark-notes/distribution_of_executors_cores_and_memory_for_spark_application.html
    //AND: https://blog.cloudera.com/how-to-tune-your-apache-spark-jobs-part-2/
    //AND: https://spark.apache.org/docs/latest/configuration.html
    //AND: https://dzone.com/articles/spark-dynamic-allocation

    private String master;
    private int maxExecutors;
    private String memory;
    private int coresPerExecutor;
    private List<String> jars;
    private int retryWait;
    private int maxRetries;
    private int cores;
    private String warehousePath;
    private String localPath;

    public String getWarehousePath() {
        return Optional.ofNullable(warehousePath).orElse("spark-warehouse");
    }

    public String getLocalPath() {
        return Optional.ofNullable(localPath).orElse("/tmp/spark-shopify");
    }

    protected SparkConf buildDefaultConfiguration() {
        return new SparkConf()
                .setMaster(getMaster())
                .setJars(getJars().toArray(new String[0]))
                .set("spark.shuffle.service.enabled", "false")
                .set("spark.shuffle.io.retryWait", String.valueOf(getRetryWait()))
                .set("spark.shuffle.io.maxRetries", String.valueOf(getMaxRetries()))
                .set("spark.dynamicAllocation.enabled", "false")
                .set("spark.executor.instances", String.valueOf(getMaxExecutors()))
                .set("spark.executor.cores", String.valueOf(getCoresPerExecutor()))
                .set("spark.executor.memory", getMemory())
                .set("spark.sql.crossJoin.enabled", "true")
                .set("spark.sql.codegen.wholeStage", "false")
                .set("spark.driver.cores", String.valueOf(getCores()))
                .set("spark.sql.broadcastTimeout", String.valueOf(900))
                .set("spark.sql.autoBroadcastJoinThreshold", String.valueOf(10485760))
                .set("spark.sql.warehouse.dir", getWarehousePath())
                .set("spark.local.dir", getLocalPath())
                .set("spark.cores.max", String.valueOf(4));
    }

    public abstract SparkConf getConfigs();
}
