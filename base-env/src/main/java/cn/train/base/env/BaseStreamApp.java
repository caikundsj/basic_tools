package cn.train.base.env;


import cn.comm.configer.JobConfig;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Flink stream env环境配置项
 */

public class BaseStreamApp {
    private final static Logger log = LoggerFactory.getLogger(BaseStreamApp.class);

    protected StreamExecutionEnvironment env = getStreamEnv();

    /**
     * 获取流计算Env环境及运行配置
     */
    private StreamExecutionEnvironment getStreamEnv() {
        try {
            log.info(" ------> 创建 env启动器 ");
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            if (enableSavePoint()) {
                env.setDefaultSavepointDirectory(JobConfig.savePointPath);
                log.info(" ------> 设置默认 保存点");
            }
            // 设置状态(state)相关
            log.info("检查点存储方式");
            switch (setCheckpointStateType()) {
                case "HASHMAP_STATE": //等价于 MEMORY_STATE / HDFS_STATE 追溯地址:https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/ops/state/state_backends/
                    log.info("HASHMAP_STATE");
                    env.setStateBackend(new HashMapStateBackend());
                    break;
                case "EMBEDDED_ROCKS_STATE": //支持增量
                    log.info("EMBEDDED_ROCKS_STATE");
                    env.setStateBackend(new EmbeddedRocksDBStateBackend());
                    break;
                default:
                    // 默认不设置
            }
            // 开启checkpoint
            if (enableCheckPoint()) {
                log.info(" ------> 设置 开启检查点");
                env.enableCheckpointing(setCheckpointInterval(), setCheckPointingMode());//恰一次处理语义 和 Checkpoint时间间隔配置项
                env.getCheckpointConfig().setCheckpointTimeout(setCheckpointTimeout());// CheckPoint超时时间
                env.getCheckpointConfig().setMinPauseBetweenCheckpoints(setMinPauseBetweenCheckpoints());// 两次CheckPoint中间最小时间间隔 （是指整个任务的全部checkpoint完成）
                env.getCheckpointConfig().setMaxConcurrentCheckpoints(setMaxConcurrentCheckpoints());// 同时允许多少个Checkpoint在做快照（是指整个任务的全部checkpoint完成）
                env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);// 容忍多少次checkpoint失败,认为是Job失败重启应用
                // env.getCheckpointConfig().enableUnalignedCheckpoints();// 启用未对齐的检查点：大大减少背压下的检查点时间。仅适用于恰好一次检查点和一个并发检查点。影响：增加额外的状态存储I/O消耗
                env.getCheckpointConfig().setExternalizedCheckpointCleanup(setCheckpointClearStrategy());// checkpoint的清除策略
                env.getCheckpointConfig().setCheckpointStorage(JobConfig.checkPointPath);// 设置检查点存储位置(外部存储系统：HDFS优先)
            }

            /*
             * 重启策略，在遇到不可预知的问题时。让Job从上一次完整的Checkpoint处恢复状态，保证Job和挂之前的状态保持一致
             * FixedDelayRestartStrategy 固定延时重启策略
             */
            env.setRestartStrategy(setRestartStrategy());

            // 选择设置事件事件和处理事件
            // env.setStreamTimeCharacteristic(setTimeCharacter());
            // 默认不适用水印
            setEnableWaterMarker(env);
            // 设置程序并行度
            env.setParallelism(setDefaultParallelism());

            log.info("StreamExecutionEnvironment 环境初始化完成");
            return env;
        } catch (Exception e) {
            log.error("起步就报错,你是属猪么?");
            throw new RuntimeException("初始化 StreamExecutionEnvironment 环境配置错误！", e);
        }

    }

    /**
     * 是否开启savepoint
     */
    protected Boolean enableSavePoint() {
        return JobConfig.enableSavePoint;
    }

    /**
     * 是否开启checkpoint
     */
    protected Boolean enableCheckPoint() {
        return JobConfig.enableCheckPoint;
    }

    /**
     * 设置默认checkpoint时间间隔
     */
    protected Long setCheckpointInterval() {
        return JobConfig.checkPointInterval;
    }

    /**
     * 设置默认checkpoint模式
     */
    protected CheckpointingMode setCheckPointingMode() {
        return CheckpointingMode.EXACTLY_ONCE;
    }

    /**
     * 设置默认checkpoint超时时间
     */
    protected Long setCheckpointTimeout() {
        return JobConfig.checkPointTimeOut;
    }

    /**
     * 设置默认两次CheckPoint中间最小时间间隔
     */
    protected Long setMinPauseBetweenCheckpoints() {
        return JobConfig.checkPointMinPauseBetween;
    }

    /**
     * 设置默认同时允许多少个Checkpoint在做快照
     */
    protected Integer setMaxConcurrentCheckpoints() {
        return JobConfig.checkPointCurrentCheckpoints;
    }

    /**
     * 设置默认checkpoint State类型
     */
    protected String setCheckpointStateType() {
        return JobConfig.checkPointStateType;
    }

    /**
     * 设置默认checkpoint类型
     */
    protected CheckpointConfig.ExternalizedCheckpointCleanup setCheckpointClearStrategy() {
        //删除:DELETE_ON_CANCELLATION; 保留:RETAIN_ON_CANCELLATION; 无检查点:NO_EXTERNALIZED_CHECKPOINTS
        return CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION;
    }

    /**
     * 设置默认重启策略
     */
    protected RestartStrategies.RestartStrategyConfiguration setRestartStrategy() {
        return RestartStrategies.fixedDelayRestart(setRestartAttempts(), setRestartAttemptsInterval());// 固定时间间隔重启
    }

    /**
     * 设置默认重启次数
     */
    protected Integer setRestartAttempts() {
        return JobConfig.checkPointRestartAttempts;
    }

    /**
     * 设置默认重启时间间隔
     */
    protected Long setRestartAttemptsInterval() {
        return JobConfig.checkPointRestartAttemptsInterval;
    }

    /**
     * 设置默认并行度
     */
    protected Integer setDefaultParallelism() {
        return JobConfig.defaultParallelism;
    }

    /**
     * 设置是否开启WaterMaker,0表示禁用,>1启用
     */
    protected void setEnableWaterMarker(StreamExecutionEnvironment env) {
        env.getConfig().setAutoWatermarkInterval(setWaterMarkerInterval());
    }

    /**
     * 设置是否开启WaterMaker,0表示禁用,>1启用
     */
    protected long setWaterMarkerInterval() {
        return 0;
    }


