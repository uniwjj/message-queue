package com.beidou.rabbit.erpc;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.*;

import java.util.UUID;

/**
 * @author ginger
 * @create 2017-12-13 1:53
 */
public class RPCClient {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

        connection = factory.newConnection();
        channel = connection.createChannel();

        // 拿到一个匿名(并非真的匿名，拿到了一个随机生成的队列名)的队列，作为replyQueue。
        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public String call(String message) throws Exception{
        String response = null;
        String corrId = UUID.randomUUID().toString();//拿到一个UUID

        // 封装correlationId和replyQueue属性
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        // 推消息，并加上之前封装好的属性
        channel.basicPublish("", requestQueueName, props, message.getBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            //检验correlationId是否匹配，确定是不是这次的请求
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody(),"UTF-8");
                break;
            }
        }
        return response;
    }

    public void close() throws Exception {
        connection.close();
    }

    public static void main(String[] argv) {
        RPCClient fibonacciRpc = null;
        String response = null;
        try {
            fibonacciRpc = new RPCClient();

            System.out.println(" [x] Requesting fib(30)");
            response = fibonacciRpc.call("30");
            System.out.println(" [.] Got '" + response + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fibonacciRpc != null) {
                try {
                    fibonacciRpc.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

}
