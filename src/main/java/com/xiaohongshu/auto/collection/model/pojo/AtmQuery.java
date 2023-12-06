package com.xiaohongshu.auto.collection.model.pojo;

import lombok.Data;

@Data
public class AtmQuery {
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

    public AtmQuery(){}

    public AtmQuery(Double passRate,Long excuteCounts){
        this.passRate=passRate;
        this.excuteCounts = excuteCounts;
    }
}