//    /**
//     * 设置kafka消费之默认消费起始位置
//     */
//    protected void setKafkaFromOffsets(FlinkKafkaConsumer<T> consumer) {
//        // 从topic中指定的group上次消费的位置开始消费，必须配置group.id参数
//        consumer.setStartFromGroupOffsets();
//    }
//
//    /**
//     * 获取Kafka消费者
//     *
//     * @param topic       指定topic
//     * @param serialModel 指定序列化方式
//     * @return FlinkKafkaConsumer对象
//     */
//    public FlinkKafkaConsumer<T> getKafkaConsumer(String topic, DeserializationSchema<T> serialModel) {
//
//        try {
//            Properties properties = new Properties();
//            if (JobConfig.enableCheckpoint) {
//
//                properties.setProperty("enable.auto.commit", "false"); // 关闭kafka默认自动提交
//            } else {
//                properties.setProperty("enable.auto.commit", "true"); // 不开启checkpoint,启动自动提交
//            }
//            properties.setProperty("bootstrap.servers", KafkaConfig.bootstrapServers);
//            properties.setProperty("group.id", KafkaConfig.groupId);
//            properties.setProperty("key.deserializer", KafkaConfig.keySerializer);
//            properties.setProperty("value.deserializer", KafkaConfig.valueSerializer);
//            //自动发现kafka的partition变化
//            properties.setProperty("flink.partition-discovery.interval-millis", KafkaConfig.partitionDiscoverMillis.toString());
//
//
//            FlinkKafkaConsumer<T> kafkaConsumer = new FlinkKafkaConsumer<T>(topic, serialModel, properties);
//
//
//            if (JobConfig.enableCheckpoint) {
//                //当 checkpoint 成功时提交 offset 到 kafka
//                kafkaConsumer.setCommitOffsetsOnCheckpoints(true);
//            }
//
//            // 设置kafka消费起始位置
//            setKafkaFromOffsets(kafkaConsumer);
//
////            kafkaConsumer.assignTimestampsAndWatermarks();
//
//            log.info("kafka 消费者配置完成 ...");
//            return kafkaConsumer;
//        } catch (Exception e) {
//            throw new RuntimeException("kafka 消费者配置错误！", e);
//        }
//    }
}
