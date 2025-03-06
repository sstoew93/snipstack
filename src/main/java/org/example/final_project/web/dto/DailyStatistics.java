package org.example.final_project.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyStatistics {
    private Integer dailyRegistrationCount;
    private Integer dailyPostCount;
    private Integer dailyCommentCount;
}
