package zieit.kononenko.analyzer.api.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(schema = "shop_xxx", name = "purchase_item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {
    @Id
    private String id;
    private String orderId;
    private String customerId;
    private String productId;
    private Long quantity;
    private Double price;
    private LocalDate purchasedTimestamp;
}
