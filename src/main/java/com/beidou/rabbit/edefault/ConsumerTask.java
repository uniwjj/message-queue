package com.beidou.rabbit.edefault;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author wangjinjie
 * @create 2017-12-12 13:37
 */
public class ConsumerTask implements Runnable {
    private Connection connection;
    private String queueName;
    private String consumerName;

    public ConsumerTask(Connection connection, String queueName, String consumerName) {
        this.connection = connection;
        this.queueName = queueName;
        this.consumerName = consumerName;
    }

    @Override
    public void run() {
        try {
            Channel channel = connection.createChannel();
            // 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
            channel.queueDeclare(queueName, true, false, false, null);
            System.out.println("消费者启动完成，等待消息...");
            // 每次从队列中取出1条消息
            channel.basicQos(1);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[]
                        body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(consumerName + " consume message: " + message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        // 消息处理失败，未处理完
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };
            // 消息处理成功
            channel.basicConsume(queueName, false, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
