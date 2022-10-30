package zieit.kononenko.analyzer.analytics.task.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String id;
    private String title;
    private String shopUrl;
    private Long itemsLeft;
}
