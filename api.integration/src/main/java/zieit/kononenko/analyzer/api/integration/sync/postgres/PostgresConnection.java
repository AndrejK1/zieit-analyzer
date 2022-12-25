package zieit.kononenko.analyzer.api.integration.sync.postgres;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import zieit.kononenko.analyzer.api.integration.entity.Customer;
import zieit.kononenko.analyzer.api.integration.entity.Product;
import zieit.kononenko.analyzer.api.integration.entity.PurchaseItem;
import zieit.kononenko.analyzer.api.integration.sync.AbstractConnection;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PostgresConnection implements AbstractConnection {
    private static final String CUSTOMERS_FILE = "static/customers.txt";
    private static final String PRODUCTS_FILE = "static/products.txt";
    private static final Long PURCHASES_COUNT = 5500L;
    private static final Long MAX_ITEMS = 5L;
    private static final Long MAX_QTY = 3L;
    private static final LocalDateTime LOW_BOUND = LocalDateTime.now().minusYears(2);
    private static final LocalDateTime UPPER_BOUND = LocalDateTime.now();

    private final List<Customer> customers = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<PurchaseItem> purchases = new ArrayList<>();

    private final Random random = new Random();

    public PostgresConnection() {
        fillMockData();
    }

    private void fillMockData() {
        customers.addAll(loadCustomersFromFile());
        products.addAll(loadProductsFromFile());

        Map<String, Object> customersDataForGeneration = customers.stream().collect(HashMap::new, (m, v) -> {
                    int customerRecency = random.nextInt(10);

                    LocalDateTime lastActive;

                    if (customerRecency >= 4) {
                        lastActive = UPPER_BOUND;
                    } else {
                        lastActive = UPPER_BOUND.minusMonths(customerRecency * 4);
                    }

                    LocalDateTime substr = lastActive.minusMonths(2 + random.nextInt(20));
                    LocalDateTime start = substr.isBefore(LOW_BOUND) ? LOW_BOUND : substr;

                    long orders = (long) (Period.between(start.toLocalDate(), lastActive.toLocalDate()).getMonths() * 1.5) + 1L;

                    m.put(v.getId(), new CustomerDataHolder(v, start, lastActive, orders));
                },
                Map::putAll);

        AtomicLong counter = new AtomicLong();
        Map<Long, Product> productsDataForGeneration = products.stream()
                .collect(LinkedHashMap::new,
                        (m, v) -> m.put(counter.addAndGet(v.getPriority()), v),
                        Map::putAll);

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
                .filter(StringUtils::hasText)
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
                .filter(StringUtils::hasText)
                .map(line -> {
                    String[] values = line.split(",");
                    return new Product(null, values[0], values[1], Long.parseLong(values[2]), Long.parseLong(values[3]));
                })
                .collect(Collectors.toList());

        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setId(String.valueOf(i + System.currentTimeMillis()));
        }

        return productList;
    }

    @Data
    @Builder
    @ToString
    private static final class CustomerDataHolder {
        private final Customer customer;
        private final LocalDateTime registered;
        private final LocalDateTime out;
        private final Long orders;
    }
}
