package zieit.kononenko.analyzer.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import zieit.kononenko.analyzer.analytics.entity.AnalyticsReportEntity;

@Repository
public interface AnalyticsReportRepository extends CrudRepository<AnalyticsReportEntity, Long> {
}
