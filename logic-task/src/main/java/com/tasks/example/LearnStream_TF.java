package com.tasks.example;

import cn.comm.utils.ConfigUtils;
import cn.train.base.IBaseRun;
import cn.train.base.env.BaseStreamApp;

public class LearnStream_TF extends BaseStreamApp implements IBaseRun {
    /*
    数据流转换
    MAP:    DataStream → DataStream 获取一个元素并生成一个元素。
    FLATMAP:    DataStream → DataStream 获取一个元素并生成零个、一个或多个元素。
    Filter:    DataStream → DataStream 计算每个元素的布尔函数并保留函数返回 true 的那些。
    KeyBy:    DataStream → KeyedStream 在逻辑上将流划分为不相交的分区。具有相同键的所有记录都分配给相同的分区。在内部，keyBy()是通过散列分区实现的。
    Reduce:    KeyedStream → DataStream 键数据流上的“滚动”减少。将当前元素与最后减少的值组合并发出新值。
    Window:    KeyedStream → WindowedStream 可以在已经分区的 KeyedStreams 上定义 Windows。Windows 根据某些特征（例如，最近 5 秒内到达的数据）对每个键中的数据进行分组。
    WindowAll:  DataStream → AllWindowedStream 可以在常规 DataStreams 上定义窗口。Windows 根据某些特征（例如，最近 5 秒内到达的数据）对所有流事件进行分组。
    Window Apply: WindowedStream → DataStream | AllWindowedStream → DataStream 将通用功能应用于整个窗口。
     */
    @Override
    public void doMain() throws Exception {
        env.readTextFile("./tmp.csv").print();
        env.execute();
    }

    public static void main(String[] args) throws Exception {
        try {
            /*
                加载配置文件中的配置项
             */
            ConfigUtils.initLoadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LearnStream_TF().doMain();
    }
}