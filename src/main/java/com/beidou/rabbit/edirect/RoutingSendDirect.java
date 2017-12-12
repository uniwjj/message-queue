package com.beidou.rabbit.edirect;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author wangjinjie
 * @create 2017-12-12 22:06
 */
public class RoutingSendDirect {
    // 交换机名称
    private static final String EXCHANGE_NAME = "direct_logs";
    private final static String QUEUE_NAME = "queue.default";
    // 路由关键字
    private static final String[] routingKeys = new String[]{"info" ,"warning", "error"};

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        // 发送消息
        for(String severity : routingKeys){
            String message = "Send the message level : " + severity;
            channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
            System.out.println(" [S] Sent '" + severity + "':'" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
