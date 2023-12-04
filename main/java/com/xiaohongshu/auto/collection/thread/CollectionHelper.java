package com.xiaohongshu.auto.collection.thread;

import com.xiaohongshu.auto.collection.base.CustomException;
import com.xiaohongshu.auto.collection.model.DataInfo;
import com.xiaohongshu.auto.collection.model.Result;
import com.xiaohongshu.auto.collection.model.pojo.AtmDTO;
import com.xiaohongshu.auto.collection.service.TransferDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaohongshu.auto.collection.properties.URLProperties.dataInfoList;

public class CollectionHelper {

    private TransferDataService transferDataService;

    public CollectionHelper(){
        this.transferDataService =new TransferDataService();
    }
    private static CollectionHelper collectionHelper = new CollectionHelper();

    public static CollectionHelper getInstance(){
        return collectionHelper;
    }
    public  long startTime;
    public  long endTime;
    public void start(){
        while(true){
            startTime=System.currentTimeMillis();
            List<Result> resultList = new ArrayList<>();
            for(DataInfo dataInfo:dataInfoList){
                    try {
                        AtmDTO atmDTO = transferDataService.getAtmDetail(dataInfo.getTaskId());
                        Map<Double,Long> resultMap;
                        if(dataInfo.getAppName()==null){
                            resultMap = new HashMap<>();
                            resultMap.put(-1.0,-1L);
                        }else{
                            resultMap = transferDataService.getReplay(dataInfo.getAppName());
                        }
                        Result result = this.builderResult(dataInfo,atmDTO,resultMap.entrySet().stream().findFirst().get());
                        resultList.add(result);
                    }catch (CustomException customException){
                        System.out.println(customException.getMsg());
                        customException.printStackTrace();
                    }

            }
            transferDataService.pushData(resultList);
            endTime = System.currentTimeMillis();
            System.out.println(endTime-startTime);
            try{
                //todo 周期运行时间
                Thread.sleep(1000*3600*24*7);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    private Result builderResult(DataInfo dataInfo,AtmDTO atmDTO, Map.Entry<Double,Long> map){
        Result result = new Result();
        result.setATMPassRate(atmDTO.getPassRate());
        result.setATMCounts(atmDTO.getExcuteCounts());
        result.setReplayRate(map.getKey());
        result.setReplayCounts(map.getValue());
        result.setDirection(dataInfo.getDirection());
        result.setSubDirection(dataInfo.getSubDirection());
        result.setPrincipal(dataInfo.getPrincipal());
        return result;
    }

}
