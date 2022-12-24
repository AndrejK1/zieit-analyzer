package zieit.kononenko.analyzer.api.integration.sync.postgres;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import zieit.kononenko.analyzer.api.integration.entity.Customer;
import zieit.kononenko.analyzer.api.integration.entity.Product;
import zieit.kononenko.analyzer.api.integration.entity.PurchaseItem;
import zieit.kononenko.analyzer.api.integration.sync.AbstractConnection;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PostgresConnection implements AbstractConnection {
    private static final String CUSTOMERS_FILE = "static/customers.txt";
    private static final String PRODUCTS_FILE = "static/products.txt";
    private static final Long PURCHASES_COUNT = 500L;
    private static final Long MAX_ITEMS = 5L;
    private static final Long MAX_QTY = 3L;
    private static final LocalDateTime LOW_BOUND = LocalDateTime.now().minusYears(2);
    private static final LocalDateTime UPPER_BOUND = LocalDateTime.now();

    private final List<Customer> customers = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<PurchaseItem> purchases = new ArrayList<>();

    public PostgresConnection() {
        fillMockData();
    }

    private void fillMockData() {
        customers.addAll(loadCustomersFromFile());
        products.addAll(loadProductsFromFile());

        // todo orders generation
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

    @SneakyThrows
    private List<Customer> loadCustomersFromFile() {
        List<Customer> customerList = IOUtils.readLines(PostgresConnection.class.getClassLoader().getResourceAsStream(CUSTOMERS_FILE), StandardCharsets.UTF_8)
                .stream()
                .map(line -> {
                    String[] values = line.split(",");
                    return new Customer(null, values[0], values[1], values[2]);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < customerList.size(); i++) {
            customerList.get(i).setId(String.valueOf(i + System.currentTimeMillis()));
        }

        return customerList;
    }

    @SneakyThrows
    private List<Product> loadProductsFromFile() {
        List<Product> productList = IOUtils.readLines(PostgresConnection.class.getClassLoader().getResourceAsStream(PRODUCTS_FILE), StandardCharsets.UTF_8)
                .stream()
                .map(line -> {
                    String[] values = line.split(",");
                    return new Product(null, values[0], values[1]);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setId(String.valueOf(i + System.currentTimeMillis()));
        }

        return productList;
    }
}
