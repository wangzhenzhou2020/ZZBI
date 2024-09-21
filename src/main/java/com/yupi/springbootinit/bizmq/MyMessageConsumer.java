package com.yupi.springbootinit.bizmq;

import com.microsoft.schemas.office.x2006.encryption.STKeyBits;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * spring rabbitmq实验。创建channel，创建consumer，项目一启动 @RabbitListener就自动运行(内部创建connection？)。
 */
@Component
@Slf4j
public class MyMessageConsumer {

    @Autowired
    private SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory;


    /**
     * work_exchange 有两个工作队列，一个是code_queue,一个delayed_queue
     * 自动AUTO方式实现重试若干次后发给死信队列。
     * @param message
     * @param object
     * @param channel
     * @param deliveryTag
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "code_queue",
                    durable = "true"
//                    arguments = {@Argument(name = "x-dead-letter-exchange", value = "dlx-direct-exchange"), // 死信队列
//                            @Argument(name = "x-dead-letter-routing-key", value = "failure")}
            ),
            exchange = @Exchange(value = "work_exchange"),
            key = {"code"}))
//    public void receiveMessage(Message message, Map<String, Object> object,Channel channel,
    public void receiveMessage(Message message, String object,Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) {
        log.info("receiveMessage message = {}", object);
        log.info("receiveMessage message id = {}", message.getMessageProperties().getMessageId());
        throw new RuntimeException("模拟 AUTO 遇到 异常");
    }

    //    // 手动MANUAL的方式不方便实现重试若干次后发给死信队列。
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(
//                    value = "code_queue",
//                    durable = "true",
//                    arguments = {@Argument(name = "x-dead-letter-exchange", value = "dlx-direct-exchange"), // 死信队列
//                            @Argument(name = "x-dead-letter-routing-key", value = "waibao"),
//                            @Argument(name = "x-max-length", value = "5", type = "java.lang.Integer")}
//            ),
//            exchange = @Exchange(value = "code_exchange"),
//            key = {"my_routingKey"}),
//            ackMode = "MANUAL",
//            containerFactory = "simpleRabbitListenerContainerFactory")  // basicQos
//    public void receiveMessage(Map<String, Object> object, Channel channel,
//                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
//    ) {
//
//        log.info("receiveMessage message = {}", object);
//        try {
//            if (true) {
//                Thread.sleep(1000);
//            } else {
//                Thread.sleep(30000);
//            }
////            channel.basicAck(deliveryTag, false);  // 这三个是手动回传ack
////            channel.basicReject(deliveryTag, true);  //false ：抛弃or重试一定次数后推给死信队列？
//            channel.basicNack(deliveryTag, false, true);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        throw new RuntimeException("receiveMessage"); // 异常？不是手动reject吗？
//    }

//    // afterTTL 死信队列
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "after_ttl_dlx_queue", durable = "true"),
//            exchange = @Exchange(value = "dlx-direct-exchange", type = ExchangeTypes.DIRECT),
//            key = {"after_ttl"})
//    )
//    public void receiveDeadAfterTTLMessage(Message message, String string, Channel channel,
//                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
//        log.info("receiveDeadAfterTTLMessage message id = {}", message.getMessageProperties().getMessageId());
//        log.info("receiveDeadAfterTTLMessage string = {}", string);
////        log.info("receiveDeadMessage message body = {}", message.getBody());
//    }
}
