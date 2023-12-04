package com.xiaohongshu.auto.collection.model.pojo;

import lombok.Data;

@Data
public class AtmDTO {
    private Integer taskId;
    private Double passRate;
    private Long excuteCounts;
    private Double avgRuntime;
    private Integer allCase;
    private Integer cronType;
    private Integer patrolType;
    private Integer triggerType;
    private Integer manualType;
    private Integer debugType;
}
