package zieit.kononenko.analyzer.analytics.task.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalyticsReport {
    private Long customerCount;
    private Long activeCustomerCount;
    private Long uniqueProductCount;
    private Long uniqueBoughtProductCount;
    private Long boughtProductItemCount;
    private Long purchasesCount;
    private Double totalValue;
    private Double averagePurchaseValue;
    private Double averagePurchaseItemsCount;
    private Long averagePurchaseTimeGapSeconds;
    private List<Customer> top5CustomersByValue;
    private List<Product> top5ProductsByGeneratedValue;
    private List<Product> top5ProductsByItemCount;
    private List<ChartPoint> revenueValueChartByDay;

    public Double getActiveCustomersPercentage() {
        return 100 * activeCustomerCount.doubleValue() / customerCount;
    }
}

