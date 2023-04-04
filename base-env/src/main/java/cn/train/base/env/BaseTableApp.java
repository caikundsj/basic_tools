package cn.train.base.env;


import cn.comm.configer.JobConfig;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;


/**
 * Flink Table 环境配置项
 */
public abstract class BaseTableApp<T> extends BaseStreamApp<T> {

    /**
     * Table 流执行环境
     */
    protected StreamTableEnvironment tableStreamEnv = getStreamTableEnv();

    /**
     * 批执行环境
     */
    protected static ExecutionEnvironment batchEnv = getExecutionEnvironment();


    private static ExecutionEnvironment getExecutionEnvironment() {
        return ExecutionEnvironment.getExecutionEnvironment();
    }




    /**
     * 获取Table中流方式的执行环境
     *
     * @return StreamTableEnvironment
     */
    private StreamTableEnvironment getStreamTableEnv() {
        TableEnvironment tableEnvironment = TableEnvironment.create(envSettings());
        //StreamTableEnvironment tableEnvironment1 = StreamTableEnvironment.create(env, envSettings());
        return (StreamTableEnvironment) tableEnvironment;
    }



    /**
     * 指定EnvironmentSettings各个参数
     *
     * @return EnvironmentSettings
     */
    private EnvironmentSettings envSettings() {
        EnvironmentSettings.Builder builder = EnvironmentSettings.newInstance();
        //setPlanner(builder);
        setRuntimeMode(builder);
        return builder.build();
    }


    /**
     * 设置planner方式
     *
     * @param builder builder
     */
    protected void setPlanner(EnvironmentSettings.Builder builder) {
        switch (JobConfig.plannerMode) {
            case "OLD":
                builder.useOldPlanner();//1.14以下可用
                break;
            case "BLINK":
                builder.useBlinkPlanner();
                break;
            case "ANY":
                builder.useAnyPlanner();
                break;
            default:
        }
    }

    /**
     * 设置流还是批处理方式
     *
     * @param builder builder
     */
    protected void setRuntimeMode(EnvironmentSettings.Builder builder) {
        switch (JobConfig.runtimeMode) {
            case "BATCH":
                builder.inBatchMode();
                break;
            case "STREAMING":
            default:
                builder.inStreamingMode();
        }
    }


}
