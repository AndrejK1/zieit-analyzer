package zieit.kononenko.analyzer.api.integration.config;

import org.hibernate.EmptyInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class HibernateInterceptor extends EmptyInterceptor {
    private static final String SHOP_PREFIX = "shop_";
    private static final Pattern SHOP_SCHEMA_PATTERN = Pattern.compile("shop_xxx");

    @Override
    public String onPrepareStatement(String sql) {
        String preparedStatement = super.onPrepareStatement(sql);

        String schema = Optional.ofNullable(ShopIdConfig.getShopId())
                .map(shopId -> SHOP_PREFIX + shopId)
                .orElse(SHOP_PREFIX + "xxx");

        return SHOP_SCHEMA_PATTERN.matcher(preparedStatement).replaceAll(schema);
    }
}
