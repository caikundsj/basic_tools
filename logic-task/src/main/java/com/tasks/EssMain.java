package com.tasks;


import cn.comm.utils.ConfigUtils;
import com.tasks.Job.BaseBusinessJob;


public class EssMain {


    public static void main(String[] args) throws Exception {
        try {
            /*
                加载配置文件中的配置项
             */
            ConfigUtils.initLoadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new BaseBusinessJob().doMain();
    }
}