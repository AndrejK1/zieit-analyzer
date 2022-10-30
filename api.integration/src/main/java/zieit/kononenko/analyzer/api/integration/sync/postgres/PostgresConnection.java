package zieit.kononenko.analyzer.api.integration.sync.postgres;

import zieit.kononenko.analyzer.api.integration.entity.Customer;
import zieit.kononenko.analyzer.api.integration.entity.Product;
import zieit.kononenko.analyzer.api.integration.entity.PurchaseItem;
import zieit.kononenko.analyzer.api.integration.sync.AbstractConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PostgresConnection implements AbstractConnection {
    private final List<Customer> customers = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<PurchaseItem> purchases = new ArrayList<>();

    public PostgresConnection() {
        fillMockData();
    }

    private void fillMockData() {
        // TODO generator
    }

    @Override
    public void syncCustomerData(Consumer<Collection<Customer>> dataPageAction) {
        dataPageAction.accept(customers);
    }

    @Override
    public void syncProductData(Consumer<Collection<Product>> dataPageAction) {
        dataPageAction.accept(products);
    }

    @Override
    public void syncPurchaseData(Consumer<Collection<PurchaseItem>> dataPageAction) {
        dataPageAction.accept(purchases);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void close() throws Exception {
        // nothing to do there
    }
}
