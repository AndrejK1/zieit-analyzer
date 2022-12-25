package zieit.kononenko.analyzer.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import zieit.kononenko.analyzer.analytics.task.AnalyticsTask;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsTaskRequest;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AnalyticsJobRunner {
    private final AnalyticsTask analyticsTask;

    @EventListener(ApplicationReadyEvent.class)
    public void runTasks() {
        analyticsTask.accept(new AnalyticsTaskRequest(1L, LocalDateTime.now().minusYears(1L), LocalDateTime.now()));
    }
}
