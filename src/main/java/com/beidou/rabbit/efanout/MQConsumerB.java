package com.beidou.rabbit.efanout;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author wangjinjie
 * @create 2017-12-12 21:30
 */
public class MQConsumerB {
    private final static String QUEUE_IP = "10.240.193.118";
    private final static int QUEUE_PORT = 5672;
    private final static String QUEUE_USER = "ginger";
    private final static String QUEUE_PWD = "ginger";
    // 交换机名称
    private final static String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QUEUE_IP);
        factory.setPort(QUEUE_PORT);
        factory.setUsername(QUEUE_USER);
        factory.setPassword(QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个非持久化、独立、自动删除的队列
        String queueName = channel.queueDeclare().getQueue();
        // 将队列与交换机绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}