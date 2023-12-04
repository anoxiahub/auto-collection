package com.xiaohongshu.auto.collection;


import com.xiaohongshu.auto.collection.thread.CollectionHelper;
import com.xiaohongshu.auto.collection.thread.FileReadHelper;


public class ApplicationStart {

    public static void main(String[] args) {
        FileReadHelper.getInstance().start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CollectionHelper.getInstance().start();

    }
}
