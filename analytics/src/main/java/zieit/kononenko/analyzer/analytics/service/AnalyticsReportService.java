package zieit.kononenko.analyzer.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zieit.kononenko.analyzer.analytics.entity.AnalyticsReportEntity;
import zieit.kononenko.analyzer.analytics.repository.AnalyticsReportRepository;

@Service
@RequiredArgsConstructor
public class AnalyticsReportService {
    private final AnalyticsReportRepository repository;

    public AnalyticsReportEntity save(AnalyticsReportEntity report) {
        return repository.save(report);
    }
}
