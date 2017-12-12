package com.beidou.rabbit.edefault;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.*;
import java.io.IOException;

/**
 * @author ginger
 * @create 2017-12-12 0:22
 */
public class MQConsumer {
    private final static String QUEUE_NAME = "queue.default";

    public static void main(String[] argv) throws Exception {
        //打开连接和创建频道，与发送端一样
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

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

                // 回复ack包，如果不回复，消息不会在服务器删除
                channel.basicAck(envelope.getDeliveryTag(), false);
                // 拒绝消息，服务器会删除该消息
                // channel.basicReject();
                // 拒绝消息，服务器会重新分配该消息
                // channel.basicNack();
            }};
        // 为channel声明一个消费者，服务器会推送消息
        // 第一个参数：队列名
        // 第二个参数：是否主动发送ack包，不发送ack消息会持续在服务端保存，直到收到ack，true自动ack，false手动ack
        // 第三个参数：消费者
        channel.basicConsume(QUEUE_NAME, false, consumer);
        // 使用该函数可以主动取服务器检索是否有新的消息，而不是等服务器推送
        // channel.basicGet();



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
