package com.xiaohongshu.auto.collection.thread;

import com.xiaohongshu.auto.collection.model.CollectionResultBO;
import com.xiaohongshu.auto.collection.model.DataInfoBO;
import com.xiaohongshu.auto.collection.model.pojo.AtmQuery;
import com.xiaohongshu.auto.collection.service.TransferDataManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AutomationDataCron {
    public  CopyOnWriteArrayList<DataInfoBO> dataInfoBOList = new CopyOnWriteArrayList<>();

    private TransferDataManager transferDataManager = new TransferDataManager();




    public void FileReadExecute(){
        String file = "basedata.yml";
        try (InputStream content = this.getClass().getClassLoader().getResourceAsStream(file)) {
            Yaml yaml = new Yaml(new Constructor(DataInfoBO.class));
            Iterable<Object> its = yaml.loadAll(content);
            dataInfoBOList.clear();
            for (Object it : its) {
                dataInfoBOList.add((DataInfoBO) it);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public void CollectionExecute(){
        List<CollectionResultBO> collectionResultBOList = new ArrayList<>();
        for(DataInfoBO dataInfoBO : dataInfoBOList){
                AtmQuery atmQuery = transferDataManager.getAtmDetail(dataInfoBO.getTaskId());
                Map<Double,Long> resultMap;
                if(dataInfoBO.getAppName()==null){
                    resultMap = new HashMap<>();
                    resultMap.put(-1.0,-1L);
                }else{
                    resultMap = transferDataManager.getReplay(dataInfoBO.getAppName());
                }
                CollectionResultBO collectionResultBO = this.builderResult(dataInfoBO, atmQuery,resultMap.entrySet().stream().findFirst().get());
                collectionResultBOList.add(collectionResultBO);
        }
        transferDataManager.pushData(collectionResultBOList);
    }

    private CollectionResultBO builderResult(DataInfoBO dataInfoBO, AtmQuery atmQuery, Map.Entry<Double,Long> map){
        CollectionResultBO collectionResultBO = new CollectionResultBO();
        collectionResultBO.setATMPassRate(atmQuery.getPassRate());
        collectionResultBO.setATMCounts(atmQuery.getExcuteCounts());
        collectionResultBO.setReplayRate(map.getKey());
        collectionResultBO.setReplayCounts(map.getValue());
        collectionResultBO.setDirection(dataInfoBO.getDirection());
        collectionResultBO.setSubDirection(dataInfoBO.getSubDirection());
        collectionResultBO.setPrincipal(dataInfoBO.getPrincipal());
        return collectionResultBO;
    }
}
