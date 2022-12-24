package zieit.kononenko.analyzer.analytics.task;


import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.avg;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.countDistinct;
import static org.apache.spark.sql.functions.date_trunc;
import static org.apache.spark.sql.functions.first;
import static org.apache.spark.sql.functions.sum;
import org.springframework.stereotype.Component;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_EMAIL_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_FIRST_NAME_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_ID_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_LAST_NAME_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_ID_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_ITEMS_LEFT_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_TITLE_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_URL_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_CUSTOMER_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_ORDER_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PRICE;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PRODUCT_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PURCHASE_TIMESTAMP;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_QUANTITY;
import zieit.kononenko.analyzer.analytics.spark.SparkService;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsReport;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsTaskRequest;
import zieit.kononenko.analyzer.analytics.task.vo.ChartPoint;
import zieit.kononenko.analyzer.analytics.task.vo.Customer;
import zieit.kononenko.analyzer.analytics.task.vo.Product;
import zieit.kononenko.analyzer.analytics.type.TableType;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnalyticsTask implements Task {
    private static final String PURCHASE_LINE_VALUE_FIELD = "purchase_line_value";
    private static final String PRODUCT_TOTAL_ITEMS_BOUGHT_FIELD = "product_total_items_bought";
    private static final String PRODUCT_TOTAL_VALUE_FIELD = "product_total_value";
    private static final String DAY_TIMESTAMP_FIELD = "day_timestamp";
    private final SparkService sparkService;

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
                .withColumn(PURCHASE_LINE_VALUE_FIELD, col(PURCHASE_ITEM_PRICE).multiply(col(PURCHASE_ITEM_QUANTITY)));

        long activeCustomersCount = allJoinedData.select(countDistinct(PURCHASE_ITEM_CUSTOMER_ID))
                .collectAsList().get(0).getLong(0);

        long uniqueBoughtProductCount = allJoinedData.select(countDistinct(PURCHASE_ITEM_PRODUCT_ID))
                .collectAsList().get(0).getLong(0);

        long uniquePurchasesInSpecifiedPeriod = allJoinedData.select(countDistinct(PURCHASE_ITEM_ORDER_ID))
                .collectAsList().get(0).getLong(0);

        long boughtProductItemCount = allJoinedData.select(sum(PURCHASE_ITEM_QUANTITY))
                .collectAsList().get(0).getLong(0);

        double totalValue = allJoinedData.select(sum(PURCHASE_LINE_VALUE_FIELD))
                .collectAsList().get(0).getDouble(0);

        double averagePurchaseValue = allJoinedData.select(avg(PURCHASE_LINE_VALUE_FIELD))
                .collectAsList().get(0).getDouble(0);

        double averagePurchaseItemsCount = allJoinedData.select(avg(PURCHASE_ITEM_QUANTITY))
                .collectAsList().get(0).getDouble(0);

        Dataset<Row> customersWithPurchasesInfo = allJoinedData.groupBy(col(PURCHASE_ITEM_CUSTOMER_ID))
                .agg(
                        first(col(PURCHASE_ITEM_CUSTOMER_ID)).as(CUSTOMER_ID_FIELD),
                        first(col(CUSTOMER_FIRST_NAME_FIELD)).as(CUSTOMER_FIRST_NAME_FIELD),
                        first(col(CUSTOMER_LAST_NAME_FIELD)).as(CUSTOMER_LAST_NAME_FIELD),
                        first(col(CUSTOMER_EMAIL_FIELD)).as(CUSTOMER_EMAIL_FIELD),
                        // new field for analytics
                        sum(col(PURCHASE_LINE_VALUE_FIELD)).as(PRODUCT_TOTAL_VALUE_FIELD)
                );

        List<Customer> top5CustomersByValue = customersWithPurchasesInfo.orderBy(col(PRODUCT_TOTAL_VALUE_FIELD))
                .limit(5)
                .collectAsList()
                .stream()
                .map(this::mapRowToCustomer)
                .collect(Collectors.toList());

        Dataset<Row> productsWithPurchasesInfo = allJoinedData.groupBy(col(PURCHASE_ITEM_PRODUCT_ID))
                .agg(
                        first(col(PURCHASE_ITEM_PRODUCT_ID)).as(PRODUCT_ID_FIELD),
                        first(col(PRODUCT_TITLE_FIELD)).as(PRODUCT_TITLE_FIELD),
                        first(col(PRODUCT_URL_FIELD)).as(PRODUCT_URL_FIELD),
                        first(col(PRODUCT_ITEMS_LEFT_FIELD)).as(PRODUCT_ITEMS_LEFT_FIELD),
                        // new fields for analytics
                        sum(col(PURCHASE_ITEM_QUANTITY)).as(PRODUCT_TOTAL_ITEMS_BOUGHT_FIELD),
                        sum(col(PURCHASE_LINE_VALUE_FIELD)).as(PRODUCT_TOTAL_VALUE_FIELD)
                );

        List<Product> top5ProductsByGeneratedValue = productsWithPurchasesInfo.orderBy(col(PRODUCT_TOTAL_ITEMS_BOUGHT_FIELD))
                .limit(5)
                .collectAsList()
                .stream()
                .map(this::mapRowToProduct)
                .collect(Collectors.toList());

        List<Product> top5ProductsByItemCount = productsWithPurchasesInfo.orderBy(col(PRODUCT_TOTAL_ITEMS_BOUGHT_FIELD))
                .limit(5)
                .collectAsList()
                .stream()
                .map(this::mapRowToProduct)
                .collect(Collectors.toList());

        List<ChartPoint> revenueValueChartByDay = productsWithPurchasesInfo
                .withColumn(DAY_TIMESTAMP_FIELD, date_trunc("DAY", col(PURCHASE_ITEM_PURCHASE_TIMESTAMP)))
                .groupBy(col(DAY_TIMESTAMP_FIELD))
                .agg(sum(col(PURCHASE_LINE_VALUE_FIELD)).as(PRODUCT_TOTAL_VALUE_FIELD))
                .orderBy(col(DAY_TIMESTAMP_FIELD))
                .collectAsList()
                .stream()
                .map(this::mapRowToChartPoint)
                .collect(Collectors.toList());

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
                .top5CustomersByValue(top5CustomersByValue)
                .top5ProductsByGeneratedValue(top5ProductsByGeneratedValue)
                .top5ProductsByItemCount(top5ProductsByItemCount)
                .revenueValueChartByDay(revenueValueChartByDay)
                .build();
    }

    private ChartPoint mapRowToChartPoint(Row row) {
        return new ChartPoint(row.getTimestamp(0).toLocalDateTime().toLocalDate(), row.getDouble(1), false);
    }

    private Product mapRowToProduct(Row row) {
        return Product.builder()
                .id(row.getString(0))
                .title(row.getString(1))
                .shopUrl(row.getString(2))
                .itemsLeft(row.getLong(3))
                .build();
    }

    private Customer mapRowToCustomer(Row row) {
        return Customer.builder()
                .id(row.getString(0))
                .firstName(row.getString(1))
                .lastName(row.getString(2))
                .email(row.getString(3))
                .build();
    }
}
