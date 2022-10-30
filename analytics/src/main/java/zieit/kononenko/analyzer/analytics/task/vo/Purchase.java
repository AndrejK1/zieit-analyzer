package zieit.kononenko.analyzer.analytics.task.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Purchase {
    private String orderItemId;
    private String orderId;
    private String customerId;
    private String productId;
    private Long quantity;
    private Double itemPrice;
    private LocalDate purchasedTimestamp;
}
