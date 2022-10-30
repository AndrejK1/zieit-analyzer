package zieit.kononenko.analyzer.analytics.task;


import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Component;
import zieit.kononenko.analyzer.analytics.spark.SparkService;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsTaskRequest;
import zieit.kononenko.analyzer.analytics.type.TableType;

@Component
@RequiredArgsConstructor
public class AnalyticsTask implements Task {
    private final SparkService sparkService;

    @Override
    public void accept(AnalyticsTaskRequest request) {
        String sparkSession = sparkService.generateUniqueSessionName(request.getShopId());

        Dataset<Row> customerData = sparkService.loadTable(sparkSession, request.getShopId(), TableType.CUSTOMER);
        Dataset<Row> productData = sparkService.loadTable(sparkSession, request.getShopId(), TableType.PRODUCT);
        Dataset<Row> orderData = sparkService.loadTable(sparkSession, request.getShopId(), TableType.PURCHASE);


    }
}
