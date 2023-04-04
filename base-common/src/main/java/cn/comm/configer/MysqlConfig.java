package cn.comm.configer;

import java.io.Serializable;

/**
 *   MYSQL 环境配置项
 */
public class MysqlConfig implements Serializable {
    private static final long serialVersionUID = 6248517349213721967L;

    /**
     * url地址
     */
    public static String url;

    /**
     * driver驱动
     */
    public static String driver;

    /**
     * 用户
     */
    public static String username;

    /**
     * 密码
     */
    public static String password;

}
