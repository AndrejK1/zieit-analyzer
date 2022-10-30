package zieit.kononenko.analyzer.api.integration.service;

import java.util.Collection;
import java.util.List;

public interface DataService<T> {
    T saveOne(T data);

    List<T> saveData(Collection<T> data);
}
