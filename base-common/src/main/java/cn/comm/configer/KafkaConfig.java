package cn.comm.configer;

import java.io.Serializable;

/**
 *  KAFKA 配置项
 */
public class KafkaConfig implements Serializable {
    private static final long serialVersionUID = 1323699199460089731L;

    /**
     * 集群地址
     */
    public static String bootstrapServers;

    /**
     * Batch大小
     */
    public static Long batchSize;

    /**
     * Batch过多久发送出去
     */
    public static Integer lingerMs;

    /**
     * 缓存大小
     */
    public static Long bufferMemory;

    /**
     * key默认序列化方式
     */
    public static String keySerializer;

    /**
     * value默认序列化方式
     */
    public static String valueSerializer;

    /**
     * 消费者组
     */
    public static String groupId;

    /**
     * 间隔多久（interval）获取一次 kafka 的元数据
     */
    public static Long partitionDiscoverMillis;

    /**
     * 量测数据topic
     */
    public static String measuringTopic;
}
