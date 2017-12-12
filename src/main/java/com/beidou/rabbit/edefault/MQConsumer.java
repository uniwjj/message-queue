package com.beidou.rabbit.edefault;

import com.rabbitmq.client.*;
import java.io.IOException;

/**
 * @author ginger
 * @create 2017-12-12 0:22
 */
public class MQConsumer {
    private final static String QUEUE_NAME = "queue.default";
    private final static String QUEUE_IP = "10.240.193.118";
    private final static int QUEUE_PORT = 5672;
    private final static String QUEUE_USER = "ginger";
    private final static String QUEUE_PWD = "ginger";

    public static void main(String[] argv) throws Exception {
        //打开连接和创建频道，与发送端一样
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QUEUE_IP);
        factory.setPort(QUEUE_PORT);
        factory.setUsername(QUEUE_USER);
        factory.setPassword(QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println("消费者启动完成，等待消息...");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[]
                    body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("consume message: " + message);
            }
        };
        // 指定消费队列
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // // 创建队列消费者
        // QueueingConsumer consumer = new QueueingConsumer(channel);
        // // 指定消费队列
        // channel.basicConsume(QUEUE_NAME, true, consumer);
        // while (true) {
        //     // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
        //     QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        //     String message = new String(delivery.getBody(), "UTF-8");
        //     System.out.println("consume message: " + message);
        // }
    }
}
