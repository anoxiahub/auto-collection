package com.xiaohongshu.auto.collection.properties;


import lombok.Getter;

public enum EnumURLProperties {

//    public static CopyOnWriteArrayList<DataInfoBO> dataInfoBOList = new CopyOnWriteArrayList<>();

    ATM_URL(1,"https://atm.devops.xiaohongshu.com/api/task/details/"),


    REDREPLAY_LIST_URL(2,"https://redreplay.devops.xiaohongshu.com/api/appDetail/replayTaskList?current=1&pageSize=50&appName="),

    REDREPLAY_URL(3,"https://redreplay.devops.xiaohongshu.com/api/replayTask/generalReport?replayTaskId="),

    WEBHOOK_URL(4,"https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=17592f9e-5dd7-4700-a6c2-164a8cc3dfce");
    @Getter
    private String url;

    @Getter
    private final int index;

    EnumURLProperties(int index, String url){
        this.index=index;
        this.url=url;
    }


}
