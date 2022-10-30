package zieit.kononenko.analyzer.api.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zieit.kononenko.analyzer.api.integration.entity.Customer;
import zieit.kononenko.analyzer.api.integration.repository.CustomerRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService implements DataService<Customer> {
    private final CustomerRepository repository;

    @Override
    public Customer saveOne(Customer data) {
        return repository.save(data);
    }

    @Override
    public List<Customer> saveData(Collection<Customer> data) {
        return (List<Customer>) repository.saveAll(data);
    }
}
