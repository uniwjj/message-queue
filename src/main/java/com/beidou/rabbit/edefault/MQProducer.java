package com.beidou.rabbit.edefault;

import com.beidou.rabbit.config.QueueConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 直接方式（默认），使用匿名交换机""
 * 消息直接发送到consumer绑定的队列，当有多个consumer绑定同一个队列时，多个消费者轮询消费消息
 * 消息在被用户消费前不会都是
 *
 * 默认，向指定的队列发送消息，消息只会被一个consumer处理,多个消费者消息会轮训处理,消息发送时如果没有consumer，消息不会丢失
 *
 * @author ginger
 * @create 2017-12-12 0:20
 */
public class MQProducer {
    private final static String QUEUE_NAME = "queue.default";

    public static void main(String[] argv) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QueueConfig.QUEUE_IP);
        factory.setPort(QueueConfig.QUEUE_PORT);
        factory.setUsername(QueueConfig.QUEUE_USER);
        factory.setPassword(QueueConfig.QUEUE_PWD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 指定一个队列，已经定义的队列再次定义无效，幂次原理，RabbitMQ重启队列也不会消失
        // 第一个参数为队列名称
        // 第二个参数为标明队列是否持久化，false为瞬时队列，true为持久化队列
        // 第三个参数为标识是否为独占队列，独占队列只允许一个connection使用，其他connection使用将会抛出异常，
        // false非独占队列，true独占队列
        // 第四个参数为标识队列不用时是否删除，false不删除，true删除，没有连接，没有消息
        // 第五个参数为其他参数
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        String message = "hello world!";
        Long start = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++) {
            // 发布一条消息
            // 第一个参数：exchange名字，空会使用默认exchange，属于direct exchange
            // 第二个参数：routing_key，路由关键字，指定路由
            // 第三个参数：消息的其他属性，此处配置了消息持久化
            // 第四个参数：消息体
            // RabbitMQ默认有一个exchange，叫default exchange，它用一个空字符串表示，它是direct exchange类型，
            // 任何发往这个exchange的消息都会被路由到routing key的名字对应的队列上，如果没有对应的队列，则消息会被丢弃
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
