package zieit.kononenko.analyzer.analytics.task;


import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.Window;
import static org.apache.spark.sql.functions.avg;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.countDistinct;
import static org.apache.spark.sql.functions.date_trunc;
import static org.apache.spark.sql.functions.first;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.min;
import static org.apache.spark.sql.functions.percent_rank;
import static org.apache.spark.sql.functions.sum;
import static org.apache.spark.sql.functions.unix_timestamp;
import static org.apache.spark.sql.functions.when;
import org.springframework.stereotype.Component;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_EMAIL_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_FIRST_NAME_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_ID_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_LAST_NAME_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_ID_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_TITLE_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_URL_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_CUSTOMER_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_ORDER_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PRICE;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PRODUCT_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PURCHASE_TIMESTAMP;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_QUANTITY;
import zieit.kononenko.analyzer.analytics.entity.AnalyticsReportEntity;
import zieit.kononenko.analyzer.analytics.service.AnalyticsReportService;
import zieit.kononenko.analyzer.analytics.spark.SparkService;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsReport;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsTaskRequest;
import zieit.kononenko.analyzer.analytics.type.TableType;
import static zieit.kononenko.analyzer.analytics.utils.ScalaUtils.toSeq;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnalyticsTask implements Task {
    //    customer analytics fields
    private static final String CUSTOMER_TOTAL_VALUE_FIELD = "customer_total_value";
    private static final String FIRST_CUSTOMER_PURCHASE_TIMESTAMP = "customer_first_purchase_timestamp";
    private static final String LAST_CUSTOMER_PURCHASE_TIMESTAMP = "customer_last_purchase_timestamp";
    private static final String CUSTOMER_PURCHASES_COUNT = "customer_purchases_count";

    //    product analytics fields
    private static final String PRODUCT_TOTAL_ITEMS_BOUGHT = "product_total_items_bought";
    private static final String PRODUCT_TOTAL_VALUE = "product_total_value";
    private static final String PRODUCT_VALUE_RANK = "product_value_rank";
    private static final String PRODUCT_SEGMENT = "product_segment";
    private static final String PREVIOUS_TOTALS_SUM = "sum_of_previous_totals";

    //    purchase analytics fields
    private static final String PURCHASE_LINE_VALUE = "purchase_line_value";
    private static final String LAST_PURCHASE_RECENCY = "last_purchase_recency";
    private static final String PURCHASE_FREQUENCY = "purchase_frequency";
    private static final String PURCHASE_FREQUENCY_RANK = "purchase_frequency_rank";
    private static final String PURCHASE_MONETARY_RANK = "purchase_monetary_rank";
    private static final String PURCHASE_RECENCY_SEGMENT = "recency_segment";
    private static final String PURCHASE_FREQUENCY_SEGMENT = "frequency_segment";
    private static final String PURCHASE_MONETARY_SEGMENT = "monetary_segment";

    //    time points analytics fields
    private static final String DAY_TIMESTAMP_FIELD = "day_timestamp";

    private static final int DAYS_TO_PREDICT = 60;
    private final SparkService sparkService;
    private final AnalyticsReportService analyticsReportService;

    @Override
    public void accept(AnalyticsTaskRequest request) {
        String sparkSession = sparkService.generateUniqueSessionName(request.getShopId());

        Dataset<Row> customerData = sparkService.loadTable(sparkSession, request.getShopId(), TableType.CUSTOMER);
        Dataset<Row> productData = sparkService.loadTable(sparkSession, request.getShopId(), TableType.PRODUCT);
        Dataset<Row> purchaseData = sparkService.loadTable(sparkSession, request.getShopId(), TableType.PURCHASE);

        // old implementation
        long uniqueCustomerCount = customerData.count();
        long uniqueProductCount = productData.count();
        long uniquePurchasesCount = purchaseData.select(countDistinct(PURCHASE_ITEM_ORDER_ID))
                .collectAsList().get(0).getLong(0);

        Dataset<Row> allJoinedData = purchaseData
                .filter(col(PURCHASE_ITEM_PURCHASE_TIMESTAMP).$less$eq(Timestamp.valueOf(request.getPeriodEnd()))
                        .and(col(PURCHASE_ITEM_PURCHASE_TIMESTAMP).$greater$eq(Timestamp.valueOf(request.getPeriodStart()))))
                .join(customerData, customerData.col(CUSTOMER_ID_FIELD).equalTo(purchaseData.col(PURCHASE_ITEM_CUSTOMER_ID)))
                .join(productData, productData.col(PRODUCT_ID_FIELD).equalTo(purchaseData.col(PURCHASE_ITEM_PRODUCT_ID)))
                .withColumn(PURCHASE_LINE_VALUE, col(PURCHASE_ITEM_PRICE).multiply(col(PURCHASE_ITEM_QUANTITY)));

        long activeCustomersCount = allJoinedData.select(countDistinct(PURCHASE_ITEM_CUSTOMER_ID))
                .collectAsList().get(0).getLong(0);

        long uniqueBoughtProductCount = allJoinedData.select(countDistinct(PURCHASE_ITEM_PRODUCT_ID))
                .collectAsList().get(0).getLong(0);

        long uniquePurchasesInSpecifiedPeriod = allJoinedData.select(countDistinct(PURCHASE_ITEM_ORDER_ID))
                .collectAsList().get(0).getLong(0);

        long boughtProductItemCount = allJoinedData.select(sum(PURCHASE_ITEM_QUANTITY))
                .collectAsList().get(0).getLong(0);

        Double totalValue = getDouble(allJoinedData.select(sum(PURCHASE_LINE_VALUE))
                .collectAsList().get(0), 0);

        Double averagePurchaseValue = getDouble(allJoinedData.select(avg(PURCHASE_LINE_VALUE))
                .collectAsList().get(0), 0);

        Double averagePurchaseItemsCount = getDouble(allJoinedData.select(avg(PURCHASE_ITEM_QUANTITY))
                .collectAsList().get(0), 0);

        Dataset<Row> productWithStatsDataset = allJoinedData.groupBy(col(PURCHASE_ITEM_PRODUCT_ID))
                .agg(
                        first(col(PRODUCT_TITLE_FIELD)).as(PRODUCT_TITLE_FIELD),
                        first(col(PRODUCT_URL_FIELD)).as(PRODUCT_URL_FIELD),
                        // new fields for analytics
                        sum(col(PURCHASE_ITEM_QUANTITY)).as(PRODUCT_TOTAL_ITEMS_BOUGHT),
                        sum(col(PURCHASE_LINE_VALUE)).as(PRODUCT_TOTAL_VALUE)
                )
                .withColumn(PREVIOUS_TOTALS_SUM, sum(PRODUCT_TOTAL_VALUE).over(Window.orderBy(col(PRODUCT_TOTAL_VALUE).desc())
                        .rowsBetween(Window.unboundedPreceding(), Window.currentRow())))
                .withColumn(PRODUCT_VALUE_RANK, col(PREVIOUS_TOTALS_SUM).divide(totalValue))
                .withColumn(PRODUCT_SEGMENT,
                        when(col(PRODUCT_VALUE_RANK).$less$eq(0.8), "A")
                                .otherwise(when(col(PRODUCT_VALUE_RANK).$less$eq(0.95), "B")
                                        .otherwise("C"))
                )
                .sort(col(PRODUCT_TOTAL_VALUE).desc());

        Dataset<Row> firstCustomerPurchase = purchaseData.groupBy(col(PURCHASE_ITEM_CUSTOMER_ID).as(CUSTOMER_ID_FIELD))
                .agg(min(col(PURCHASE_ITEM_PURCHASE_TIMESTAMP)).as(FIRST_CUSTOMER_PURCHASE_TIMESTAMP))
                .withColumn(FIRST_CUSTOMER_PURCHASE_TIMESTAMP,
                        when(col(FIRST_CUSTOMER_PURCHASE_TIMESTAMP).$greater$eq(Timestamp.valueOf(request.getPeriodStart())),
                                col(FIRST_CUSTOMER_PURCHASE_TIMESTAMP))
                                .otherwise(lit(Timestamp.valueOf(request.getPeriodStart()))));

        Dataset<Row> customersWithPurchasesInfo = allJoinedData.groupBy(col(CUSTOMER_ID_FIELD))
                .agg(
                        first(col(CUSTOMER_FIRST_NAME_FIELD)).as(CUSTOMER_FIRST_NAME_FIELD),
                        first(col(CUSTOMER_LAST_NAME_FIELD)).as(CUSTOMER_LAST_NAME_FIELD),
                        first(col(CUSTOMER_EMAIL_FIELD)).as(CUSTOMER_EMAIL_FIELD),
                        // new field for analytics
                        countDistinct(PURCHASE_ITEM_ORDER_ID).as(CUSTOMER_PURCHASES_COUNT),
                        sum(col(PURCHASE_LINE_VALUE)).as(CUSTOMER_TOTAL_VALUE_FIELD),
                        max(col(PURCHASE_ITEM_PURCHASE_TIMESTAMP)).as(LAST_CUSTOMER_PURCHASE_TIMESTAMP))
                .join(firstCustomerPurchase, toSeq(CUSTOMER_ID_FIELD))
                .withColumn(PURCHASE_FREQUENCY,
                        col(CUSTOMER_PURCHASES_COUNT).multiply(lit(30 * 24 * 60 * 60)).divide(
                                unix_timestamp(lit(Timestamp.valueOf(request.getPeriodEnd())), "yyyy-MM-dd hh:mm:ss.SSSSSSZ")
                                        .minus(unix_timestamp(col(FIRST_CUSTOMER_PURCHASE_TIMESTAMP), "yyyy-MM-dd hh:mm:ss.SSSSSSZ"))
                        ))
                .withColumn(LAST_PURCHASE_RECENCY, percent_rank().over(Window.orderBy(LAST_CUSTOMER_PURCHASE_TIMESTAMP)))
                .withColumn(PURCHASE_FREQUENCY_RANK, percent_rank().over(Window.orderBy(PURCHASE_FREQUENCY)))
                .withColumn(PURCHASE_MONETARY_RANK, percent_rank().over(Window.orderBy(CUSTOMER_TOTAL_VALUE_FIELD)))
                .orderBy(col(CUSTOMER_TOTAL_VALUE_FIELD).desc());

        customersWithPurchasesInfo = customersWithPurchasesInfo
                .withColumn(PURCHASE_RECENCY_SEGMENT,
                        when(col(LAST_PURCHASE_RECENCY).$greater$eq(0.67), "A")
                                .otherwise(when(col(LAST_PURCHASE_RECENCY).$greater$eq(0.33), "B")
                                        .otherwise("C"))
                )
                .withColumn(PURCHASE_FREQUENCY_SEGMENT,
                        when(col(PURCHASE_FREQUENCY_RANK).$greater$eq(0.67), "A")
                                .otherwise(when(col(PURCHASE_FREQUENCY_RANK).$greater$eq(0.33), "B")
                                        .otherwise("C"))
                )
                .withColumn(PURCHASE_MONETARY_SEGMENT,
                        when(col(PURCHASE_MONETARY_RANK).$greater$eq(0.67), "A")
                                .otherwise(when(col(PURCHASE_MONETARY_RANK).$greater$eq(0.33), "B")
                                        .otherwise("C"))
                );

        List<AnalyticsReport.Customer> customersWithStats = customersWithPurchasesInfo
                .collectAsList()
                .stream()
                .map(this::mapRowToCustomer)
                .collect(Collectors.toList());

        List<AnalyticsReport.Product> productsWithStats = productWithStatsDataset
                .collectAsList()
                .stream()
                .map(this::mapRowToProduct)
                .collect(Collectors.toList());

        List<AnalyticsReport.ChartPoint> revenueValueChartByDay = collectChartPoints(allJoinedData);

        AnalyticsReport report = AnalyticsReport.builder()
                .customerCount(uniqueCustomerCount)
                .activeCustomerCount(activeCustomersCount)
                .uniqueProductCount(uniqueProductCount)
                .uniqueBoughtProductCount(uniqueBoughtProductCount)
                .boughtProductItemCount(boughtProductItemCount)
                .purchasesCount(uniquePurchasesCount)
                .uniquePurchasesInSpecifiedPeriod(uniquePurchasesInSpecifiedPeriod)
                .totalValue(totalValue)
                .averagePurchaseValue(averagePurchaseValue)
                .averagePurchaseUniqueItemsCount(averagePurchaseItemsCount)
                .customersWithStats(customersWithStats)
                .productsWithStats(productsWithStats)
                .revenueValueChartByDay(revenueValueChartByDay)
                .build();

        analyticsReportService.save(
                new AnalyticsReportEntity(System.currentTimeMillis(),
                        request.getShopId(),
                        report,
                        request.getPeriodStart(),
                        request.getPeriodEnd()
                )
        );
    }

    private List<AnalyticsReport.ChartPoint> collectChartPoints(Dataset<Row> orderDataset) {
        List<AnalyticsReport.ChartPoint> existingPoints = orderDataset
                .withColumn(DAY_TIMESTAMP_FIELD, date_trunc("DAY", col(PURCHASE_ITEM_PURCHASE_TIMESTAMP)))
                .groupBy(col(DAY_TIMESTAMP_FIELD))
                .agg(sum(col(PURCHASE_LINE_VALUE)).as(PRODUCT_TOTAL_VALUE))
                .orderBy(col(DAY_TIMESTAMP_FIELD))
                .collectAsList()
                .stream()
                .map(this::mapRowToChartPoint)
                .collect(Collectors.toList());

        // calculate regression
        SimpleRegression simpleRegression = new SimpleRegression();

        double[] x = new double[existingPoints.size()];
        double[] y = new double[existingPoints.size()];

        for (int i = 0; i < existingPoints.size(); i++) {
            simpleRegression.addData(i, existingPoints.get(i).getValue());
        }

        LocalDate lastExistingDate = existingPoints.get(existingPoints.size() - 1).getDate();

        for (int i = 0; i < DAYS_TO_PREDICT; i++) {
            existingPoints.add(new AnalyticsReport.ChartPoint(
                    lastExistingDate.plusDays(i + 1),
                    simpleRegression.predict(existingPoints.size() + (double) i),
                    true));
        }

        return existingPoints;
    }

    private AnalyticsReport.ChartPoint mapRowToChartPoint(Row row) {
        return new AnalyticsReport.ChartPoint(row.getTimestamp(0).toLocalDateTime().toLocalDate(), getDouble(row, 1), false);
    }

    private AnalyticsReport.Product mapRowToProduct(Row row) {
        return AnalyticsReport.Product.builder()
                .id(row.getString(0))
                .title(row.getString(1))
                .shopUrl(row.getString(2))
                .totalRevenue(row.getDecimal(4))
                .segment(row.getString(7))
                .build();
    }

    private AnalyticsReport.Customer mapRowToCustomer(Row row) {
        return AnalyticsReport.Customer.builder()
                .id(row.getString(0))
                .firstName(row.getString(1))
                .lastName(row.getString(2))
                .email(row.getString(3))
                .purchasesCount(row.getLong(4))
                .totalValue(getDouble(row, 5))
                .lastPurchaseTimestamp(row.getTimestamp(6).toLocalDateTime())
                .firstPurchaseTimestamp(row.getTimestamp(7).toLocalDateTime())
                .recencySegment(row.getString(12))
                .frequencySegment(row.getString(13))
                .monetarySegment(row.getString(14))
                .build();
    }

    private Double getDouble(Row row, int index) {
        return ((Number) row.getAs(index)).doubleValue();
    }
}
