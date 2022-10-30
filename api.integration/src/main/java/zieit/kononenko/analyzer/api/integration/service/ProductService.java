package zieit.kononenko.analyzer.api.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zieit.kononenko.analyzer.api.integration.entity.Customer;
import zieit.kononenko.analyzer.api.integration.entity.Product;
import zieit.kononenko.analyzer.api.integration.repository.CustomerRepository;
import zieit.kononenko.analyzer.api.integration.repository.ProductRepository;

import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService implements DataService<Product> {
    private final ProductRepository repository;

    @Override
    public Product saveOne(Product data) {
        return repository.save(data);
    }

    @Override
    public List<Product> saveData(Collection<Product> data) {
        return (List<Product>) repository.saveAll(data);
    }
}
