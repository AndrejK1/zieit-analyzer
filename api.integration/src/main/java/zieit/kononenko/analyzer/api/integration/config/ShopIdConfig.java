package zieit.kononenko.analyzer.api.integration.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopIdConfig {
    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    public static Long getShopId() {
        return CONTEXT.get();
    }

    public static void setTenantId(Long shopId) {
        CONTEXT.set(shopId);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
