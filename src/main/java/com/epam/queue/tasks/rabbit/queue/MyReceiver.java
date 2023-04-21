package com.epam.queue.tasks.rabbit.queue;

import com.epam.queue.tasks.interfaces.Receiver;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MyReceiver implements Receiver {

    private String queueName;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private DefaultConsumer consumer;

    public MyReceiver() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
    }

    @Override
    public void setQueueName(String name) {
        queueName = name;
    }

    @Override
    public void receive() {
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);

            consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received message: " + message);
                }
            };

            channel.basicConsume(queueName, true, consumer);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}