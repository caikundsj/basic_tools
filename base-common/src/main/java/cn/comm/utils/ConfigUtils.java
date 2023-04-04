package cn.comm.utils;


import cn.comm.configer.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 加载配置文件工具
 */
public class ConfigUtils {
    private final static Logger log = LoggerFactory.getLogger(ConfigUtils.class);

    private static final Config rootConfig = ConfigFactory.load("env-init");
    private static final String configType;

    static {
        configType = rootConfig.getString("init-config-type");
    }

    /**
     * load初始化加载加载
     */
    public static void initLoadConfig() {
        // 根据环境标识，加载不同环境配置
        Config eleConfig = ConfigFactory.load("conf/" + configType + "-application");
        loadKafkaConfig(eleConfig);
        loadJobConfig(eleConfig);
        loadMysqlConfig(eleConfig);
        loadHiveConfig(eleConfig);
        loadClickHouseConfig(eleConfig);
        log.info(configType + " >> 加载配置文件初始化完成");
    }

    /**
     * 加载Kafka相关配置
     *
     * @param pConfig kafka元素节点父节点
     */
    private static void loadKafkaConfig(Config pConfig) {
        Config kafkaConfig = pConfig.getConfig("kafka");
        KafkaConfig.bootstrapServers = kafkaConfig.getString("bootstrap.servers");
        KafkaConfig.batchSize = kafkaConfig.getLong("batch.size");
        KafkaConfig.lingerMs = kafkaConfig.getInt("linger.ms");
        KafkaConfig.bufferMemory = kafkaConfig.getLong("buffer.memory");
        KafkaConfig.keySerializer = kafkaConfig.getString("key.serializer");
        KafkaConfig.valueSerializer = kafkaConfig.getString("value.serializer");
        KafkaConfig.groupId = kafkaConfig.getString("group.id");
        KafkaConfig.partitionDiscoverMillis = kafkaConfig.getLong("partition.discover.millis");
        Config topicConfig = kafkaConfig.getConfig("topic");
        KafkaConfig.measuringTopic = topicConfig.getString("MeasuringTopic");
    }

    /**
     * 加载当前任务相关配置
     *
     * @param pConfig job元素节点父节点
     */
    private static void loadJobConfig(Config pConfig) {
        Config jobConfig = pConfig.getConfig("job");
        JobConfig.jobNamePrefix = jobConfig.getString("name.prefix");
        JobConfig.defaultParallelism = jobConfig.getInt("default.parallelism");
        JobConfig.sinkParallelism = jobConfig.getInt("sink.parallelism");

        Config checkPointConfig = jobConfig.getConfig("checkpoint");
        JobConfig.enableCheckPoint = checkPointConfig.getBoolean("enable");
        JobConfig.checkPointInterval = checkPointConfig.getLong("interval");
        JobConfig.checkPointStateType = checkPointConfig.getString("type");
        JobConfig.checkPointPath = checkPointConfig.getString("path");
        JobConfig.checkPointTimeOut = checkPointConfig.getLong("timeout");
        JobConfig.checkPointMinPauseBetween = checkPointConfig.getLong("min.pause.between");
        JobConfig.checkPointCurrentCheckpoints = checkPointConfig.getInt("current.checkpoints");
        JobConfig.checkPointRestartAttempts = checkPointConfig.getInt("restart.attempts.times");
        JobConfig.checkPointRestartAttemptsInterval = checkPointConfig.getLong("restart.attempts.interval");

        Config savePointConfig = jobConfig.getConfig("savepoint");
        JobConfig.enableSavePoint = savePointConfig.getBoolean("enable");
        JobConfig.savePointPath = savePointConfig.getString("path");

        Config tableConfig = jobConfig.getConfig("table");
        JobConfig.plannerMode = tableConfig.getString("planner");
        JobConfig.runtimeMode = tableConfig.getString("runtime");

    }

    /**
     * 加载Mysql相关配置
     *
     * @param pConfig job元素节点父节点
     */
    private static void loadMysqlConfig(Config pConfig) {
        Config mysqlConfig = pConfig.getConfig("mysql");
        MysqlConfig.url = mysqlConfig.getString("url");
        MysqlConfig.driver = mysqlConfig.getString("driver");
        MysqlConfig.username = mysqlConfig.getString("username");
        MysqlConfig.password = mysqlConfig.getString("password");
    }

    /**
     * 加载Hive相关配置
     *
     * @param pConfig job元素节点父节点
     */
    private static void loadHiveConfig(Config pConfig) {
        Config hiveConfig = pConfig.getConfig("hiveCatalog");
        HiveConfig.catalogName = hiveConfig.getString("catalogName");
        HiveConfig.dataBase = hiveConfig.getString("dataBase");
        HiveConfig.hiveConf = hiveConfig.getString("hiveConf");
        HiveConfig.hiveVersion = hiveConfig.getString("hiveVersion");
    }

    /**
     * 加载ClickHouse相关配置
     *
     * @param pConfig job元素节点父节点
     */
    private static void loadClickHouseConfig(Config pConfig) {
        Config clickhouseConfig = pConfig.getConfig("clickhouse");
        ClickHouseConfig.curl = clickhouseConfig.getString("url");
        ClickHouseConfig.databaseName = clickhouseConfig.getString("database-name");
        ClickHouseConfig.username = clickhouseConfig.getString("username");
        ClickHouseConfig.password = clickhouseConfig.getString("password");
    }

}