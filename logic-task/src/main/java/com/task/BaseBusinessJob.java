package com.task;

import cn.train.base.IBaseRun;
import cn.train.base.env.BaseTableApp;
import org.apache.flink.table.api.TableResult;

public class BaseBusinessJob extends BaseTableApp implements IBaseRun {
    @Override
    public void doMain() throws Exception {
        tableStreamEnv.executeSql("CREATE TABLE pv_cloud_stat_collect_measuring_map (\n" +
                "   dev            STRING,\n" +
                "   `time`         BIGINT,\n" +
                "   `data`         MAP<String,Double>,\n" +
                "   run_time       AS  CAST(TO_TIMESTAMP(FROM_UNIXTIME(`time`/1000,'yyyy-MM-dd HH:mm:ss')) AS TIMESTAMP(3)),\n" +
                "   WATERMARK      FOR run_time AS run_time - INTERVAL '30' SECOND,\n" +
                "   proc_time      AS  PROCTIME()\n" +
                ") WITH (\n" +
                "   'connector' = 'kafka',\n" +
                "   'topic' = 'pv_cloud_stat_collect_measuring_map',\n" +
                "   'properties.bootstrap.servers' = '10.10.5.150:9092,10.10.5.151:9092,10.10.5.152:9092,',\n" +
                "   'properties.group.id' = 'pv_cloud_rqpeat_test',\n" +
                "   'scan.topic-partition-discovery.interval' = '1h',\n" +
                "   'scan.startup.mode' = 'latest-offset',\n" +
                "   'format' = 'json'\n" +
                ")");
        tableStreamEnv.executeSql("select dev,DATE_FORMAT(run_time,'yyyy-MM-dd HH:mm:ss') as d from pv_cloud_stat_collect_measuring_map;");
    }
}
