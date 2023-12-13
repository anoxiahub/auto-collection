package com.xiaohongshu.auto.collection.service;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiaohongshu.auto.collection.model.CollectionResultBO;
import com.xiaohongshu.auto.collection.model.MarkdownType;
import com.xiaohongshu.auto.collection.model.pojo.AtmQuery;
import com.xiaohongshu.auto.collection.model.pojo.ReplayCollectionQuery;
import com.xiaohongshu.auto.collection.model.pojo.ReplayQuery;
import com.xiaohongshu.auto.collection.properties.EnumURLProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaohongshu.auto.collection.properties.EnumURLProperties.REDREPLAY_URL;


public class TransferDataManager {


    public TransferDataManager(){
        this.map.put("_debug_","IAMYOURDADDY");

    }
    private Map<String,String> map = new HashMap();
    public AtmQuery getAtmDetail(Integer key) {
        HttpResponse response = HttpRequest.get(EnumURLProperties.ATM_URL.getUrl() + key).addHeaders(map).execute();
        if(response.getStatus()!=200) return new AtmQuery(-1.0,-1L);
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        return jsonObject.get("data", AtmQuery.class);
    }

    public Map<Double,Long> getReplay(String value){
        HttpResponse response = HttpRequest.get(EnumURLProperties.REDREPLAY_LIST_URL.getUrl()+value).addHeaders(map).execute();
        if(response.getStatus()!=200){
            HashMap<Double,Long> map1 = new HashMap<>();
            map1.put(-1.0,-1L);
            return map1;
        };
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<ReplayCollectionQuery> replayCollectionQueryList = jsonArray.toList(ReplayCollectionQuery.class);
        BigDecimal diffRate = new BigDecimal(0);
        Long total = new Long(0);
        int size = 0;
        for(ReplayCollectionQuery replayCollection: replayCollectionQueryList){
            if(!replayCollection.getStatus().equals("FINISHED")) continue;
            if(replayCollection.getExecutor().equals("YUNXIAO")||replayCollection.equals("YUNXIAO_master")){
                ReplayQuery replayQuery;
                replayQuery = this.analyze(replayCollection);
                if(replayQuery==null) continue;
                diffRate = diffRate.add(new BigDecimal(replayQuery.getDiffRate()));
                total+= replayQuery.getTotal();
                size++;
            }

        }
        BigDecimal passRate = new BigDecimal(1).subtract(diffRate.divide(new BigDecimal(size),BigDecimal.ROUND_HALF_DOWN,4));
        BigDecimal totalAverage = new BigDecimal(total).divide(new BigDecimal(size),BigDecimal.ROUND_HALF_DOWN,0);
        HashMap<Double,Long> hashMap = new HashMap<>();
        hashMap.put(passRate.doubleValue(), totalAverage.longValue());
        return hashMap;

    }


    public boolean pushData(List<CollectionResultBO> collectionResultBOList){
        List<CollectionResultBO> collectionResultBOS = new ArrayList<>();
        StringBuilder builder = new StringBuilder("今日周报推送如下，请相关同事注意。");
        for(int i = 0; i< collectionResultBOList.size(); i++){
            builder.append("\n>方向:<font color=\"comment\">"+ collectionResultBOList.get(i).getDirection()+"</font>" +
                    "      子方向:<font color=\"comment\">"+ collectionResultBOList.get(i).getSubDirection()+"</font>\n" +
                    "ATM通过率:<font color=\"comment\">"+ collectionResultBOList.get(i).getATMPassRate()+"%</font>" +
                    "  ATM场景数:<font color=\"comment\">"+ collectionResultBOList.get(i).getATMCounts()+"</font>"
            );
            if(collectionResultBOList.get(i).getReplayCounts()>=0&& collectionResultBOList.get(i).getReplayRate()>=0){
                builder.append(
                        "   流量回放通过率:<font color=\"comment\">"+ collectionResultBOList.get(i).getReplayRate()+"%</font>" +
                                "   流量回放场景数:<font color=\"comment\">"+ collectionResultBOList.get(i).getReplayCounts()+"</font>"
                );
            }
            if(collectionResultBOList.get(i).isEfficient()){
                builder.append("      卡点有效:<font color=\"comment\">有效</font>");
            }else{
                builder.append("      卡点有效:<font color=\"warning\">无效@"+ collectionResultBOList.get(i).getPrincipal()+"</font>");
                collectionResultBOS.add(collectionResultBOList.get(i));
            }
            if((i+1)%10==0||i== collectionResultBOList.size()-1){
                String jsonStr = JSONUtil.toJsonStr(new MarkdownType(builder.toString()));
                String post = HttpUtil.post(EnumURLProperties.WEBHOOK_URL.getUrl(), jsonStr);
                builder=new StringBuilder();
            }
        }
        StringBuilder resultBuilder = new StringBuilder("**总结:**\n");
        for(CollectionResultBO collectionResultBO : collectionResultBOS){
            resultBuilder.append("\n><font color=\"warning\">【重要提醒】</font>");
            if(collectionResultBO.getATMPassRate()>=0&&collectionResultBO.getATMPassRate()<95&& collectionResultBO.getReplayRate()>=0&& collectionResultBO.getReplayRate()<95){
                resultBuilder.append("方向:<font color=\"warning\">"+ collectionResultBO.getDirection()+"</font>,子方向:<font color=\"warning\">"+ collectionResultBO.getSubDirection()+"</font>,ATM通过率和流量回放通过率低于阈值,当前："+ collectionResultBO.getATMPassRate()+"%,<font color=\"warning\">"+ collectionResultBO.getReplayRate()+"%</font>,请及时维护<font color=\"warning\">@"+ collectionResultBO.getPrincipal()+"</font>");
            }else if(collectionResultBO.getATMPassRate()<95&&collectionResultBO.getATMPassRate()>=0){
                resultBuilder.append("方向:<font color=\"warning\">"+ collectionResultBO.getDirection()+"</font>,子方向:<font color=\"warning\">"+ collectionResultBO.getSubDirection()+"</font>,ATM通过率低于阈值,当前："+ collectionResultBO.getATMPassRate()+"%,请及时维护<font color=\"warning\">@"+ collectionResultBO.getPrincipal()+"</font>");
            }else{
                resultBuilder.append("方向:<font color=\"warning\">"+ collectionResultBO.getDirection()+"</font>,子方向:<font color=\"warning\">"+ collectionResultBO.getSubDirection()+"</font>,流量回放通过率低于阈值,当前："+ collectionResultBO.getReplayRate()+"%,请及时维护<font color=\"warning\">@"+ collectionResultBO.getPrincipal()+"</font>");
            }
        }
        String jsonStr = JSONUtil.toJsonStr(new MarkdownType(resultBuilder.toString()));
        System.out.println(jsonStr);
        String post = HttpUtil.post(EnumURLProperties.WEBHOOK_URL.getUrl(), jsonStr);
        System.out.println(post);
        return true;
    }


    private ReplayQuery analyze(ReplayCollectionQuery replayCollectionQuery) {
        HttpResponse response = HttpRequest.get(REDREPLAY_URL.getUrl()+ replayCollectionQuery.getReplayTaskId()).execute();
        if(response.getStatus()!=200) {
           return null;
        }
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        return jsonObject.get("data", ReplayQuery.class);

    }
}
