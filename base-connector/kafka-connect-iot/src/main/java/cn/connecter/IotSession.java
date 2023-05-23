package cn.connecter;

import org.apache.iotdb.session.Session;

public class IotSession {
    public static void main(String[] args) {


        // 指定一个可连接节点
        Session build = new Session.Builder()
                .host("10.10.5.150")
                .port(6667)
                .build();

    }
}