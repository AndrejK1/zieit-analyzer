package zieit.kononenko.analyzer.analytics.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import zieit.kononenko.analyzer.analytics.task.vo.AnalyticsReport;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(schema = "analyzer", name = "analytics_reports")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReportEntity {
    @Id
    private Long id;
    private Long shopId;
    @Type(type = "jsonb")
    private AnalyticsReport report;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
