package zieit.kononenko.analyzer.api.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zieit.kononenko.analyzer.api.integration.entity.PurchaseItem;
import zieit.kononenko.analyzer.api.integration.repository.PurchaseItemRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseItemService implements DataService<PurchaseItem> {
    private final PurchaseItemRepository repository;

    @Override
    public PurchaseItem saveOne(PurchaseItem data) {
        return repository.save(data);
    }

    @Override
    public List<PurchaseItem> saveData(Collection<PurchaseItem> data) {
        return (List<PurchaseItem>) repository.saveAll(data);
    }
}
