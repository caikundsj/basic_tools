package cn.comm.configer;

import java.io.Serializable;

/**
 *   JOB 任务环境配置项
 */
public class JobConfig implements Serializable {
    private static final long serialVersionUID = -1143387026533611296L;

    /**
     * 任务运行并行度
     */
    public static Integer defaultParallelism;

    /**
     * 任务运行并行度
     */
    public static Integer sinkParallelism;

    /**
     * 任务名前缀
     */
    public static String jobNamePrefix;

    /**
     * 是否开启savepoint
     */
    public static Boolean enableSavePoint;

    /**
     * 是否开启checkpoint
     */
    public static Boolean enableCheckPoint;

    /**
     * checkpoint时间间隔
     */
    public static Long checkPointInterval;

    /**
     * checkpoint类型
     */
    public static String checkPointStateType;

    /**
     * checkpoint路径
     */
    public static String checkPointPath;

    /**
     * savepoint路径
     */
    public static String savePointPath;

    /**
     * checkpoint超时时间
     */
    public static Long checkPointTimeOut;

    /**
     * 两次CheckPoint中间最小时间间隔（单位：毫秒）
     */
    public static Long checkPointMinPauseBetween;

    /**
     * 同时允许多少个checkpoint做快照
     */
    public static Integer checkPointCurrentCheckpoints = 1;

    /**
     * 默认使用FixedDelayRestartStrategy重启策略的重试次数(3次)
     */
    public static Integer checkPointRestartAttempts;

    /**
     * 默认使用FixedDelayRestartStrategy重启策略的的每次重启时间间隔
     */
    public static Long checkPointRestartAttemptsInterval;

    /**
     * table planner规划器 默认无
     */
    public static String plannerMode;
    /**
     * table 运行时环境 默认streaming
     */
    public static String runtimeMode;
}
