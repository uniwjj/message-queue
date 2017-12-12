package com.beidou.rabbit.edefault;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 直接方式（默认），使用匿名交换机""
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
        // 指定一个队列，已经定义的队列再次定义无效，幂次原理，RabbitMQ重启队列也不会消失
        // 第一个参数为队列名称
        // 第二个参数为标明队列是否持久化，false为瞬时队列，true为持久化队列
        // 第三个参数为标识是否为独占队列，false非独占队列，true独占队列
        // 第四个参数为标识队列不用时是否删除，false不删除，true删除
        // 第五个参数为其他参数
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
