package com.beidou.rabbit.config;

/**
 * @author ginger
 * @create 2017-12-12 23:27
 */
public interface QueueConfig {
    /**
     * 队列IP地址
     */
    String QUEUE_IP = "10.240.193.118";

    /**
     * 队列端口号
     */
    int QUEUE_PORT = 5672;

    /**
     * 用户名
     */
    String QUEUE_USER = "ginger";

    /**
     * 密码
     */
    String QUEUE_PWD = "ginger";


}