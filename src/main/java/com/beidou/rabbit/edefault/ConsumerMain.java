package com.beidou.rabbit.edefault;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author wangjinjie
 * @create 2017-12-12 13:45
 */
public class ConsumerMain {
    private final static String QUEUE_NAME = "queue.default";
    private final static String QUEUE_IP = "10.240.193.118";
    private final static int QUEUE_PORT = 5672;
    private final static String QUEUE_USER = "ginger";
    private final static String QUEUE_PWD = "ginger";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QUEUE_IP);
        factory.setPort(QUEUE_PORT);
        factory.setUsername(QUEUE_USER);
        factory.setPassword(QUEUE_PWD);

        Connection connection = factory.newConnection();

        Runnable task1 = new ConsumerTask(connection, QUEUE_NAME, "张三");
        Runnable task2 = new ConsumerTask(connection, QUEUE_NAME, "李四");
        Runnable task3 = new ConsumerTask(connection, QUEUE_NAME, "王五");
        Runnable task4 = new ConsumerTask(connection, QUEUE_NAME, "马六");
        new Thread(task1).start();
        new Thread(task2).start();
        new Thread(task3).start();
        new Thread(task4).start();
    }
}
