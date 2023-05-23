package cn.train.base.env;


import cn.comm.configer.JobConfig;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;


/**
 * Flink Table 环境配置项
 */
public abstract class BaseTableApp<T> extends BaseStreamApp {

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
        return StreamTableEnvironment.create(envSettings());
    }


    /**
     * 指定EnvironmentSettings各个参数
     *
     * @return EnvironmentSettings
     */
    private StreamExecutionEnvironment envSettings() {
//        EnvironmentSettings.Builder builder = EnvironmentSettings.newInstance();
        //setPlanner(builder);
        setRuntimeMode();
        return env;
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
     */
    protected void setRuntimeMode() {
        switch (JobConfig.runtimeMode) {
            case "BATCH":
                env.setRuntimeMode(RuntimeExecutionMode.BATCH);
                break;
            case "STREAMING":
                env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
                break;
            default:
                env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        }
    }


}
