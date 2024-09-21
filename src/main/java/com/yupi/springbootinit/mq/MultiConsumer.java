package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * 简单多队列
 */
public class MultiConsumer {
    private final static String QUEUE_NAME = "multi_queue";

    /* 被动确认消息 */
//    public static void main(String[] argv) throws Exception {
//        // 创建连接
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        // 创建队列                      持久化，确保队列在 RabbitMQ 节点重启后仍然存在。
//        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//        // 定义了如何处理消息
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//
//            System.out.println(" [x] Received '" + message + "'");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                System.out.println(" [x] Done");
//            }
//        };
//        boolean autoAck = true; // consumer手动确认消息已处理，这样队列中的消息才会被删除。
//        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
//    }

    /* 主动确认消息 */
//    public static void main(String[] argv) throws Exception {
//        // 创建连接
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        // 创建队列
//        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//        // 定义了如何处理消息
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//
//            System.out.println(" [x] Received '" + message + "'");
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                System.out.println(" [x] Done");
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            }
//        };
//        boolean autoAck = false; // consumer手动确认消息已处理，这样队列中的消息才会被删除。
//        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
//    }

    /* 队列根据 ack 来给Connection 发送消息 */
//    public static void main(String[] argv) throws Exception {
//        // 创建连接
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        // 创建队列
//        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//        channel.basicQos(1); // 队列在没有收到channel 的 ack确认前，不会向该worker发送新的消息。（该worker压力大）
//        // 定义了如何处理消息
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//
//            System.out.println(" [x] Received '" + message + "'");
//            try {
//                if (message.length()>2){
//                    Thread.sleep(60000);
//                }else {
//                    Thread.sleep(10000);
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                System.out.println(" [x] Done");
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            }
//        };
//        boolean autoAck = false; // consumer手动确认消息已处理，这样队列中的消息才会被删除。
//        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
//    }

    /*多个Connection，每个Connection一个Consumer*/
    public static void main(String[] argv) throws Exception {
        // 创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            Channel channel = connection.createChannel();
            // 创建队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.printf(" [*] channel %d Waiting for messages. To exit press CTRL+C%n", i);
            channel.basicQos(1);  // 根据ack来确认是否往consumer发送消息。
            // 定义了如何处理消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                System.out.printf(" [x] channel %d  Received %s %n", finalI, message);
                try {
                    if (message.length() > 2) {
                        Thread.sleep(20000);
                    } else {
                        Thread.sleep(3000);
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (InterruptedException e) {
                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
                    throw new RuntimeException(e);
                } finally {
                    System.out.printf(" [x] channel %d  Done%n", finalI);
                }
            };
            boolean autoAck = false;
            channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
            });
        }
    }

    /**/
}