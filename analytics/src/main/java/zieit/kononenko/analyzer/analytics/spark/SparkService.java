package zieit.kononenko.analyzer.analytics.spark;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.col;
import org.springframework.stereotype.Service;
import zieit.kononenko.analyzer.analytics.config.SparkSessionConfig;
import zieit.kononenko.analyzer.analytics.constants.SchemaFields;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_EMAIL_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_FIRST_NAME_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_ID_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.CUSTOMER_LAST_NAME_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_ID_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_ITEMS_LEFT_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_TITLE_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PRODUCT_URL_FIELD;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_CUSTOMER_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_ORDER_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PRICE;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PRODUCT_ID;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_PURCHASE_TIMESTAMP;
import static zieit.kononenko.analyzer.analytics.constants.SchemaFields.PURCHASE_ITEM_QUANTITY;
import zieit.kononenko.analyzer.analytics.type.TableType;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SparkService {
    private final Map<TableType, Column[]> selectStatement = ImmutableMap.<TableType, Column[]>builder()
            .put(TableType.CUSTOMER,
                    new Column[]{
                            col(SchemaFields.POSTGRES_CUSTOMER_ID_FIELD).as(CUSTOMER_ID_FIELD),
                            col(SchemaFields.POSTGRES_CUSTOMER_FIRST_NAME_FIELD).as(CUSTOMER_FIRST_NAME_FIELD),
                            col(SchemaFields.POSTGRES_CUSTOMER_LAST_NAME_FIELD).as(CUSTOMER_LAST_NAME_FIELD),
                            col(SchemaFields.POSTGRES_CUSTOMER_EMAIL_FIELD).as(CUSTOMER_EMAIL_FIELD)
                    }
            )
            .put(TableType.PRODUCT,
                    new Column[]{
                            col(SchemaFields.POSTGRES_PRODUCT_ID_FIELD).as(PRODUCT_ID_FIELD),
                            col(SchemaFields.POSTGRES_PRODUCT_TITLE_FIELD).as(PRODUCT_TITLE_FIELD),
                            col(SchemaFields.POSTGRES_PRODUCT_URL_FIELD).as(PRODUCT_URL_FIELD),
                            col(SchemaFields.POSTGRES_PRODUCT_ITEMS_LEFT_FIELD).as(PRODUCT_ITEMS_LEFT_FIELD)
                    }
            )
            .put(TableType.PURCHASE,
                    new Column[]{
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_ID).as(PURCHASE_ITEM_ID),
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_ORDER_ID).as(PURCHASE_ITEM_ORDER_ID),
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_CUSTOMER_ID).as(PURCHASE_ITEM_CUSTOMER_ID),
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_PRODUCT_ID).as(PURCHASE_ITEM_PRODUCT_ID),
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_QUANTITY).as(PURCHASE_ITEM_QUANTITY),
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_PRICE).as(PURCHASE_ITEM_PRICE),
                            col(SchemaFields.POSTGRES_PURCHASE_ITEM_PURCHASE_TIMESTAMP).as(PURCHASE_ITEM_PURCHASE_TIMESTAMP)
                    }
            )
            .build();

    private final SparkSessionConfig sessionConfig;
    private final SessionProvider sessionProvider;

    public Dataset<Row> loadTable(String sparkSession, Long shopId, TableType table) {
        return sessionProvider.getSession(sparkSession)
                .read()
                .format("jdbc")
                .options(sessionConfig.getPostgres().getOptions())
                .option("dbtable", toShopSchemaName(shopId) + "." + table.getTableName())
                .load()
                .select(selectStatement.get(table));
    }

    private String toShopSchemaName(Long shopId) {
        return "shop_" + shopId;
    }

    public String generateUniqueSessionName(Long shopId) {
        return "Session_" + shopId + "_" + UUID.randomUUID();
    }
}

