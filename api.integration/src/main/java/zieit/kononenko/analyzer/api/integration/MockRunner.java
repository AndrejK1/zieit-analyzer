package zieit.kononenko.analyzer.api.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import zieit.kononenko.analyzer.api.integration.config.ShopIdConfig;
import zieit.kononenko.analyzer.api.integration.sync.postgres.PostgresConnectionConfiguration;
import zieit.kononenko.analyzer.api.integration.sync.postgres.PostgresDataSync;

@Component
@RequiredArgsConstructor
public class MockRunner {
    private final PostgresDataSync postgresDataSync;

    @EventListener(ApplicationReadyEvent.class)
    public void startMockSync() {
        ShopIdConfig.setTenantId(1L);
        postgresDataSync.syncExternalData(new PostgresConnectionConfiguration());
    }
}
