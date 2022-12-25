package zieit.kononenko.analyzer.api.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(schema = "shop_xxx", name = "customer")
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
