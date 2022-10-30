package zieit.kononenko.analyzer.api.integration.sync;

import zieit.kononenko.analyzer.api.integration.entity.Customer;
import zieit.kononenko.analyzer.api.integration.entity.Product;
import zieit.kononenko.analyzer.api.integration.entity.PurchaseItem;

import java.util.Collection;
import java.util.function.Consumer;

public interface AbstractConnection extends AutoCloseable {
    void syncCustomerData(Consumer<Collection<Customer>> dataPageAction);

    void syncProductData(Consumer<Collection<Product>> dataPageAction);

    void syncPurchaseData(Consumer<Collection<PurchaseItem>> dataPageAction);

    boolean isAvailable();
}
