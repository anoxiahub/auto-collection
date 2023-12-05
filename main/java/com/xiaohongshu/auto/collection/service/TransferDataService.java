package com.xiaohongshu.auto.collection.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiaohongshu.auto.collection.base.CustomException;
import com.xiaohongshu.auto.collection.model.MarkdownType;
import com.xiaohongshu.auto.collection.model.Result;
import com.xiaohongshu.auto.collection.model.pojo.AtmDTO;
import com.xiaohongshu.auto.collection.model.pojo.ReplayCollectionDTO;
import com.xiaohongshu.auto.collection.model.pojo.ReplayDTO;
import com.xiaohongshu.auto.collection.properties.URLProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaohongshu.auto.collection.properties.URLProperties.ATM_URL;
import static com.xiaohongshu.auto.collection.properties.URLProperties.REDREPLAY_LIST_URL;
import static com.xiaohongshu.auto.collection.properties.URLProperties.REDREPLAY_URL;

public class TransferDataService {


    public TransferDataService(){
        this.map.put("_debug_","IAMYOURDADDY");

    }
    private Map<String,String> map = new HashMap();
    public AtmDTO getAtmDetail(Integer key) throws CustomException {
        HttpResponse response = HttpRequest.get(ATM_URL + key).addHeaders(map).execute();
        if(response.getStatus()!=200) throw new CustomException("Atm请求异常");
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        return jsonObject.get("data",AtmDTO.class);
    }

    public Map<Double,Long> getReplay(String value)throws CustomException{
        HttpResponse response = HttpRequest.get(REDREPLAY_LIST_URL+value).addHeaders(map).execute();
        if(response.getStatus()!=200) throw new CustomException("流量回放应用列表请求异常");
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<ReplayCollectionDTO> replayCollectionDTOList = jsonArray.toList(ReplayCollectionDTO.class);
        BigDecimal diffRate = new BigDecimal(0);
        Long total = new Long(0);
        int size = 0;
        for(ReplayCollectionDTO replayCollection:replayCollectionDTOList){
            if(!replayCollection.getStatus().equals("FINISHED")) continue;
            if(replayCollection.getExecutor().equals("YUNXIAO")||replayCollection.equals("YUNXIAO_master")){
                ReplayDTO replayDTO;
                try{
                     replayDTO = this.analyze(replayCollection);
                }catch (CustomException customException){
                    continue;
                }
                diffRate = diffRate.add(new BigDecimal(replayDTO.getDiffRate()));
                total+=replayDTO.getTotal();
                size++;
            }

        }
        BigDecimal passRate = new BigDecimal(1).subtract(diffRate.divide(new BigDecimal(size),BigDecimal.ROUND_HALF_DOWN,4));
        BigDecimal totalAverage = new BigDecimal(total).divide(new BigDecimal(size),BigDecimal.ROUND_HALF_DOWN,0);
        HashMap<Double,Long> hashMap = new HashMap<>();
        hashMap.put(passRate.doubleValue(), totalAverage.longValue());
        return hashMap;

    }


    public boolean pushData(List<Result> resultList){
        List<Result> results = new ArrayList<>();
        StringBuilder builder = new StringBuilder("今日周报推送如下，请相关同事注意。");
        for(int i=0;i<resultList.size();i++){
            builder.append("\n>方向:<font color=\"comment\">"+ resultList.get(i).getDirection()+"</font>" +
                    "      子方向:<font color=\"comment\">"+resultList.get(i).getSubDirection()+"</font>\n" +
                    "ATM通过率:<font color=\"comment\">"+resultList.get(i).getATMPassRate()+"%</font>" +
                    "  ATM场景数:<font color=\"comment\">"+resultList.get(i).getATMCounts()+"</font>"
            );
            if(resultList.get(i).getReplayCounts()>=0&&resultList.get(i).getReplayRate()>=0){
                builder.append(
                        "   流量回放通过率:<font color=\"comment\">"+resultList.get(i).getReplayRate()+"%</font>" +
                                "   流量回放场景数:<font color=\"comment\">"+resultList.get(i).getReplayCounts()+"</font>"
                );
            }
            if(resultList.get(i).isEfficient()){
                builder.append("      卡点有效:<font color=\"comment\">有效</font>");
            }else{
                builder.append("      卡点有效:<font color=\"warning\">无效@"+resultList.get(i).getPrincipal()+"</font>");
                results.add(resultList.get(i));
            }
            if((i+1)%10==0||i==resultList.size()-1){
                String jsonStr = JSONUtil.toJsonStr(new MarkdownType(builder.toString()));
                System.out.println(jsonStr);
                String post = HttpUtil.post(URLProperties.WEBHOOK_URL, jsonStr);
                System.out.println(post);
                builder=new StringBuilder();
            }
        }
        StringBuilder resultBuilder = new StringBuilder("**总结:**\n");
        for(Result result:results){
            resultBuilder.append("\n><font color=\"warning\">【重要提醒】</font>");
            if(result.getATMPassRate()<95&&result.getReplayRate()>0&&result.getReplayRate()<95){
                resultBuilder.append("方向:<font color=\"warning\">"+result.getDirection()+"</font>,子方向:<font color=\"warning\">"+result.getSubDirection()+"</font>,ATM通过率和流量回放通过率低于阈值,当前：<font color=\"warning\">"+result.getATMPassRate()+"</font>%,<font color=\"warning\">"+result.getReplayRate()+"%</font>,需持续观察<font color=\"warning\">@"+result.getPrincipal()+"</font>");
            }else if(result.getATMPassRate()<95){
                resultBuilder.append("方向:<font color=\"warning\">"+result.getDirection()+"</font>,子方向:<font color=\"warning\">"+result.getSubDirection()+"</font>,ATM通过率低于阈值,当前："+result.getATMPassRate()+"%需持续观察<font color=\"warning\">@"+result.getPrincipal()+"</font>");
            }else{
                resultBuilder.append("方向:<font color=\"warning\">"+result.getDirection()+"</font>,子方向:<font color=\"warning\">"+result.getSubDirection()+"</font>,流量回放通过率低于阈值,当前："+result.getATMPassRate()+"%需持续观察<font color=\"warning\">@"+result.getPrincipal()+"</font>");
            }
        }
        String jsonStr = JSONUtil.toJsonStr(new MarkdownType(resultBuilder.toString()));
        System.out.println(jsonStr);
        String post = HttpUtil.post(URLProperties.WEBHOOK_URL, jsonStr);
        System.out.println(post);
        return true;
    }


    private ReplayDTO analyze(ReplayCollectionDTO replayCollectionDTO) throws CustomException{
        HttpResponse response = HttpRequest.get(REDREPLAY_URL+replayCollectionDTO.getReplayTaskId()).execute();
        if(response.getStatus()!=200) throw new CustomException("回放报告请求异常");
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        return jsonObject.get("data",ReplayDTO.class);

    }
}
