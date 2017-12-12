package com.beidou.rabbit.etopic;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * topic exchange
 *
 * 与direct模式有类似之处，都使用routing key作为路由，
 * 不同之处在于direct模式只能指定固定的字符串，而topic可以指定一个字符串模式
 * #匹配若干个单词，*匹配一个单词
 *
 * @author wangjinjie
 * @create 2017-12-12 22:26
 */
public class TopicSend {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明一个匹配模式的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // 待发送的消息
        String[] routingKeys = new String[]{"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox",
                "lazy.brown.fox", "quick.brown.fox", "quick.orange.male.rabbit", "lazy.orange.male.rabbit"};

        // 发送消息
        for(String severity :routingKeys){
            String message = "From "+severity+" routingKey' s message!";
            channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
            System.out.println("TopicSend [x] Sent '" + severity + "':'" + message + "'");
        }

        channel.close();
        connection.close();
    }

}
