package zieit.kononenko.analyzer.api.integration.sync.postgres;

import org.springframework.stereotype.Component;
import zieit.kononenko.analyzer.api.integration.service.CustomerService;
import zieit.kononenko.analyzer.api.integration.service.ProductService;
import zieit.kononenko.analyzer.api.integration.service.PurchaseItemService;
import zieit.kononenko.analyzer.api.integration.sync.DataSync;

@Component
public class PostgresDataSync extends DataSync<PostgresConnectionConfiguration, PostgresConnection> {
    public PostgresDataSync(CustomerService customerService, ProductService productService, PurchaseItemService purchaseService) {
        super(customerService, productService, purchaseService);
    }

    @Override
    protected PostgresConnection createConnection(PostgresConnectionConfiguration connectionConfiguration) {
        return new PostgresConnection();
    }
}
