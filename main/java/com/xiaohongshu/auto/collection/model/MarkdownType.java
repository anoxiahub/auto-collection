package com.xiaohongshu.auto.collection.model;


import cn.hutool.json.JSONUtil;
import com.xiaohongshu.auto.collection.model.pojo.AtmDTO;
import com.xiaohongshu.auto.collection.model.pojo.ReplayCollectionDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MarkdownType {

    private String msgtype;

    private Map<String,String> markdown=new HashMap<>();

    public MarkdownType(){}

    public MarkdownType(String markdown){
        this.markdown.put("content",markdown);
        this.msgtype="markdown";
    }


}
