package zieit.kononenko.analyzer.analytics.task;

import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsTaskRequest;

import java.util.function.Consumer;

public interface Task extends Consumer<AnalyticsTaskRequest> {
}
