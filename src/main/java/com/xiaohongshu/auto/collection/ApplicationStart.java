package com.xiaohongshu.auto.collection;


import com.xiaohongshu.auto.collection.thread.AutomationDataCron;

public class ApplicationStart {

    public static void main(String[] args) {
        AutomationDataCron automationDataCron = new AutomationDataCron();
        automationDataCron.FileReadExecute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        automationDataCron.CollectionExecute();

    }
}
