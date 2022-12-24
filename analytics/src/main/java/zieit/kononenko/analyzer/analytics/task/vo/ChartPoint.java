package zieit.kononenko.analyzer.analytics.task.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartPoint {
    private LocalDate date;
    private Double value;
    private Boolean forecast;
}
