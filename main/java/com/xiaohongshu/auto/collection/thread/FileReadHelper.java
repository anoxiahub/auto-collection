package com.xiaohongshu.auto.collection.thread;

import com.xiaohongshu.auto.collection.model.DataInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

import static com.xiaohongshu.auto.collection.properties.URLProperties.dataInfoList;

public class FileReadHelper {

    private static final String file = "application.yml";
    private static FileReadHelper fileReadHelper = new FileReadHelper();

    public static FileReadHelper getInstance(){
        return fileReadHelper;
    }

    public void start(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try (InputStream content = this.getClass().getClassLoader().getResourceAsStream(file)) {
                        Yaml yaml = new Yaml(new Constructor(DataInfo.class));
                        Iterable<Object> its = yaml.loadAll(content);
                        dataInfoList.clear();
                        for (Object it : its) {
                            dataInfoList.add((DataInfo) it);
                        }
                        Thread.sleep(1000*3600*24);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                }

            }
        });
        thread.start();
    }

}
