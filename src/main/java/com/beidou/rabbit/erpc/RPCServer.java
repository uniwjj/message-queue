package com.beidou.rabbit.erpc;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.*;

/**
 * @author ginger
 * @create 2017-12-13 1:53
 */
public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n ==0) return 0;
        if (n == 1) return 1;
        return fib(n-1) + fib(n-2);
    }

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        // 一次只接收一条消息
        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 开启消息应答机制
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        System.out.println(" [x] Awaiting RPC requests");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            // 拿到correlationId属性
            AMQP.BasicProperties props = delivery.getProperties();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId())
                    .build();
            String message = new String(delivery.getBody(),"UTF-8");
            int n = Integer.parseInt(message);

            System.out.println(" [.] fib(" + message + ")");
            String response = "" + fib(n);

            //拿到replyQueue，并绑定为routing key，发送消息
            channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));
            //返回消息确认信息
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }

        // channel.close();
        // connection.close();
    }

}
