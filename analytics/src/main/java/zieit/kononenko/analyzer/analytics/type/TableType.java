package zieit.kononenko.analyzer.analytics.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TableType {
    CUSTOMER("customer"),
    PRODUCT("product"),
    PURCHASE("purchase");

    private final String tableName;
}
