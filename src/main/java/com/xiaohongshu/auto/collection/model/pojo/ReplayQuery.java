package com.xiaohongshu.auto.collection.model.pojo;

import lombok.Data;

@Data
public class ReplayQuery {

    private String appName;

    private Integer diffCount;

    private Double diffRate;

    private Integer duration;

    private Integer total;
}
