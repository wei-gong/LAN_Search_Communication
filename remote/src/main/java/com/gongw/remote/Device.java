package com.gongw.remote;

/**
 * 局域网中的设备
 */
public class Device {
    //ip地址
    private String ip;
    //端口号
    private int port;
    //唯一id
    private String uuid;

    public Device(String ip, int port, String uuid) {
        super();
        this.ip = ip;
        this.port = port;
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
