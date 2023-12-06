package com.xiaohongshu.auto.collection.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplayCollectionQuery {
    private String configId;
    private String appName;
    private String configName;
    private String configType;

    private String executor;

    private Integer replayTaskId;

    private String status;

    private LocalDateTime updateTime;




}
