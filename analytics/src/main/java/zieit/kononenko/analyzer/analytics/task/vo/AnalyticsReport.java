package zieit.kononenko.analyzer.analytics.task.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticsReport {
    private Long customerCount;
    private Long activeCustomerCount;
    private Long uniqueProductCount;
    private Long uniqueBoughtProductCount;
    private Long boughtProductItemCount;
    private Long purchasesCount;
    private Long uniquePurchasesInSpecifiedPeriod;
    private Double totalValue;
    private Double averagePurchaseValue;
    private Double averagePurchaseUniqueItemsCount;
    private List<Customer> customersWithStats;
    private List<Product> productsWithStats;
    private List<ChartPoint> revenueValueChartByDay;

    public Double getActiveCustomersPercentage() {
        return 100 * activeCustomerCount.doubleValue() / customerCount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Customer {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private LocalDateTime firstPurchaseTimestamp;
        private LocalDateTime lastPurchaseTimestamp;
        private Long purchasesCount;
        private Double totalValue;
        private String recencySegment;
        private String monetarySegment;
        private String frequencySegment;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Product {
        private String id;
        private String title;
        private String shopUrl;
        private String segment;
        private BigDecimal totalRevenue;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ChartPoint {
        private LocalDate date;
        private Double value;
        private Boolean forecast;
    }
}

