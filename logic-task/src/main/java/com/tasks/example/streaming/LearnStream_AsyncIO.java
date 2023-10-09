package com.tasks.example.streaming;

import cn.train.base.IBaseRun;
import cn.train.base.env.BaseStreamApp;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LearnStream_AsyncIO extends BaseStreamApp implements IBaseRun {
    @Override
    public void doMain() throws Exception {
//添加 kafka source
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("10.10.5.150:9092")
                .setTopics("topic")
                .setGroupId("flink_ne_cloud_DimensionExpansionJob")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //添加 kafka sink
        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers("10.10.5.150:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("topic-DimensionExpansion")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()).build();

        // 注册 水印 1-5秒区间  source阶段
        DataStreamSource<String> kafkaSource = env.fromSource(source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(1).withSeconds(5)), "Kafka Source");

        // 逻辑处理 tf阶段
        SingleOutputStreamOperator<JSONObject> mapStream = kafkaSource.map((MapFunction<String, JSONObject>) JSONObject::parseObject);
        SingleOutputStreamOperator<JSONObject> asyncStream = AsyncDataStream.orderedWait(mapStream, new MyRichAsyncFunction(), 5, TimeUnit.SECONDS);
        SingleOutputStreamOperator<String> stringStream = asyncStream.map((MapFunction<JSONObject, String>) JSONObject::toString);

        // 输出 sink
        stringStream.sinkTo(sink);
        // 运行
        env.execute();
    }

    static class MyRichAsyncFunction extends RichAsyncFunction<JSONObject, JSONObject> {
        // MySQL数据库连接
        static final String URL = "jdbc:mysql://10.10.62.21:3306/ne_cloud?characterEncoding=UTF-8";
        // 数据库用户名
        static final String USERNAME = "root";
        // 数据库密码
        static final String PASSWORD = "admin@hckj";
        // 驱动
        static final String DRIVER = "com.mysql.jdbc.Driver";
        //数据库连接
        private Connection conn;
        // 创建线程池
        private ThreadPoolExecutor threadPoolExecutor;

        @Override
        public void open(Configuration parameters) throws Exception {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            //初始化线程池
            threadPoolExecutor = new ThreadPoolExecutor(
                    10,
                    10,
                    0,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(1000));
        }

        /**
         * 每来一条数据，就执行一次该方法
         *
         * @param dataSet_param 输入数据
         * @param resultFuture  通过该对象收集数据
         * @throws Exception
         */
        @Override
        public void asyncInvoke(JSONObject dataSet_param, ResultFuture<JSONObject> resultFuture) throws Exception {
            threadPoolExecutor.submit(() -> {
                try {
                    JSONObject dataSet = query(dataSet_param); // 查询数据库
                    // 通过下面方法收集数据
                    resultFuture.complete(Collections.singleton(dataSet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 查询MySQL数据库，并将数据放入到 Map 中
        public JSONObject query(JSONObject dataSer_param) throws Exception {
            System.out.print("接收到数据：" + dataSer_param + " ");
            // 模拟查询延迟，单位：秒
            int delay = new Random().nextInt(20);
            System.out.printf("模拟查询延迟：%s 秒%n", delay);
            Thread.sleep(delay * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            System.out.println(sdf.format(new Date()) + "  查询数据库");
            String sql = " SELECT tmp_aggr.aggr_station_id,\n" +
                    "       tmp_aggr.aggr_station_code,\n" +
                    "       tmp_aggr.aggr_station_name,\n" +
                    "       tmp_station.station_id,\n" +
                    "       tmp_station.station_code,\n" +
                    "       tmp_station.station_name,\n" +
                    "       tmp_station.station_abbr,\n" +
                    "       tmp_station_attr.attr_val AS sta_capacity,\n" +
                    "       tmp_type.type_id,\n" +
                    "       tmp_type.type_code,\n" +
                    "       tmp_type.type_name,\n" +
                    "       tmp_logic.logic_equ_id,\n" +
                    "       tmp_logic.logic_equ_code,\n" +
                    "       tmp_logic.logic_equ_name,\n" +
                    "       tmp_type.inter_equ,\n" +
                    "       tmp_param.param_id,\n" +
                    "       tmp_param.param_code,\n" +
                    "       tmp_param.param_type,\n" +
                    "       tmp_param.param_name,\n" +
                    "       tmp_param.param_claz,\n" +
                    "       tmp_param.alm_claz,\n" +
                    "       tmp_param.alm_level,\n" +
                    "       tmp_param.fault_monitor,\n" +
                    "       tmp_param.main_advise,\n" +
                    "       tmp_param.no_alm,\n" +
                    "       tmp_station.tenant_id\n" +
                    "FROM equ_logic_equ AS tmp_logic\n" +
                    "         INNER JOIN equ_le_param AS tmp_param on tmp_logic.logic_equ_id = tmp_param.logic_equ_id\n" +
                    "         LEFT JOIN equ_station AS tmp_station on tmp_logic.station_id = tmp_station.station_id\n" +
                    "         LEFT JOIN equ_type AS tmp_type on tmp_logic.type_id = tmp_type.type_id\n" +
                    "         LEFT JOIN equ_station_attr AS tmp_station_attr on tmp_station.station_id = tmp_station_attr.station_id\n" +
                    "    AND tmp_station_attr.attr_code = 'StaCapacity'\n" +
                    "    AND tmp_station_attr.recovery = FALSE\n" +
                    "         LEFT JOIN equ_aggr_station_relate AS tmp_relate\n" +
                    "                   on tmp_station.station_id = tmp_relate.station_id\n" +
                    "                       AND tmp_relate.recovery = FALSE\n" +
                    "         LEFT JOIN equ_aggr_station AS tmp_aggr\n" +
                    "                   on tmp_relate.aggr_station_id = tmp_aggr.aggr_station_id\n" +
                    "                       AND tmp_aggr.recovery = FALSE\n" +
                    "WHERE tmp_logic.recovery = FALSE\n" +
                    "  AND tmp_type.recovery = FALSE\n" +
                    "  AND tmp_station.recovery = FALSE\n" +
                    "  AND tmp_param.recovery = FALSE\n" +
                    "  AND cast(? AS BIGINT) = tmp_station.station_id\n" +
                    "  AND ? = tmp_logic.cabinet_no\n" +
                    "  AND ? = tmp_logic.emu_sn ";
            PreparedStatement statement = null;
            ResultSet rs = null;
            String name = null;
            try {
                statement = conn.prepareStatement(sql);
                statement.setString(1, dataSer_param.getString("station"));
                statement.setString(2, dataSer_param.getString("cabinet"));
                statement.setString(3, dataSer_param.getString("emu_sn"));
                rs = statement.executeQuery();
                //全量更新维度数据到内存
                if (rs.next()) {
                    dataSer_param.fluentPut("aggr_station_id", rs.getString(1));
                    dataSer_param.fluentPut("aggr_station_code", rs.getString(2));
                    dataSer_param.fluentPut("aggr_station_name", rs.getString(3));
                    dataSer_param.fluentPut("station_id", rs.getString(1));
                    dataSer_param.fluentPut("station_code", rs.getString(1));
                    dataSer_param.fluentPut("station_name", rs.getString(1));
                    dataSer_param.fluentPut("station_abbr", rs.getString(1));
                    dataSer_param.fluentPut("sta_capacity", rs.getString(1));
                    dataSer_param.fluentPut("type_id", rs.getString(1));
                    dataSer_param.fluentPut("type_code", rs.getString(1));
                    dataSer_param.fluentPut("type_name", rs.getString(1));
                    dataSer_param.fluentPut("logic_equ_id", rs.getString(1));
                    dataSer_param.fluentPut("logic_equ_code", rs.getString(1));
                    dataSer_param.fluentPut("logic_equ_name", rs.getString(1));
                    dataSer_param.fluentPut("inter_equ", rs.getString(1));
                    dataSer_param.fluentPut("param_id", rs.getString(1));
                    dataSer_param.fluentPut("param_code", rs.getString(1));
                    dataSer_param.fluentPut("param_type", rs.getString(1));
                    dataSer_param.fluentPut("param_name", rs.getString(1));
                    dataSer_param.fluentPut("param_claz", rs.getString(1));
                    dataSer_param.fluentPut("alm_claz", rs.getString(1));
                    dataSer_param.fluentPut("alm_level", rs.getString(1));
                    dataSer_param.fluentPut("fault_monitor", rs.getString(1));
                    dataSer_param.fluentPut("main_advise", rs.getString(1));
                    dataSer_param.fluentPut("no_alm", rs.getString(1));
                    dataSer_param.fluentPut("tenant_id", rs.getString(1));
                    System.out.printf("  查询结果：%s", dataSer_param);
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            return dataSer_param;
        }

        /**
         * 如果执行超时，就执行该方法
         *
         * @param dataSet_param element coming from an upstream task
         * @param resultFuture  to be completed with the result data
         * @throws Exception
         */
        @Override
        public void timeout(JSONObject dataSet_param, ResultFuture<JSONObject> resultFuture) throws Exception {
            resultFuture.complete(Collections.singleton(dataSet_param));
        }

        @Override
        public void close() throws Exception {
            // 关闭数据库连接
            if (conn != null) {
                conn.close();
            }
            // 终止定时任务
            if (threadPoolExecutor != null) {
                threadPoolExecutor.shutdown();
            }
        }
    }
}