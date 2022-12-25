package zieit.kononenko.analyzer.api.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(schema = "shop_xxx", name = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    private String title;
    @Column(name = "url")
    private String shopUrl;

    @Transient
    private Double price;
    @Transient
    private Integer priority;
}
