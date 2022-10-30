package zieit.kononenko.analyzer.api.integration.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import zieit.kononenko.analyzer.api.integration.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {
}
