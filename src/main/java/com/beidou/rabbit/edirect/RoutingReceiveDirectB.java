package com.beidou.rabbit.edirect;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.*;
import java.io.IOException;

/**
 * @author wangjinjie
 * @create 2017-12-12 22:16
 */
public class RoutingReceiveDirectB {
    // 交换机名称
    private static final String EXCHANGE_NAME = "direct_logs";
    private final static String QUEUE_NAME = "queue.default";
    // 路由关键字
    private static final String[] routingKeys = new String[]{"error"};

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
        // 获取匿名队列名称
        String queueName = channel.queueDeclare().getQueue();
        // 根据路由关键字进行多重绑定
        for (String severity : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, severity);
            System.out.println("B exchange:"+EXCHANGE_NAME+", queue:"+queueName+", BindRoutingKey:" + severity);
        }
        System.out.println("[B] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [B] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

}
