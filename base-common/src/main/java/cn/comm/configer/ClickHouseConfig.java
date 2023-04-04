package cn.comm.configer;

import java.io.Serializable;

public class ClickHouseConfig implements Serializable {
    private static final long serialVersionUID = -852699818284876890L;

    /**
     * url地址
     */
    public static String curl;

    /**
     * databaseName地址
     */
    public static String databaseName;

    /**
     * 用户
     */
    public static String username;

    /**
     * 密码
     */
    public static String password;
}
