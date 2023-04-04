package cn.comm.configer;

import java.io.Serializable;

/**
 * HIVE  环境配置项
 */
public class HiveConfig implements Serializable {
    private static final long serialVersionUID = -4805098728077656030L;

    /**
     * catalog 名称
     */
    public static String catalogName;

    /**
     * 数据库环境
     */
    public static String dataBase;

    /**
     * 配置文件位置
     */
    public static String hiveConf;

    /**
     * 配置版本
     */
    public static String hiveVersion;
}
