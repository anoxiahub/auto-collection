package com.xiaohongshu.auto.collection.model;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Result {
    private String direction;
    private String subDirection;
    private Double ATMPassRate;
    private Long ATMCounts;
    private Double replayRate;
    private Long replayCounts;
    private boolean efficient;
    private String principal;

    public void setATMPassRate(Double ATMPassRate) {
        this.ATMPassRate = new BigDecimal(ATMPassRate).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }


    public void setReplayRate(Double replayRate) {
        this.replayRate = new BigDecimal(replayRate).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public boolean isEfficient() {
        return this.ATMPassRate>95&&(this.replayRate>95||this.replayRate<0);
    }
}
