package com.xiaohongshu.auto.collection.model;


import cn.hutool.json.JSONUtil;
import com.xiaohongshu.auto.collection.model.pojo.AtmDTO;
import com.xiaohongshu.auto.collection.model.pojo.ReplayCollectionDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MarkdownType {

    private String msgtype;

    private Map<String,Object> markdown=new HashMap<>();

    public MarkdownType(){}

    public MarkdownType(String markdown){
        this.markdown.put("content",markdown);
        this.msgtype="markdown";
    }

    public void warnRemind(String principal){
        ArrayList<String> list = new ArrayList();
        list.add(principal);
        markdown.put("mentioned_list",list);
    }

}
