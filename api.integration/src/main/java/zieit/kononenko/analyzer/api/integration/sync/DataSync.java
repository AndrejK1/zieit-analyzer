package zieit.kononenko.analyzer.api.integration.sync;

import lombok.RequiredArgsConstructor;
import zieit.kononenko.analyzer.api.integration.service.CustomerService;
import zieit.kononenko.analyzer.api.integration.service.ProductService;
import zieit.kononenko.analyzer.api.integration.service.PurchaseItemService;

@RequiredArgsConstructor
public abstract class DataSync<C extends ConnectionConfiguration, S extends AbstractConnection> {
    private final CustomerService customerService;
    private final ProductService productService;
    private final PurchaseItemService purchaseService;

    public void syncExternalData(C connectionConfiguration) {
        try (S connection = createConnection(connectionConfiguration)) {
            if (!connection.isAvailable()) {
                throw new IllegalStateException("Can't connect to external data source");
            }

            connection.syncCustomerData(customerService::saveData);
            connection.syncProductData(productService::saveData);
            connection.syncPurchaseData(purchaseService::saveData);
        } catch (Exception e) {
            throw new IllegalStateException("Data sync error!", e);
        }
    }

    protected abstract S createConnection(C connectionConfiguration);
}
