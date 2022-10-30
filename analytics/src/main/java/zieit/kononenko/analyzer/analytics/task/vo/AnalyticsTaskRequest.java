package zieit.kononenko.analyzer.analytics.task.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnalyticsTaskRequest {
    private final Long shopId;
    private final LocalDateTime periodStart;
    private final LocalDateTime periodEnd;
}
