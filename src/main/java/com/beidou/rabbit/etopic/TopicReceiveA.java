package com.beidou.rabbit.etopic;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author wangjinjie
 * @create 2017-12-12 22:26
 */
public class TopicReceiveA {
    private final static String QUEUE_IP = "10.240.193.118";
    private final static int QUEUE_PORT = 5672;
    private final static String QUEUE_USER = "ginger";
    private final static String QUEUE_PWD = "ginger";

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QUEUE_IP);
        factory.setPort(QUEUE_PORT);
        factory.setUsername(QUEUE_USER);
        factory.setPassword(QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明一个匹配模式的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        // 路由关键字
        String[] routingKeys = new String[]{"*.orange.*"};
        // 绑定路由关键字
        for (String bindingKey : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
            System.out.println("ReceiveLogsTopic1 exchange:"+EXCHANGE_NAME+", queue:"+queueName+", BindRoutingKey:" + bindingKey);
        }
        System.out.println("ReceiveLogsTopic1 [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("ReceiveLogsTopic1 [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

}