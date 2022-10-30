package zieit.kononenko.analyzer.api.integration.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import zieit.kononenko.analyzer.api.integration.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
}
