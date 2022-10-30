package zieit.kononenko.analyzer.analytics.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaFields {
    public static final String POSTGRES_CUSTOMER_ID_FIELD = "id";
    public static final String POSTGRES_CUSTOMER_FIRST_NAME_FIELD = "first_name";
    public static final String POSTGRES_CUSTOMER_LAST_NAME_FIELD = "last_name";
    public static final String POSTGRES_CUSTOMER_EMAIL_FIELD = "email";

    public static final String POSTGRES_PRODUCT_ID_FIELD = "id";
    public static final String POSTGRES_PRODUCT_TITLE_FIELD = "title";
    public static final String POSTGRES_PRODUCT_URL_FIELD = "url";
    public static final String POSTGRES_PRODUCT_ITEMS_LEFT_FIELD = "items_left";

    public static final String POSTGRES_PURCHASE_ITEM_ID = "id";
    public static final String POSTGRES_PURCHASE_ITEM_ORDER_ID = "order_id";
    public static final String POSTGRES_PURCHASE_ITEM_CUSTOMER_ID = "customer_id";
    public static final String POSTGRES_PURCHASE_ITEM_PRODUCT_ID = "product_id";
    public static final String POSTGRES_PURCHASE_ITEM_QUANTITY = "quantity";
    public static final String POSTGRES_PURCHASE_ITEM_PRICE = "price";
    public static final String POSTGRES_PURCHASE_ITEM_PURCHASE_TIMESTAMP = "purchase_timestamp";

    private static final String CUSTOMER_PREFIX = "customer_";
    public static final String CUSTOMER_ID_FIELD = CUSTOMER_PREFIX + POSTGRES_CUSTOMER_ID_FIELD;
    public static final String CUSTOMER_FIRST_NAME_FIELD = CUSTOMER_PREFIX + POSTGRES_CUSTOMER_FIRST_NAME_FIELD;
    public static final String CUSTOMER_LAST_NAME_FIELD = CUSTOMER_PREFIX + POSTGRES_CUSTOMER_LAST_NAME_FIELD;
    public static final String CUSTOMER_EMAIL_FIELD = CUSTOMER_PREFIX + POSTGRES_CUSTOMER_EMAIL_FIELD;

    private static final String PRODUCT_PREFIX = "product_";
    public static final String PRODUCT_ID_FIELD = PRODUCT_PREFIX + POSTGRES_PRODUCT_ID_FIELD;
    public static final String PRODUCT_TITLE_FIELD = PRODUCT_PREFIX + POSTGRES_PRODUCT_TITLE_FIELD;
    public static final String PRODUCT_URL_FIELD = PRODUCT_PREFIX + POSTGRES_PRODUCT_URL_FIELD;
    public static final String PRODUCT_ITEMS_LEFT_FIELD = PRODUCT_PREFIX + POSTGRES_PRODUCT_ITEMS_LEFT_FIELD;

    private static final String PURCHASE_PREFIX = "purchase_";
    public static final String PURCHASE_ITEM_ID = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_ID;
    public static final String PURCHASE_ITEM_ORDER_ID = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_ORDER_ID;
    public static final String PURCHASE_ITEM_CUSTOMER_ID = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_CUSTOMER_ID;
    public static final String PURCHASE_ITEM_PRODUCT_ID = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_PRODUCT_ID;
    public static final String PURCHASE_ITEM_QUANTITY = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_QUANTITY;
    public static final String PURCHASE_ITEM_PRICE = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_PRICE;
    public static final String PURCHASE_ITEM_PURCHASE_TIMESTAMP = PURCHASE_PREFIX + POSTGRES_PURCHASE_ITEM_PURCHASE_TIMESTAMP;
}
