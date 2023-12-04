package com.xiaohongshu.auto.collection.properties;

import com.xiaohongshu.auto.collection.model.DataInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class URLProperties {

    public static CopyOnWriteArrayList<DataInfo> dataInfoList = new CopyOnWriteArrayList<>();


    public static String ATM_URL = "https://atm.devops.xiaohongshu.com/api/task/details/";


    public static final String REDREPLAY_LIST_URL = "https://redreplay.devops.xiaohongshu.com/api/appDetail/replayTaskList?current=1&pageSize=50&appName=";

    public static final String REDREPLAY_URL = "https://redreplay.devops.xiaohongshu.com/api/replayTask/generalReport?replayTaskId=";

    public static final String WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=79920ddc-0091-4a8b-93c9-4048e2a1be5c";
}
