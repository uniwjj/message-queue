package com.beidou.rabbit.edefault;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 直接方式（默认）
 * 消息直接发送到consumer绑定的队列，当有多个consumer绑定同一个队列时，多个消费者轮询消费消息
 * 消息在被用户消费前不会都是
 * @author ginger
 * @create 2017-12-12 0:20
 */
public class MQProducer {
    private final static String QUEUE_NAME = "queue.default";
    private final static String QUEUE_IP = "10.240.193.118";
    private final static int QUEUE_PORT = 5672;
    private final static String QUEUE_USER = "ginger";
    private final static String QUEUE_PWD = "ginger";

    public static void main(String[] argv) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QUEUE_IP);
        factory.setPort(QUEUE_PORT);
        factory.setUsername(QUEUE_USER);
        factory.setPassword(QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 指定一个队列
        // channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 如QUEUE_NAME是一个transient的queue，第二个参数必须是false；重启rabbit后QUEUE_NAME会被删除掉
        // 如QUEUE_NAME是一个durability的queue，第二个参数必须是true；重启rabbit后QUEUE_NAME不会被删除掉
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        String message = "hello world!";
        Long start = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++) {
            // 将消息保存起来，重启rabbit后待消费的消息不会被删除
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, (message+i).getBytes());
            // 不保存消息，重启rabbit后待消费的消息都将丢失
            // channel.basicPublish("", QUEUE_NAME, null, (message+i).getBytes());
        }
        System.out.println("发送完成："+(System.currentTimeMillis() - start));
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
