package com.beidou.rabbit.edirect;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * direct exchange
 * 向所有绑定了相应routing key的队列发送消息,
 * 如果producer在发布消息时没有consumer在监听，消息将被丢弃，
 * 如果有多个consumer监听了相同的routing key，则他们都会收到消息
 *
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
        // 第一个参数：交换机名称
        // 第二个参数：交换机类型
        // 第三个参数：交换机持久性，如果为true则服务器重启时不会丢失
        // 第四个参数：交换机在不被使用时是否删除
        // 第五个参数：交换机的其他属性
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
