package cn.train.base;

/**
 *   flink任务执行规范
 */
public interface IBaseRun {

    /**
     *   任务逻辑入口
     */
    void doMain() throws Exception;

}
