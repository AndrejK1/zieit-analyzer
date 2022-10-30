package zieit.kononenko.analyzer.analytics.task.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartPoint {
    private LocalDate date;
    private Double value;
}
