package com.beidou.rabbit.efanout;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 扇出交换机，只有消费者先运行才能收到消息
 * 发布订阅模式
 *
 * @author wangjinjie
 * @create 2017-12-12 21:29
 */
public class MQProducer {

    // 交换机名称
    private final static String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明一个交换机，为扇出类型，所有与该交换机绑定的队列都会收到消息
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // 分发消息
        for(int i = 0 ; i < 10; i++){
            String message = "Hello World! " + i;
            // 第一个参数为交换机名称
            // 第二个参数为routing_key，路由键
            // 第三个参数为消息属性，包括消息是否持久化，即重启服务器会不会消失
            // 第四个参数为消息字节数组
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
        connection.close();
    }

}
